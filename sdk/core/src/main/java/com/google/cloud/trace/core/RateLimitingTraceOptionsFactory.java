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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.RateLimiter;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A factory for generating new trace options. In case of a new root {@link TraceContext}, the trace
 * option values are set based on a rate limiter which limits the number of enabled traces per
 * second. The new trace options' stack trace option is set to the provided value. No sampling
 * decision is made in case of trace options for a child {@link TraceContext}.
 *
 * <p>This class is unconditionally thread-safe.
 */
@ThreadSafe
public final class RateLimitingTraceOptionsFactory implements TraceOptionsFactory {

  private final RateLimiter rateLimiter;
  private final boolean stackTraceEnabled;

  /**
   * Creates a trace options factory that generates trace options based on a rate limiter which
   * limits the number of enabled traces according to the specified {@code samplingRate} and sets
   * the stack trace according to the specified {@code stackTraceEnabled} parameter.
   *
   * @param samplingRate a double that is the rate (unit 1/s) of generated trace options whose trace
   *     option is true.
   * @param stackTraceEnabled the value of the new trace options' stack trace option
   */
  public RateLimitingTraceOptionsFactory(double samplingRate, boolean stackTraceEnabled) {
    this(RateLimiter.create(samplingRate), stackTraceEnabled);
  }

  @VisibleForTesting
  RateLimitingTraceOptionsFactory(RateLimiter rateLimiter, boolean stackTraceEnabled) {
    this.rateLimiter = checkNotNull(rateLimiter);
    this.stackTraceEnabled = stackTraceEnabled;
  }

  @Override
  public TraceOptions create() {
    boolean traceEnabled = rateLimiter.tryAcquire();

    return new TraceOptions()
        .overrideTraceEnabled(traceEnabled)
        .overrideStackTraceEnabled(stackTraceEnabled);
  }

  @Override
  public TraceOptions create(TraceOptions traceOptions) {
    checkNotNull(traceOptions);

    return traceOptions;
  }
}
