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

/**
 * Wrapper class for http responses from the Cloud Trace API.
 */
public class CloudTraceResponse {

  private final String content;
  private final int statusCode;
  
  public CloudTraceResponse(String content, int statusCode) {
    this.content = content;
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getContent() {
    return content;
  }
}
