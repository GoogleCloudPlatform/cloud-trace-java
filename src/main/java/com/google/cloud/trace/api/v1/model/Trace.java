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

package com.google.cloud.trace.api.v1.model;

/**
 * Model definition for Trace.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the Google Cloud Trace API. For a detailed explanation
 * see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 */
@SuppressWarnings("javadoc")
public final class Trace extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String projectId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<TraceSpan> span;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String traceId;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getProjectId() {
    return projectId;
  }

  /**
   * @param projectId projectId or {@code null} for none
   */
  public Trace setProjectId(java.lang.String projectId) {
    this.projectId = projectId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<TraceSpan> getSpan() {
    return span;
  }

  /**
   * @param span span or {@code null} for none
   */
  public Trace setSpan(java.util.List<TraceSpan> span) {
    this.span = span;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getTraceId() {
    return traceId;
  }

  /**
   * @param traceId traceId or {@code null} for none
   */
  public Trace setTraceId(java.lang.String traceId) {
    this.traceId = traceId;
    return this;
  }

  @Override
  public Trace set(String fieldName, Object value) {
    return (Trace) super.set(fieldName, value);
  }

  @Override
  public Trace clone() {
    return (Trace) super.clone();
  }
}
