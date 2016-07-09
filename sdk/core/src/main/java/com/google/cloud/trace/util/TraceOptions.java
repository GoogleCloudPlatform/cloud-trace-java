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

package com.google.cloud.trace.util;

import com.google.common.base.MoreObjects;
import java.util.Objects;

/**
 * A class that represents trace options. These options determine options such as whether a span
 * should be traced or whether stack trace information should be collected.
 */
public class TraceOptions {
  private final static int TRACE_ENABLED = 0x1;
  private final static int STACK_TRACE_ENABLED = 0x2;

  private final int optionsMask;

  /**
   * Returns a new trace options with the trace option disabled.
   *
   * @return the new trace options.
   */
  public static TraceOptions forTraceDisabled() {
    return new TraceOptions();
  }

  /**
   * Returns a new trace options with the trace option enabled.
   *
   * @return the new trace options.
   */
  public static TraceOptions forTraceEnabled() {
    return new TraceOptions(TRACE_ENABLED);
  }

  /**
   * Creates a new trace options with default options values.
   */
  public TraceOptions() {
    this(0);
  }

  /**
   * Creates a new trace options with the given options mask.
   *
   * @param optionsMask an options mask for the new trace options.
   */
  public TraceOptions(int optionsMask) {
    this.optionsMask = optionsMask;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof TraceOptions)) {
      return false;
    }

    TraceOptions that = (TraceOptions)obj;
    return Objects.equals(optionsMask, that.optionsMask);
  }

  @Override
  public int hashCode() {
    return Objects.hash(optionsMask);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("optionsMask", optionsMask)
        .toString();
  }

  /**
   * Returns the options mask.
   *
   * @return the options mask.
   */
  public int getOptionsMask() {
    return optionsMask;
  }

  /**
   * Returns whether the trace option is enabled.
   *
   * @return a boolean indicating whether the trace option is enabled.
   */
  public boolean getTraceEnabled() {
    return applyMask(TRACE_ENABLED);
  }

  /**
   * Returns whether the stack trace option is enabled.
   *
   * @return a boolean indicating whether the stack trace option is enabled.
   */
  public boolean getStackTraceEnabled() {
    return applyMask(STACK_TRACE_ENABLED);
  }

  /**
   * Returns a new trace options with this trace options' options values, overriden with the given
   * trace option.
   *
   * @param enabled a boolean that indicates the trace option.
   * @return the new trace options.
   */
  public TraceOptions overrideTraceEnabled(boolean enabled) {
    return new TraceOptions(enabled ? set(TRACE_ENABLED) : clear(TRACE_ENABLED));
  }

  /**
   * Returns a new trace options with this trace options' options values, overriden with the given
   * stack trace option.
   *
   * @param enabled a boolean that indicates the stack trace option.
   * @return the new trace options.
   */
  public TraceOptions overrideStackTraceEnabled(boolean enabled) {
    return new TraceOptions(enabled ? set(STACK_TRACE_ENABLED) : clear(STACK_TRACE_ENABLED));
  }

  private boolean applyMask(int optionsMask) {
    return (this.optionsMask & optionsMask) != 0;
  }

  private int clear(int optionsMask) {
    return this.optionsMask & ~optionsMask;
  }

  private int set(int optionsMask) {
    return this.optionsMask | optionsMask;
  }
}
