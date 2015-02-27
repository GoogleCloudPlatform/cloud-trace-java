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
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;

import java.io.IOException;
import java.util.Properties;

/**
 * Wrapper for {@link HttpRequestFactory} that can be used in testable code.
 */
public class CloudTraceRequestFactory implements CanInitFromProperties {

  /** Instance of the HTTP transport. */
  private HttpTransport httpTransport;

  /** The wrapped request factory. */
  private HttpRequestFactory requestFactory;
  
  public CloudTraceRequestFactory() {
    this.httpTransport = new ApacheHttpTransport();
    this.requestFactory = this.httpTransport.createRequestFactory();
  }

  public CloudTraceRequestFactory(HttpTransport transport, HttpRequestFactory requestFactory) {
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

  /**
   * Creates the patch request that we use to write trace data to the API.
   */
  public CloudTraceRequest buildPatchRequest(GenericUrl url, String requestBody) throws IOException {
    return new CloudTraceRequest(
        requestFactory.buildPatchRequest(url, ByteArrayContent.fromString(null, requestBody)));
  }

  /**
   * Creates the get request that we use to read data from the trace API.
   */
  public CloudTraceRequest buildGetRequest(GenericUrl url) throws IOException {
    return new CloudTraceRequest(requestFactory.buildGetRequest(url));
  }

  public HttpTransport getHttpTransport() {
    return httpTransport;
  }

  public HttpRequestFactory getRequestFactory() {
    return requestFactory;
  }

  /**
   * Executes the given request.
   */
  public CloudTraceResponse execute(CloudTraceRequest request) throws IOException {
    return new CloudTraceResponse(request.getHttpRequest().execute());
  }
}
