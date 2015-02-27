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

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpStatusCodes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads traces from the public Google Cloud Trace API.
 */
public class CloudTraceReader {

  private static final Logger logger = Logger.getLogger(CloudTraceReader.class.getName());

  /**
   * The scope(s) we need to read traces from the Cloud Trace API.
   */
  public static List<String> SCOPES =
      Arrays.asList("https://www.googleapis.com/auth/trace.readonly");

  /**
   * Request factory for calling the Cloud Trace API.
   */
  private CloudTraceRequestFactory requestFactory;

  /**
   * The id of the cloud project to write traces to.
   */
  private String projectId;
  
  /**
   * The endpoint of the Google API service to call.
   */
  private String apiEndpoint = "https://www.googleapis.com/";
  
  public CloudTraceReader() {
    this.requestFactory = new CloudTraceRequestFactory();
  }
  
  public CloudTraceRequestFactory getRequestFactory() {
    return requestFactory;
  }

  public void setRequestFactory(CloudTraceRequestFactory requestFactory) {
    this.requestFactory = requestFactory;
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

  // TODO: Convert this to produce an instance of Trace from the v1 model.
  public String readTraceById(String traceId) throws CloudTraceException {
    checkState();
    GenericUrl url = buildUrl(traceId);
    logger.log(Level.INFO, "Reading trace from " + url);
    try {
      CloudTraceRequest request = requestFactory.buildGetRequest(url);
      CloudTraceResponse response = requestFactory.execute(request);
      if (response.getStatusCode() != HttpStatusCodes.STATUS_CODE_OK) {
        throw new CloudTraceException("Failed to read span, status = " + response.getStatusCode());
      }
      return response.getContentAsString();
    } catch (IOException e) {
      throw new CloudTraceException("Exception reading span from API, url=" + url, e);
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
  }
}
