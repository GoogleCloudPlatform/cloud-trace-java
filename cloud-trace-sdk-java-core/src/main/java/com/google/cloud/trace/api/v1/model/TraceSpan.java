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
 * Model definition for TraceSpan.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the Google Cloud Trace API. For a detailed explanation
 * see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 */
@SuppressWarnings("javadoc")
public final class TraceSpan extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String endTime;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String kind;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.Map<String, java.lang.String> labels;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.math.BigInteger parentSpanId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.math.BigInteger spanId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String startTime;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getEndTime() {
    return endTime;
  }

  /**
   * @param endTime endTime or {@code null} for none
   */
  public TraceSpan setEndTime(java.lang.String endTime) {
    this.endTime = endTime;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getKind() {
    return kind;
  }

  /**
   * @param kind kind or {@code null} for none
   */
  public TraceSpan setKind(java.lang.String kind) {
    this.kind = kind;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.Map<String, java.lang.String> getLabels() {
    return labels;
  }

  /**
   * @param labels labels or {@code null} for none
   */
  public TraceSpan setLabels(java.util.Map<String, java.lang.String> labels) {
    this.labels = labels;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getName() {
    return name;
  }

  /**
   * @param name name or {@code null} for none
   */
  public TraceSpan setName(java.lang.String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.math.BigInteger getParentSpanId() {
    return parentSpanId;
  }

  /**
   * @param parentSpanId parentSpanId or {@code null} for none
   */
  public TraceSpan setParentSpanId(java.math.BigInteger parentSpanId) {
    this.parentSpanId = parentSpanId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.math.BigInteger getSpanId() {
    return spanId;
  }

  /**
   * @param spanId spanId or {@code null} for none
   */
  public TraceSpan setSpanId(java.math.BigInteger spanId) {
    this.spanId = spanId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getStartTime() {
    return startTime;
  }

  /**
   * @param startTime startTime or {@code null} for none
   */
  public TraceSpan setStartTime(java.lang.String startTime) {
    this.startTime = startTime;
    return this;
  }

  @Override
  public TraceSpan set(String fieldName, Object value) {
    return (TraceSpan) super.set(fieldName, value);
  }

  @Override
  public TraceSpan clone() {
    return (TraceSpan) super.clone();
  }
}
