package com.google.cloud.trace.v1.util;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class TraceKey {
  private final String projectId;
  private final String traceId;

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

  public String getProjectId() {
    return projectId;
  }

  public String getTraceId() {
    return traceId;
  }
}
