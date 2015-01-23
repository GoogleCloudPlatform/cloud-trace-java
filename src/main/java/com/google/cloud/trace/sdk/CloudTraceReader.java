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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.http.HttpTransport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

/**
 * Reads traces from the public Google Cloud Trace API.
 */
public class CloudTraceReader {

  /**
   * The scope(s) we need to read traces from the Cloud Trace API.
   */
  public static List<String> SCOPES =
      Arrays.asList("https://www.googleapis.com/auth/trace.readonly");

  /**
   * Gets the OAuth2 credentials for calling the Cloud Trace API.
   */
  private CredentialProvider credentialProvider;

  /**
   * The id of the cloud project to write traces to.
   */
  private String projectId;
  
  /**
   * The endpoint of the Google API service to call.
   */
  private String apiEndpoint = "https://www.googleapis.com/";
  
  /** Instance of the HTTP transport. */
  private HttpTransport httpTransport;

  public CloudTraceReader() throws GeneralSecurityException, IOException {
    this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
  }
  
  public CredentialProvider getCredentialProvider() {
    return credentialProvider;
  }

  public void setCredentialProvider(CredentialProvider credentialProvider) {
    this.credentialProvider = credentialProvider;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String getApiEndpoint() {
    return apiEndpoint;
  }

  public void setApiEndpoint(String apiEndpoint) {
    this.apiEndpoint = apiEndpoint;
  }

  public String readTraceById(String traceId) throws TraceWriterException {
    checkState();
    GenericUrl url = buildUrl(traceId);      
    try {
      Credential credential = credentialProvider.authorize();
      HttpRequestFactory requestFactory = httpTransport.createRequestFactory(credential);
      HttpRequest request = requestFactory.buildGetRequest(url);
      // TODO: Should not be setting this header.
      request.getHeaders().put("X-GFE-SSL", "yes");
      HttpResponse response = request.execute();
      if (response.getStatusCode() != HttpStatusCodes.STATUS_CODE_OK) {
        throw new TraceWriterException("Failed to read span, status = " + response.getStatusCode());
      }
      BufferedReader reader = new BufferedReader(new InputStreamReader(response.getContent()));
      StringBuilder sb = new StringBuilder();
      String line = null;
      while ((line = reader.readLine()) != null)
      {
        sb.append(line + '\n');
      }
      return sb.toString();
    } catch (IOException | GeneralSecurityException e) {
      throw new TraceWriterException("Exception reading span from API, url=" + url, e);
    }
  }

  /**
   * Creates the URL to use to patch the trace with the given span.
   */
  private GenericUrl buildUrl(String traceId) {
    String url = apiEndpoint + "v1/projects/" + projectId + "/traces/" + traceId;
    return new GenericUrl(url);
  }

  /**
   * Validates the state before attempting to read a trace from the API.
   */
  private void checkState() {
    if (projectId == null || projectId.isEmpty()) {
      throw new IllegalStateException("Project id must be set");
    }
    if (apiEndpoint == null || apiEndpoint.isEmpty()) {
      throw new IllegalStateException("API endpoint must be set");
    }
    if (credentialProvider == null) {
      throw new IllegalStateException("Credential provider must be set");
    }
  }
}
