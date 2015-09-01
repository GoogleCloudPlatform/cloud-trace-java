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

package com.google.cloud.trace.sdk;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Wrapper for {@link HttpRequestFactory} that can be used in testable code.
 */
public class HttpTransportCloudTraceRequestFactory implements CloudTraceRequestFactory {

  /** Instance of the HTTP transport. */
  private HttpTransport httpTransport;

  /** The wrapped request factory. */
  private HttpRequestFactory requestFactory;
  
  public HttpTransportCloudTraceRequestFactory() {
    this.httpTransport = new ApacheHttpTransport();
    this.requestFactory = this.httpTransport.createRequestFactory();
  }

  public HttpTransportCloudTraceRequestFactory(HttpTransport transport, HttpRequestFactory requestFactory) {
    this.httpTransport = transport;
    this.requestFactory = requestFactory;
  }

  @Override
  public void initFromProperties(Properties props) throws CloudTraceException {
    String credentialProviderClassName =
        props.getProperty(getClass().getName() + ".credentialProvider");
    if (credentialProviderClassName != null && !credentialProviderClassName.isEmpty()) {
      CredentialProvider credProvider = (CredentialProvider) ReflectionUtils.createFromProperties(
          credentialProviderClassName, props);
      this.requestFactory = this.httpTransport.createRequestFactory(
          credProvider.getCredential(CloudTraceWriter.SCOPES));
    }
  }

  @Override
  public CloudTraceResponse executePatch(GenericUrl url, String requestBody) throws CloudTraceException {
    try {
      HttpRequest request = requestFactory.buildPatchRequest(url,
          ByteArrayContent.fromString(null, requestBody));
      request.getHeaders().setContentType("application/json");
      HttpResponse response = request.execute();
      return new CloudTraceResponse("", response.getStatusCode());
    } catch (IOException e) {
      throw new CloudTraceException("Exception executing PATCH", e);
    }
  }

  @Override
  public CloudTraceResponse executeGet(GenericUrl url) throws CloudTraceException {
    try {
      HttpRequest request = requestFactory.buildGetRequest(url);
      HttpResponse response = request.execute();
      StringBuilder sb = new StringBuilder();
      if (response.getStatusCode() == HttpStatusCodes.STATUS_CODE_OK) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getContent()));
        String line = null;
        while ((line = reader.readLine()) != null)
        {
          sb.append(line + '\n');
        }
      }      
      return new CloudTraceResponse(sb.toString(), response.getStatusCode());
    } catch (IOException e) {
      throw new CloudTraceException("Exception executing GET", e);
    }
  }

  public HttpRequestFactory getRequestFactory() {
    return requestFactory;
  }
}
