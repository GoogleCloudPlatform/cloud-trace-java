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
 * A factory for generating new trace options with constant option values.
 *
 * @see TraceOptions
 * @see TraceOptionsFactory
 */
public class ConstantTraceOptionsFactory implements TraceOptionsFactory {
  private final TraceOptions traceOptions;

  /**
   * Creates a trace options factory that generates trace options with the given option values.
   *
   * @param traceEnabled      a boolean that is the value of the new trace options' trace option.
   * @param stackTraceEnabled a boolean that is the value of the new trace options' stack trace
   *                          option.
   */
  public ConstantTraceOptionsFactory(boolean traceEnabled, boolean stackTraceEnabled) {
    this.traceOptions = new TraceOptions()
        .overrideTraceEnabled(traceEnabled).overrideStackTraceEnabled(stackTraceEnabled);
  }

  @Override
  public TraceOptions create() {
    return traceOptions;
  }

  @Override
  public TraceOptions create(TraceOptions traceOptions) {
    return traceOptions;
  }
}
