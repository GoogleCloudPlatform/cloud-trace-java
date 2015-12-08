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

package com.google.cloud.trace.samples.gae.hellotrace;

import com.google.appengine.api.utils.SystemProperty;
import com.google.cloud.trace.sdk.CloudTraceWriter;
import com.google.cloud.trace.sdk.gae.UrlFetchCloudTraceRequestFactory;

/**
 * TraceWriterSingleton holds a single CouldTraceWriter for the hello-trace project.
 */
public class TraceWriterSingleton {
  private static volatile CloudTraceWriter instance = new CloudTraceWriter();
  static {
    instance.setRequestFactory(new UrlFetchCloudTraceRequestFactory());
    instance.setProjectId(SystemProperty.applicationId.get());
  }

  private TraceWriterSingleton() { }

  public static CloudTraceWriter getInstance() {
    return instance;
  }
}
