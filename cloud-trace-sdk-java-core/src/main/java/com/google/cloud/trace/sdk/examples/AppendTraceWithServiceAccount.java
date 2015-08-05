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

package com.google.cloud.trace.sdk.examples;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.cloud.trace.sdk.HttpTransportCloudTraceRequestFactory;
import com.google.cloud.trace.sdk.CloudTraceWriter;
import com.google.cloud.trace.sdk.DefaultTraceSpanDataBuilder;
import com.google.cloud.trace.sdk.ServiceAccountCredentialProvider;
import com.google.cloud.trace.sdk.SpanIdGenerator;
import com.google.cloud.trace.sdk.TraceContext;
import com.google.cloud.trace.sdk.TraceSpanData;
import com.google.cloud.trace.sdk.TraceSpanDataBuilder;
import com.google.cloud.trace.sdk.UUIDTraceIdGenerator;

import java.io.File;
import java.math.BigInteger;

/**
 * Writes a trace to the Cloud Trace API using an OAuth2 service account.
 * Command-line arguments:
 *   0: The email address of the service account in the Cloud project.
 *   1: Full path to a service account P12 key file.
 *   2: The Cloud Trace API endpoint.
 *   3. The project ID to write the trace to.
 *   4. The trace ID.
 */
public class AppendTraceWithServiceAccount {

  public static void main(String[] args) throws Exception {
    ServiceAccountCredentialProvider credentialProvider = new ServiceAccountCredentialProvider();
    credentialProvider.setEmailAddress(args[0]);
    credentialProvider.setP12File(new File(args[1]));

    CloudTraceWriter writer = new CloudTraceWriter();
    HttpTransport httpTransport = new ApacheHttpTransport();
    HttpRequestFactory requestFactory = httpTransport.createRequestFactory(
        credentialProvider.getCredential(CloudTraceWriter.SCOPES));
    writer.setRequestFactory(new HttpTransportCloudTraceRequestFactory(httpTransport, requestFactory));
    writer.setApiEndpoint(args[2]);
    writer.setProjectId(args[3]);
    
    TraceSpanDataBuilder spanDataBuilder = new DefaultTraceSpanDataBuilder("/PARENT");
    TraceSpanData parentSpan = new TraceSpanData(spanDataBuilder);
    Thread.sleep(1000);
    
    TraceSpanData childSpan1 = new TraceSpanData(spanDataBuilder.createChild("/CHILD1"));
    Thread.sleep(2000);

    TraceSpanData childSpan2 = new TraceSpanData(spanDataBuilder.createChild("/CHILD2"));
    Thread.sleep(2000);

    childSpan2.end();
    Thread.sleep(2000);

    childSpan1.end();
    Thread.sleep(2000);
    parentSpan.end();
    
    System.out.println("Writing trace span with trace id " + parentSpan.getContext().getTraceId());
    writer.writeSpans(parentSpan, childSpan1, childSpan2);
  }
}
