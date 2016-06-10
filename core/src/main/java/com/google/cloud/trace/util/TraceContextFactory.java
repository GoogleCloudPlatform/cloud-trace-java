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
