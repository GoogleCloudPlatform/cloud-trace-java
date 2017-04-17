# Concepts

## Trace context

A `TraceContext` is the identifier for a particular span - it holds the trace ID,
span ID, and options for that span. The `TraceContext` is passed when a span is
started, stopped, or annotated.

## Raw tracer

The `rawTracer` defines where the trace data is sent. There are two main
implementations of `rawTracer`:

**LoggingRawTracer**: Sends data to the specified log location at the specified
log level.

    // Create the raw tracer.
    TraceSource traceSource = new TraceSource();
    TraceSink traceSink = new LoggingTraceSink(logger, Level.WARNING); // Sets the sink to be java.lang.Logger
    RawTracer rawTracer = new RawTracerV1("10056646", traceSource, traceSink); // Creates rawTracer with projectId, traceSource, sink defined

**RawTracerV1**: Sends data to the Stackdriver Trace API. It defines the project
ID, the trace source, and the trace sink for all associated traces.

    // Create the raw tracer.
    TraceSource traceSource = new TraceSource();
    TraceSink traceSink = new GrpcTraceSink("cloudtrace.googleapis.com",
    GoogleCredentials.getApplicationDefault());

    RawTracer rawTracer = new RawTracerV1(projectId, traceSource, traceSink);

The trace source is always a new `TraceSource` object.

Trace sinks are described below. Multiple raw tracers can be defined so that
data can be sent to more than one sink.

## Logging sinks

Logging sinks write the data to the specified location. In a production
environment, you'll use a `GrpcTraceSink` to write to the Trace service. In a
testing environment, or if you're writing traces to your own service, use
`LoggingTraceSink`.

*   `GrpcTraceSink`: Writes to the Trace service using gRPC.
*   `LoggingTraceSink`: Writes to the specified logger. Useful for testing or for
    writing to your own service.

## Buffering sinks

Buffering sinks can be backed by any logging sink. They queue up writes based on
size, time, or both.

*   `ScheduledBufferingTraceSink`: Writes to a sink that is buffered by both
    size and time. The sink will send buffered data once the specified size has
    been reached, or the specified delay has elapsed, whichever happens first.
*   `SimpleBufferingTraceSink`: Flushes on command.
*   `SizedBufferingTraceSink`: A sink that is buffered by size.

## Tracer / ManagedTracer

Once your raw tracer has been configured, you must create a `Tracer` object. The
`Tracer` object contains the methods required for starting, stopping, and
annotating spans.

A simple `Tracer` object, when working with spans, requires that a context be
passed to it. The `TraceContext` defines the relationships between a trace and its
parents and/or children, as well as any options on the trace.

For example:

    // Create a span using the given timestamps.
    TraceContext context1 = tracer.startSpan(traceContextFactory.rootContext(), "my span 1");
    StackTrace.Builder stackTraceBuilder = ThrowableStackTraceHelper.createBuilder(new Exception());
    tracer.setStackTrace(context1, stackTraceBuilder.build());
    tracer.endSpan(context1);

A `ManagedTracer` doesn't require manual handling of contexts. When
`startSpan()` is called, if no spans are currently open, the root span of the
`ManagedTracer` is used as the parent. This creates a new, top-level span in the
trace.

If a second span is started before the first has been ended, the second span is
created as a child of the first. When `endSpan()` is called, the youngest child
span is ended.

    // Create the managed tracer.
    TraceContextHandler traceContextHandler = new DefaultTraceContextHandler(
        traceContextFactory.initialContext());
    ManagedTracer managedTracer = new TraceContextHandlerTracer(tracer, traceContextHandler);

    // Create some trace data.
    managedTracer.startSpan("my span 1");
    managedTracer.startSpan("my span 2");
    StackTrace.Builder stackTraceBuilder = ThrowableStackTraceHelper.createBuilder(new Exception());
    managedTracer.setStackTrace(stackTraceBuilder.build());
    managedTracer.endSpan();
    managedTracer.endSpan();

## Sending a stack trace

Stack traces can be attached to a span as an annotation if they are formatted
correctly.

To simplify writing a stack trace, use the `stackTraceBuilder.build()` method,
which converts your application's exception messages into properly-formatted
stack traces.

    StackTrace.Builder stackTraceBuilder = ThrowableStackTraceHelper.createBuilder(new Exception());
    tracer.setStackTrace(context2, stackTraceBuilder.build());

## Annotating a span

Add labels to a span with the `annotateSpan()` function.

    Label label1 = new Label(key, value);    
    tracer.annotateSpan(context1, label1);

## When to start a new span vs a new trace

A trace corresponds neatly to a request, while a span is a unit of activity
within the request.

For example, for a web service, a trace corresponds neatly to a web request.
A span corresponds to a remote procedure call or a local method call when
handling the request.
