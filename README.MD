# Google Cloud Trace SDK for Java

Google Cloud Trace SDK for Java is a Java library that provides tools, best practices, and examples for integrating with Google Cloud Trace, a feature of Google Cloud Platform that collects, displays, and analyzes latency data from applications.

*NOTE: Google Cloud Trace SDK for Java currently depends on the Google Trace API, which is currently only available to selected Google Cloud users. The API should be widely available within the next few weeks.*

## Links

* [Google Cloud Trace](https://cloud.google.com/cloud-trace/)

## How to develop

* Note the current list of open issues in the Github issue tracker.
* Clone this repository.
* See the CONTRIBUTING file in the root directory.
* The project is organized into a "core" module that contains the main library classes and interfaces, and optional submodules for platform-specific additions (servlets, etc.).
* There is a top-level Maven pom that builds them all:
<pre><code>mvn install</code></pre>
* Make sure to add and run tests as appropriate before submitting patch requests.

## Releases
Releases will be tagged in the repository and an announcement will be made on our Github page. We have no strict timetable but aim to do this every three months or whenever a more urgent need arises.

## Roadmap

* [gRPC](http://www.grpc.io/) Integration
* Guice/Spring submodules (e.g. injecting TraceContext into request scopes).
* HttpClient integration (including trace header forwarding on outgoing requests).

## User documentation

### Prerequisites

* Clone the project and build from source (see above).
* Maven dependency for the core module:
<pre><code>
&lt;dependency&gt;
&nbsp;&nbsp;&lt;groupId&gt;com.google.cloud.trace&lt;/groupId&gt;
&nbsp;&nbsp;&lt;artifactId&gt;google-cloud-trace-sdk-core&lt;/artifactId&gt;
&nbsp;&nbsp;&lt;version&gt;0.1-SNAPSHOT&lt;/version&gt;
&lt;/dependency&gt;
</code></pre>

### Key Classes and Interfaces

#### TraceSpanData
TraceSpanData contains the key data for a specific timing, including start and end times, name, and parent span (if any). It can also hold arbitrary user-defined key-value data. The key focus of the SDK is on the creation and management of TraceSpanData instances.

#### TraceContext
TraceContext associates a span with a specific trace. It should be propagated across the wire (for example, serialized in a well-defined HTTP header -- see TraceHeader.java) to support cross-node and cross-application tracing.

#### TraceSpanDataBuilder
This interface exposes the data needed to construct a TraceSpanData. To create a TraceSpanData, you will first create an instance of a TraceSpanBuilder implementation, then pass that instance to the TraceSpanData constructor. There is a default implementation, DefaultTraceSpanDataBuilder, that creates a TraceSpanData from simple values.

#### TraceWriter
TraceWriter is an interface that serves as a sink for completed TraceSpanData instances, writing them directly to the Cloud Trace API, or to a log for later processing (for example).

#### TraceSpanDataHandle
This is a convenience class that wraps a TraceSpanData in a Java AutoClosable implementation that will automatically end a span and write it out via a TraceWriter.

### Trace Writers
The core SDK includes a couple of TraceWriter implementations that write out data, along with some decorators that implement specific writing policies.

#### LoggingTraceWriter
LoggingTraceWriter writes TraceSpanData instances out to a standard Java log file at the INFO level. These may later be forwarded to the Cloud Trace API out-of-band using a separate application or logs processor like FluentD.

#### CloudTraceWriter
CloudTraceWriter contains all of the logic needed to call the Cloud Trace API (JSON in an HTTP PATCH request). Because TraceWriter is span-oriented and the API is trace-oriented, for efficiency purposes it contains the logic to aggregate spans by trace before forwarding them along.

#### BatchingTraceWriter
This is a decorator TraceWriter implementation that batches a number of trace span data instances prior to delegating them to the underlying TraceWriter.

##### OAuth2 Authentication
The Cloud Trace API requires [OAuth2 credentials](https://developers.google.com/identity/protocols/OAuth2?hl=en) to be provided on each call. (See the Cloud Trace API documentation for more details.) To ease this process, the Cloud Trace SDK has an interface, CredentialProvider, with the following implementations, all of which can be configured in a .properties file:
* InstalledAppCredentialProvider
* LocalMetadataCredentialProvider
* ServiceAccountCredentialProvider

### Trace Enabling Policies
An interface, TraceEnablingPolicy, is provided to allow TraceSpanDataBuilder's a structured way, when creating new TraceContext's, to request writers to write or not write traces. Built-in implementations include:
* Always/NeverTraceEnablingPolicy
* PercentageTraceEnablingPolicy -- allows a configurable percentage of traces to be written.
* RateLimiterTraceEnablingPolicy -- allows rate-limited traces.
* ForwardingTraceEnablingPolicy -- forwards the incoming write/don't state.

### Submodules
Submodules are separate libraries that depend on the core Cloud Trace SDK and add additional functionality to integrate with specific libraries, frameworks, and platforms.

#### Servlet
The main contribution of the Servlet submodule is a servlet Filter, TraceFilter, that when installed will automatically manage a new TraceSpanData instance (including processing any incoming trace header) and install it on the servlet Request context. Install and configure in web.xml.

<pre><code>
&lt;dependency&gt;
&nbsp;&nbsp;&lt;groupId&gt;com.google.cloud.trace&lt;/groupId&gt;
&nbsp;&nbsp;&lt;artifactId&gt;google-cloud-trace-sdk-servlet&lt;/artifactId&gt;
&nbsp;&nbsp;&lt;version&gt;0.1-SNAPSHOT&lt;/version&gt;
&lt;/dependency&gt;
</code></pre>
