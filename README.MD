# Google Cloud Trace SDK for Java

Google Cloud Trace SDK for Java is a Java library that provides tools, best practices, and examples for integrating with Google Cloud Trace.

## Links

* [Google Cloud Trace](https://cloud.google.com/cloud-trace/)

## How to develop

* Note the current list of open issues in the Github issue tracker.
* Clone this repository.
* See the CONTRIBUTING file in the root directory.
* The project is organized into a "core" module that contains the main library classes and interfaces, and a number of optional submodules for platform-specific additions (servlets, Google App Engine, etc.).
* There is a top-level Maven pom that builds them all:
<pre><code>mvn install</code></pre>
* Make sure to add and run tests as appropriate before submitting patch requests.

## User documentation

### Prerequisites

* Clone the project and build from source (see above).
* TODO: Insert mvn pom dependencies here for core (submodules below).

### Key Classes and Interfaces
* TraceContext, TraceSpanData, etc.

### Trace Enabling Policies

### Trace Writers
* LoggingTraceWriter
* CloudTraceWriter
  * OAuth2 Authentication

### Submodules

* Servlet
* Google App Engine

### Releases
Releases will be tagged in the repository and an announcement will be made on our Github page. We have no strict timetable but aim to do this every three months or whenever a more urgent need arises.

### Roadmap

* [gRPC](http://www.grpc.io/) Integration
* Guice/Spring submodules (e.g. injecting TraceContext into request scopes).
* HttpClient integration (including trace header forwarding on outgoing requests).
