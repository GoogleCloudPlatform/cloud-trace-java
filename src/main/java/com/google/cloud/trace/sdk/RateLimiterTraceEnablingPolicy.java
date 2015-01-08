// Copyright 2014 Google Inc. All rights reserved.
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

package com.google.cloud.trace.sdk;

import com.google.common.util.concurrent.RateLimiter;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Trace-enabling policy that uses a rate limiter to decide whether or not to enable.
 */
public class RateLimiterTraceEnablingPolicy implements TraceEnablingPolicy, CanInitFromProperties {

  private static final Logger logger =
      Logger.getLogger(RateLimiterTraceEnablingPolicy.class.getName());

  /**
   * The rate limiter, defaults to 1 trace enabled per second.
   */
  private RateLimiter limiter;

  /**
   * Creates a rate limiting policy with the default rate of 1 per second.
   */
  public RateLimiterTraceEnablingPolicy() {
    this(1.0);
  }
  
  /**
   * Creates a rate limiting policy with the given rate.
   * @param rate
   */
  public RateLimiterTraceEnablingPolicy(double rate) {
    this.limiter = RateLimiter.create(1.0);
  }
  
  @Override
  public boolean isTracingEnabled(boolean alreadyEnabled) {
    if (alreadyEnabled) {
      return true;
    }
    return limiter.tryAcquire();
  }

  @Override
  public void initFromProperties(Properties props) {
    String rateStr = props.getProperty(getClass().getName() + ".ratePerSecond");
    if (rateStr != null) {
      try {
        double rate = Double.parseDouble(rateStr);
        this.limiter = RateLimiter.create(rate);
      } catch (NumberFormatException nfe) {
        logger.log(Level.WARNING, "Found non-numeric rate in the properties (" + rateStr + ")");
      }
    }
  }

  public RateLimiter getLimiter() {
    return limiter;
  }

  public void setLimiter(RateLimiter limiter) {
    this.limiter = limiter;
  }
}
