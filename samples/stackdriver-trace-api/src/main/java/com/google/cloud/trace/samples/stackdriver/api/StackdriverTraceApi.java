package com.google.cloud.trace.samples.stackdriver.api;

import com.google.cloud.trace.Trace;
import com.google.cloud.trace.Tracer;
import com.google.cloud.trace.core.Labels;
import com.google.cloud.trace.core.StartSpanOptions;
import com.google.cloud.trace.core.TraceContext;
import com.google.cloud.trace.service.TraceGrpcApiService;
import com.google.cloud.trace.service.TraceService;
import java.io.IOException;

public class StackdriverTraceApi {
  public static void main(String[] args) throws IOException {
    String projectId = System.getProperty("projectId");

    // Initialize the Tracer.
    TraceService traceService =
        TraceGrpcApiService.builder()
            // Set the projectId.
            .setProjectId(projectId)
            // Uncomment this if you want to provide your own credentials for the Stackdriver Trace
            // API.
            // On GCE, this is optional because the Application Default Credentials are used by
            // default.
            // .setCredentials(
            //    GoogleCredentials.fromStream(new FileInputStream("/path/to/my/credentials.json")))
            // Use a short delay of 1 second for this example. In production, you may want to use a
            // higher value so that more trace events are batched together in a single request to
            // the
            // Stackdriver Trace API. By default, the delay is 15 seconds.
            .setScheduledDelay(1)
            .build();
    Trace.init(traceService);

    Tracer tracer = Trace.getTracer();
    // Force tracing for this span to see the trace in Stackdriver console.
    StartSpanOptions startSpanOptions = new StartSpanOptions().setEnableTrace(true);
    TraceContext context = tracer.startSpan("test-span", startSpanOptions);
    tracer.annotateSpan(
        context, Labels.builder().add("MyTestLabelKey", "MyTestLabelValue").build());
    tracer.endSpan(context);
  }
}
