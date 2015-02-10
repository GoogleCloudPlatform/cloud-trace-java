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
import com.google.cloud.trace.sdk.ServiceAccountCredentialProvider;

import java.io.File;

/**
 * Reads a trace from the Cloud Trace API using an OAuth2 service account.
 * Command-line arguments:
 *   0: The email address of the service account in the Cloud project.
 *   1: Full path to a service account P12 key file.
 *   2: The Cloud Trace API endpoint.
 *   3. The project ID to read the trace from.
 *   4. The trace ID.
 */
public class GetTraceWithServiceAccount {

  public static void main(String[] args) throws Exception {
    ServiceAccountCredentialProvider credentialProvider = new ServiceAccountCredentialProvider();
    credentialProvider.setEmailAddress(args[0]);
    credentialProvider.setP12File(new File(args[1]));
    System.out.println("Token: " + credentialProvider.getCredential().getAccessToken());

    CloudTraceReader reader = new CloudTraceReader();
    NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    HttpRequestFactory requestFactory = httpTransport.createRequestFactory(
        credentialProvider.getCredential());
    reader.setRequestFactory(requestFactory);
    reader.setApiEndpoint(args[2]);
    reader.setProjectId(args[3]);
    String result = reader.readTraceById(args[4]);
    System.out.println(result);
  }
}
