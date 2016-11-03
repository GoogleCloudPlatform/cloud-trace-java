# Google Cloud Trace SDK for Java

[![Build Status][travis-image]][travis-url] [![Maven Central][maven-image]][maven-url]

Stackdriver Trace is a distributed tracing system for Google Cloud Platform that collects latency data from your applications and displays it in the Google Cloud Platform Console. You can see detailed near real-time insights into application performance. Stackdriver Trace automatically analyzes all your application traces to generate in-depth performance reports to surface application performance degradations and call flow performance bottlenecks.

The Java SDK provides programmatic access to the concepts of tracing, allowing you to start and stop trace spans, annotate spans, and write the data to Stackdriver Trace or your own service using your choice of sink.

## Prerequisites

To use this SDK, you must have an application that you'd like to trace. The app can be on Google Cloud Platform, on-premise, or another cloud platform.

If you'd like to integrate the SDK with the Stackdriver Trace service, in order view your traces in the Cloud Console UI, you must:

1. [Create a Cloud project](https://support.google.com/cloud/answer/6251787?hl=en).
2. [Enable billing](https://support.google.com/cloud/answer/6288653#new-billing).
3. [Enable the Stackdriver Trace API](https://console.cloud.google.com/apis/api/cloudtrace.googleapis.com/overview).

These steps enable the API but don't require that your app is hosted on Google Cloud Platform.

If you're using the Google Cloud Platform to host your application, this SDK is a good choice for apps on App Engine flexible environment, Google Compute Engine, and Google Container Engine. Apps on App Engine standard environment are traced automatically and don't require the use of this SDK.

## Project overview

To use the SDK, add the following Maven dependency to your application's `pom.xml` file:

    <dependency>
      <groupId>com.google.cloud.trace</groupId>
      <artifactId>google-cloud-trace-sdk-core</artifactId>
      <version>0.1.1-SNAPSHOT</version>
    </dependency>

If you'd like to build the SDK yourself, create a clone of this project on your local machine:

    git clone https://github.com/GoogleCloudPlatform/cloud-trace-java.git 

The project is organized into a core module that contains the main library classes and interfaces, and optional submodules for platform-specific additions (servlets, etc.). There is a top-level Maven POM that builds them all.

To build the project, from its root directory run:

    mvn install


## Hello, Trace!

The following sample writes a trace span, containing a stack trace, to logs.

    public class BasicLogging {
      private final static Logger logger = Logger.getLogger(BasicLogging.class.getName());

      public static void main(String[] args) {
        // Create the raw tracer.
        TraceSource traceSource = new TraceSource();
        TraceSink traceSink = new LoggingTraceSink(logger, Level.WARNING);
        RawTracer rawTracer = new RawTracerV1("1", traceSource, traceSink);

        // Create the tracer.
        TraceContextFactory traceContextFactory = new TraceContextFactory(
            new ConstantTraceOptionsFactory(true, false));
        TimestampFactory timestampFactory = new JavaTimestampFactory();
        Tracer tracer = new TraceContextFactoryTracer(rawTracer, traceContextFactory, timestampFactory);

        // Create a span using the given timestamps.
        TraceContext context1 = tracer.startSpan(traceContextFactory.rootContext(), "my span 1");
        StackTrace.Builder stackTraceBuilder = ThrowableStackTraceHelper.createBuilder(new Exception());
        tracer.setStackTrace(context1, stackTraceBuilder.build());
        tracer.endSpan(context1);
      }
    }

## What's next

(coming soon: Concepts, Trace Java SDK on Google Cloud Platform)


[travis-image]: https://travis-ci.org/GoogleCloudPlatform/cloud-trace-java.svg?branch=master
[travis-url]: https://travis-ci.org/GoogleCloudPlatform/cloud-trace-java
[maven-image]: https://maven-badges.herokuapp.com/maven-central/com.google.cloud.trace/sdk/badge.svg
[maven-url]: https://maven-badges.herokuapp.com/maven-central/com.google.cloud.trace/sdk
