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

```xml
    <dependency>
      <groupId>com.google.cloud.trace</groupId>
      <artifactId>core</artifactId>
    </dependency>
```
Additionally, add a dependency on a `TraceService` artifact which will process the trace events you create.
* The Logging service will write trace events to the logs.
```xml
  <dependency>
    <groupId>com.google.cloud.trace</groupId>
    <artifactId>logging-service</artifactId>
  </dependency>
```
* The Stackdriver Trace gRPC API `TraceService` will write traces to the Stackdriver Trace API using gRPC.
```xml
  <dependency>
    <groupId>com.google.cloud.trace</groupId>
    <artifactId>trace-grpc-api-service</artifactId>
  </dependency>
```

If you'd like to build the SDK yourself, create a clone of this project on your local machine:

    git clone https://github.com/GoogleCloudPlatform/cloud-trace-java.git 

The project is organized into a core module that contains the main library classes and interfaces, and optional submodules for platform-specific additions (servlets, etc.). There is a top-level Maven POM that builds them all.

To build the project, from its root directory run:

    mvn install


## Hello, Trace!
### Writing to the Stackdriver Trace API
The following sample writes a trace span to the Stackdriver Trace API using gRPC.
* Add the dependencies to your project.
```xml
  <dependencies>
    <dependency>
      <groupId>com.google.cloud.trace</groupId>
      <artifactId>core</artifactId>
      <version>VERSION</version>
    </dependency>
    <dependency>
      <groupId>com.google.cloud.trace</groupId>
      <artifactId>service</artifactId>
      <version>VERSION</version>
    </dependency>
    <dependency>
      <groupId>com.google.cloud.trace</groupId>
      <artifactId>trace-grpc-api-service</artifactId>
      <version>VERSION</version>
    </dependency>
  </dependencies>
```
* Write traces.
```java
public class Example {
  public static void main(String[] args) throws IOException {
    // Initialize the Tracer.
    TraceService traceService = TraceGrpcApiService.builder()
        // Set the projectId.
        .setProjectId("my-project-id")
        // Uncomment this if you want to provide your own credentials for the Stackdriver Trace API.
        // On GCE, this is optional because the Application Default Credentials are used by default.
        //.setCredentials(
        //    GoogleCredentials.fromStream(new FileInputStream("/path/to/my/credentials.json")))
        // Use a short delay of 1 second for this example. In production, you may want to use a
        // higher value so that more trace events are batched together in a single request to the
        // Stackdriver Trace API. By default, the delay is 15 seconds.
        .setScheduledDelay(1)
    .build();
    Trace.init(traceService);

    Tracer tracer = Trace.getTracer();
    TraceContext context = tracer.startSpan("test-span");
    tracer.endSpan(context);
  }
}
```
### Basic logging
The following sample writes a trace span, containing a stack trace, to logs.
* Add the dependencies to your project.
```xml
  <dependencies>
    <dependency>
      <groupId>com.google.cloud.trace</groupId>
      <artifactId>core</artifactId>
      <version>VERSION</version>
    </dependency>
    <dependency>
      <groupId>com.google.cloud.trace</groupId>
      <artifactId>service</artifactId>
      <version>VERSION</version>
    </dependency>
    <!-- Adding this dependency auto-configures tracing to write to logs. -->
    <dependency>
      <groupId>com.google.cloud.trace</groupId>
      <artifactId>logging-service</artifactId>
      <version>VERSION</version>
      <scope>runtime</scope>
    </dependency>
  </dependencies>
```
* Write traces.
```java
public class Example {
  public static void main(String[] args) {
    Tracer tracer = Trace.getTracer();

    // Create a span using the given timestamps.
    TraceContext context = tracer.startSpan("my span 1");
    StackTrace.Builder stackTraceBuilder = ThrowableStackTraceHelper.createBuilder(new Exception());
    tracer.setStackTrace(context, stackTraceBuilder.build());
    tracer.endSpan(context);
  }
}
```
## What's next

(coming soon: Concepts, Trace Java SDK on Google Cloud Platform)


[travis-image]: https://travis-ci.org/GoogleCloudPlatform/cloud-trace-java.svg?branch=master
[travis-url]: https://travis-ci.org/GoogleCloudPlatform/cloud-trace-java
[maven-image]: https://maven-badges.herokuapp.com/maven-central/com.google.cloud.trace/sdk/badge.svg
[maven-url]: https://maven-badges.herokuapp.com/maven-central/com.google.cloud.trace/sdk
