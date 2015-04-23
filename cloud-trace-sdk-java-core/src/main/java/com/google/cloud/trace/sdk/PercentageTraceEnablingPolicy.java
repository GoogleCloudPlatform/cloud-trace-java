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

import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Trace-enabling policy that enables for a percentage of requests.
 */
public class PercentageTraceEnablingPolicy implements TraceEnablingPolicy, CanInitFromProperties {

  private static final Logger logger =
      Logger.getLogger(PercentageTraceEnablingPolicy.class.getName());
  
  /**
   * The percentage of requests to enable for, defaults to zero.
   */
  private double percent;

  private Random random = new Random();
  
  public PercentageTraceEnablingPolicy() {
    this(0);
  }
  
  /**
   * Creates a percentage enabling policy with the given percentage value.
   * @param percent The percent value in the range 0..1 inclusive.
   */
  public PercentageTraceEnablingPolicy(double percent) {
    checkPercentage(percent);
    this.percent = percent;
  }

  @Override
  public boolean isTracingEnabled(boolean alreadyEnabled) {
    return percent > random.nextDouble();
  }

  @Override
  public void initFromProperties(Properties props) {
    String pctStr = props.getProperty(getClass().getName() + ".percentage");
    if (pctStr != null) {
      try {
        double pct = Double.parseDouble(pctStr);
        checkPercentage(pct);
        this.percent = pct;
      } catch (NumberFormatException nfe) {
        logger.log(Level.WARNING,
            "Found non-numeric percentage in the properties (" + pctStr + ")");
      } catch (IllegalArgumentException iae) {
        logger.log(Level.WARNING, iae.getMessage());
      }
    }
  }

  public Random getRandom() {
    return random;
  }

  public void setRandom(Random random) {
    this.random = random;
  }

  public double getPercent() {
    return percent;
  }

  public void setPercent(double pct) {
    checkPercentage(pct);
    this.percent = pct;
  }

  private void checkPercentage(double pct) {
    if (pct < 0 || pct > 1) {
      throw new IllegalArgumentException("Invalid percentage: " + pct);
    }
  }
}
