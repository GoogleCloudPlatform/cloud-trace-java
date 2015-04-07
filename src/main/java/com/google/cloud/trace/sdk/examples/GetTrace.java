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

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.cloud.trace.sdk.CloudTraceReader;
import com.google.cloud.trace.sdk.HttpTransportCloudTraceRequestFactory;
import com.google.cloud.trace.sdk.InstalledAppCredentialProvider;

import java.io.File;

/**
 * Reads a trace from the Cloud Trace API.
 * Command-line arguments:
 *   0: Full path to an Installed Application OAuth2 client secrets file.
 *   1: The Cloud Trace API endpoint.
 *   2. The project ID to read the trace from.
 *   3. The trace ID.
 */
public class GetTrace {

  public static void main(String[] args) throws Exception {
    InstalledAppCredentialProvider credentialProvider = new InstalledAppCredentialProvider();
    credentialProvider.setClientSecretsFile(new File(args[0]));

    CloudTraceReader reader = new CloudTraceReader();
    // NetHttpTransport doesn't support HTTP PATCH, but for a GET it's good enough.
    NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    HttpRequestFactory requestFactory = httpTransport.createRequestFactory(
        credentialProvider.getCredential(CloudTraceReader.SCOPES));
    reader.setRequestFactory(new HttpTransportCloudTraceRequestFactory(httpTransport, requestFactory));
    reader.setApiEndpoint(args[1]);
    reader.setProjectId(args[2]);
    String result = reader.readTraceById(args[3]);
    System.out.println(result);
  }
}
