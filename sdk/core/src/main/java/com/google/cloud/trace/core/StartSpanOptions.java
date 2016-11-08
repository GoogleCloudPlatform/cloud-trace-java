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

package com.google.cloud.trace.core;

/**
 * A class that contains values that can override the default values used when starting a new span.
 *
 * @see SpanKind
 * @see Timestamp
 * @see TraceOptions
 */
public class StartSpanOptions {
  private Timestamp timestamp;
  private SpanKind spanKind;
  private Boolean enableTrace;
  private Boolean enableStackTrace;

  /**
   * Returns the timestamp.
   *
   * @return the timestamp or the default if null.
   */
  public Timestamp getTimestamp() {
    return timestamp;
  }

  /**
   * Sets the timestamp.
   *
   * @param timestamp the timestamp or the default if null.
   * @return this.
   */
  public StartSpanOptions setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Returns the span kind.
   *
   * @return the span kind or the default if null.
   */
  public SpanKind getSpanKind() {
    return spanKind;
  }

  /**
   * Sets the span kind.
   *
   * @param spanKind the span kind or the default if null.
   * @return this.
   */
  public StartSpanOptions setSpanKind(SpanKind spanKind) {
    this.spanKind = spanKind;
    return this;
  }

  /**
   * Returns the enable trace option.
   *
   * @return the enable trace option or the default if null.
   */
  public Boolean getEnableTrace() {
    return enableTrace;
  }

  /**
   * Sets the enable trace option.
   *
   * @param enableTrace the enable trace option or the default if null.
   * @return this.
   */
  public StartSpanOptions setEnableTrace(Boolean enableTrace) {
    this.enableTrace = enableTrace;
    return this;
  }

  /**
   * Returns the enable stack trace option.
   *
   * @return the enable stack trace option or the default if null.
   */
  public Boolean getEnableStackTrace() {
    return enableStackTrace;
  }

  /**
   * Sets the enable stack trace option.
   *
   * @param enableStackTrace the enable stack trace option or the default if null.
   * @return this.
   */
  public StartSpanOptions setEnableStackTrace(Boolean enableStackTrace) {
    this.enableStackTrace = enableStackTrace;
    return this;
  }
}
