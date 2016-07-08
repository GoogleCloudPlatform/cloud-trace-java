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

package com.google.cloud.trace.util;

import com.google.common.primitives.UnsignedLongs;

import java.math.BigInteger;

public class TraceContextFactory {
  private final TraceOptionsFactory traceOptionsFactory;
  private final IdFactory<TraceId> traceIdFactory;
  private final IdFactory<SpanId> spanIdFactory;

  public static String headerKey() {
    return "X-Cloud-Trace-Context";
  }

  public TraceContextFactory(TraceOptionsFactory traceOptionsFactory) {
    this(traceOptionsFactory, new RandomTraceIdFactory(), new RandomSpanIdFactory());
  }

  public TraceContextFactory(TraceOptionsFactory traceOptionsFactory,
      IdFactory<TraceId> traceIdFactory, IdFactory<SpanId> spanIdFactory) {
    this.traceOptionsFactory = traceOptionsFactory;
    this.traceIdFactory = traceIdFactory;
    this.spanIdFactory = spanIdFactory;
  }

  public TraceContext childContext(TraceContext parentContext) {
    if (parentContext.getTraceId().isValid()) {
      return new TraceContext(parentContext.getTraceId(), spanIdFactory.nextId(),
          traceOptionsFactory.create(parentContext.getTraceOptions()));
    }
    return new TraceContext(traceIdFactory.nextId(), spanIdFactory.nextId(),
        traceOptionsFactory.create(parentContext.getTraceOptions()));
  }

  public TraceContext initialContext() {
    return new TraceContext(traceIdFactory.invalid(), spanIdFactory.invalid(), new TraceOptions());
  }

  public TraceContext rootContext() {
    return new TraceContext(traceIdFactory.invalid(), spanIdFactory.invalid(),
        traceOptionsFactory.create());
  }

  public TraceContext fromHeader(String header) {
    int index = header.indexOf('/');
    if (index == -1) {
      TraceId traceId = new TraceId(new BigInteger(header, 16));
      return new TraceContext(traceId, spanIdFactory.invalid(), traceOptionsFactory.create());
    }

    TraceId traceId = new TraceId(new BigInteger(header.substring(0, index), 16));

    String[] afterTraceId = header.substring(index + 1).split(";");
    SpanId spanId = new SpanId(UnsignedLongs.parseUnsignedLong(afterTraceId[0]));
    TraceOptions traceOptions = null;
    for (int i = 1; i < afterTraceId.length; i++) {
      if (afterTraceId[i].startsWith("o=")) {
        String optionsString = afterTraceId[i].substring(2);
        int options = Integer.parseInt(optionsString);
        traceOptions = traceOptionsFactory.create(new TraceOptions(options));
      }
    }

    if (traceOptions == null) {
      traceOptions = traceOptionsFactory.create();
    }

    return new TraceContext(traceId, spanId, traceOptions);
  }
}
