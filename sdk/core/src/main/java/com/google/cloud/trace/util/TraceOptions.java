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

public class TraceOptions {
  private final static int TRACE_ENABLED = 0x1;
  private final static int STACK_TRACE_ENABLED = 0x2;

  private final int optionsMask;

  public static TraceOptions forTraceDisabled() {
    return new TraceOptions();
  }

  public static TraceOptions forTraceEnabled() {
    return new TraceOptions(TRACE_ENABLED);
  }

  public TraceOptions() {
    this(0);
  }

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

  public int getOptionsMask() {
    return optionsMask;
  }

  public boolean getTraceEnabled() {
    return applyMask(TRACE_ENABLED);
  }

  public boolean getStackTraceEnabled() {
    return applyMask(STACK_TRACE_ENABLED);
  }

  public TraceOptions overrideTraceEnabled(boolean enabled) {
    return new TraceOptions(enabled ? set(TRACE_ENABLED) : clear(TRACE_ENABLED));
  }

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
