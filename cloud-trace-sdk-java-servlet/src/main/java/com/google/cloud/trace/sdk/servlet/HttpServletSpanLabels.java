// Copyright 2014 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.trace.sdk.servlet;

/**
 * Label definitions relating to http servlets.
 */
public interface HttpServletSpanLabels {
  /**
   * Label key for the http response code.
   */
  public static final String HTTP_RESPONSE_CODE_LABEL_KEY =
      "trace.cloud.google.com/http/status_code";

  /**
   * Label key for the http method.
   */
  public static final String HTTP_METHOD_LABEL_KEY =
      "trace.cloud.google.com/http/method";

  /**
   * Label key for the full url.
   */
  public static final String HTTP_URL_LABEL_KEY =
      "trace.cloud.google.com/http/url";

  /**
   * Label key for the host name.
   */
  public static final String HTTP_HOST_LABEL_KEY =
      "trace.cloud.google.com/http/host";
}
