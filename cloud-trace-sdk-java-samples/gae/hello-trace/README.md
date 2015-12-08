# HelloTrace sample app to use with App Engine Java

This is a sample App Engine application demonstrating how to create and write trace spans using
Google Cloud Trace SDK.

## User documentation

### Prerequisites

* Enable Google Cloud Trace: https://console.developers.google.com/project/<project ID>/clouddev/trace
* Enable Google Cloud Trace API: https://console.developers.google.com/project/<project ID>/apiui/apiview/cloudtrace/overview

### How to test the sample application?

* Update pom.xml to modify app.id to be your own App Engine project ID
* Change directory to hello-trace
* Compile and build:
<pre><code>mvn clean install<code></pre>
* Upload to App Engine:
<pre><code>mvn appengine:update<code></pre>

Please see more details [here](https://cloud.google.com/appengine/docs/java/tools/maven) on how to
use Apache Maven with App Engine.