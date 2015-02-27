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

import com.google.api.client.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Wrapper class for http responses from the Cloud Trace API.
 */
public class CloudTraceResponse {

  private HttpResponse response;

  public CloudTraceResponse(HttpResponse response) {
    this.response = response;
  }

  public int getStatusCode() {
    return this.response.getStatusCode();
  }

  public String getContentAsString() throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getContent()));
    StringBuilder sb = new StringBuilder();
    String line = null;
    while ((line = reader.readLine()) != null)
    {
      sb.append(line + '\n');
    }
    return sb.toString();
  }
}
