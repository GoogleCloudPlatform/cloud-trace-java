// Copyright 2016 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.trace.guice.annotation;

import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.annotation.Label;
import com.google.cloud.trace.annotation.Name;
import com.google.cloud.trace.annotation.Option;
import com.google.cloud.trace.annotation.Span;
import com.google.cloud.trace.core.Labels;
import com.google.cloud.trace.core.StackTrace;
import com.google.cloud.trace.core.StartSpanOptions;
import com.google.cloud.trace.core.ThrowableStackTraceHelper;
import com.google.cloud.trace.core.TraceContext;
import com.google.common.base.CaseFormat;
import com.google.inject.Provider;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.util.Map;

public class TracerSpanInterceptor implements MethodInterceptor {
  private final Provider<Tracer> tracerProvider;
  private final Provider<Map<String, Labeler>> labelerMapProvider;
  private final String labelHost;

  public TracerSpanInterceptor(
      Provider<Tracer> tracerProvider,
      Provider<Map<String, Labeler>> labelerMapProvider,
      String labelHost) {
    this.tracerProvider = tracerProvider;
    this.labelerMapProvider = labelerMapProvider;
    this.labelHost = labelHost;
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Span span = invocation.getMethod().getAnnotation(Span.class);

    Labels.Builder labelsBeforeCallBuilder = Labels.builder();

    if (span.callLabels() == Option.TRUE) {
      labelsBeforeCallBuilder
          .add("g.co/call/class",
              invocation.getMethod().getDeclaringClass().getName())
          .add("g.co/call/method",
              invocation.getMethod().getName())
          .add("g.co/call/package",
              invocation.getMethod().getDeclaringClass().getPackage().getName());
    }
    if (span.entry()) {
      labelsBeforeCallBuilder.add("/agent", "cloud-trace-java/0.1");
    }

    String methodName;
    Name name = invocation.getMethod().getAnnotation(Name.class);
    if (name != null) {
      methodName = name.value();
    } else {
      String overrideName = null;
      for (int i = 0; i < span.labels().length; i++) {
        Labeler labeler = labelerMapProvider.get().get(span.labels()[i]);
        if (labeler != null) {
          labeler.addLabelsBeforeCall(labelsBeforeCallBuilder);
          String labelerName = labeler.overrideName();
          if (labelerName != null) {
            overrideName = labelerName;
          }
        }
      }
      if (overrideName != null) {
        methodName = overrideName;
      } else {
        methodName = getMethodName(invocation);
      }
    }

    Boolean enableTrace = span.trace().getBooleanValue();
    Boolean enableStackTrace = span.stackTrace().getBooleanValue();
    Tracer tracer = tracerProvider.get();
    TraceContext traceContext = tracer.startSpan(methodName, new StartSpanOptions()
        .setEnableTrace(enableTrace).setEnableStackTrace(enableStackTrace));
    boolean stackTraceEnabled = traceContext.getHandle().getCurrentSpanContext().getTraceOptions()
        .getStackTraceEnabled();

    boolean labelAllParams = span.labelAll();
    String labelPrefix;
    if (span.labelPrefix().equals("/")) {
      labelPrefix = getMethodLabelPrefix(invocation);
    } else {
      labelPrefix = span.labelPrefix();
    }
    addParameterAnnotations(
        invocation, labelsBeforeCallBuilder, labelAllParams, labelHost + labelPrefix);

    Labels labelsBeforeCall = labelsBeforeCallBuilder.build();
    if (labelsBeforeCall.getLabels().size() > 0) {
      tracer.annotateSpan(traceContext, labelsBeforeCall);
    }

    Labels.Builder labelsAfterCallBuilder = Labels.builder();
    // This won't be returned if an exception is throw, so I'll set it to null here to keep the
    // compiler happy.
    Object result = null;
    // This should be set below so the stack frame will be on the same line as the invocation, but
    // the compiler complains if I don't initialize this.
    Throwable throwable = new Exception();
    try {
      throwable = new Exception(); result = invocation.proceed();
    } catch (Throwable t) {
      if (span.callLabels() == Option.TRUE) {
        labelsAfterCallBuilder
            .add("g.co/exception/class", t.getClass().getName())
            .add("g.co/exception/message", t.getMessage());
      }
    } finally {
      if (stackTraceEnabled) {
        StackTrace.Builder builder = StackTrace.builder();
        builder.add(invocation.getMethod().getDeclaringClass().getName(),
            invocation.getMethod().getName(), null, null, null);
        ThrowableStackTraceHelper.addFrames(builder, throwable);
        tracer.setStackTrace(traceContext, builder.build());
      }

      for (int i = span.labels().length; i > 0; i--) {
        Labeler labeler = labelerMapProvider.get().get(span.labels()[i - 1]);
        if (labeler != null) {
          labeler.addLabelsAfterCall(labelsAfterCallBuilder);
        }
      }
      Labels labelsAfterCall = labelsAfterCallBuilder.build();
      if (labelsAfterCall.getLabels().size() > 0) {
        tracer.annotateSpan(traceContext, labelsAfterCall);
      }
      tracer.endSpan(traceContext);
    }
    return result;
  }

  private String getMethodName(MethodInvocation invocation) {
    return String.format("%s.%s", invocation.getMethod().getDeclaringClass().getSimpleName(),
        invocation.getMethod().getName());
  }

  private String getMethodLabelPrefix(MethodInvocation invocation) {
    String className = invocation.getMethod().getDeclaringClass().getSimpleName();
    String methodName = invocation.getMethod().getName();
    if (className.isEmpty()) {
      return String.format("/%s", convertJavaName(methodName));
    } else {
      return String.format("/%s/%s", convertJavaName(className), convertJavaName(methodName));
    }
  }

  private String convertJavaName(String name) {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
  }

  private void addParameterAnnotations(MethodInvocation invocation,
      Labels.Builder labelsBuilder, boolean labelAllParams, String labelPrefix) {
    Annotation[][] annotationsArray = invocation.getMethod().getParameterAnnotations();
    for (int i = 0; i < annotationsArray.length; i++) {
      Label label = null;
      Name name = null;
      for (Annotation annotation : annotationsArray[i]) {
        if (annotation.annotationType() == Label.class) {
          label = (Label)annotation;
        } else if (annotation.annotationType() == Name.class) {
          name = (Name)annotation;
        }
      }
      boolean enabled;
      String parameterName;
      if (label != null) {
        enabled = label.enabled();
        if (label.name().equals("/")) {
          parameterName = String.format("%s/arg%d", labelPrefix, i);
        } else if (label.name().isEmpty()) {
          parameterName = labelPrefix;
        } else {
          parameterName = String.format("%s/%s", labelPrefix, label.name());
        }
      } else {
        enabled = false;
        parameterName = String.format("%s/arg%d", labelPrefix, i);
      }
      if (labelAllParams) {
        enabled = true;
      }
      if (name != null) {
        parameterName = name.value();
      }
      if (enabled) {
        labelsBuilder.add(parameterName, invocation.getArguments()[i].toString());
      }
    }
  }
}
