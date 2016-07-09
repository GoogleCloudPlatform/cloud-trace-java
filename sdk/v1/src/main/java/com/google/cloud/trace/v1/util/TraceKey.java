// Copyright 2016 Google Inc. All rights reserved.
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

package com.google.cloud.trace.v1.util;

import com.google.common.base.MoreObjects;
import java.util.Objects;

/**
 * A class that represents a trace key. A trace key consists of the project identifier of the Google
 * Cloud Platform project that owns the trace and the trace identifier of the trace.
 */
public class TraceKey {
  private final String projectId;
  private final String traceId;

  /**
   * Creates a trace key.
   *
   * @param projectId a string that contains the Google Cloud Platform project identifier.
   * @param traceId    a string that contains the trace identifier.
   */
  public TraceKey(String projectId, String traceId) {
    this.projectId = projectId;
    this.traceId = traceId;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof TraceKey)) {
      return false;
    }

    TraceKey that = (TraceKey)obj;
    return Objects.equals(projectId, that.projectId)
        && Objects.equals(traceId, that.traceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(projectId, traceId);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("projectId", projectId)
        .add("traceId", traceId)
        .toString();
  }

  /**
   * Returns the project identifier.
   *
   * @return the project identifier.
   */
  public String getProjectId() {
    return projectId;
  }

  /**
   * Returns the trace identifier.
   *
   * @return the trace identifier.
   */
  public String getTraceId() {
    return traceId;
  }
}
