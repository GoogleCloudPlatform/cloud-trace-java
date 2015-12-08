// Copyright 2015 Google Inc. All rights reserved.
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

package com.google.cloud.trace.sdk.gae;

import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.CloudTrace;
import com.google.cloud.trace.sdk.TraceContext;

import javax.annotation.Nullable;

/**
 * A wrapper class over CloudTrace to get and set current trace context.
 */
public class AppEngineCurrentTraceContext {
  private static AppEngineCurrentTraceContext instance = new AppEngineCurrentTraceContext();

  @Nullable
  public TraceContext get() {
    return ContextTransformer.transform(
        CloudTrace.getCurrentContext(ApiProxy.getCurrentEnvironment()));
  }

  public void set(@Nullable TraceContext context) {
    CloudTrace.setCurrentContext(
        ApiProxy.getCurrentEnvironment(), ContextTransformer.transform(context));
  }

  public static void setInstance(AppEngineCurrentTraceContext current) {
    instance = current;
  }
  public static AppEngineCurrentTraceContext getInstance() {
    return instance;
  }
}