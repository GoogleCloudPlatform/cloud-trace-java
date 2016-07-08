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

import java.security.SecureRandom;
import java.util.Random;

public class NaiveSamplingTraceOptionsFactory implements TraceOptionsFactory {
  private final Random random;
  private final double samplingRate;
  private final boolean stackTraceEnabled;

  public NaiveSamplingTraceOptionsFactory(
      Random random, double samplingRate, boolean stackTraceEnabled) {
    this.random = random;
    this.samplingRate = samplingRate;
    this.stackTraceEnabled = stackTraceEnabled;
  }

  public NaiveSamplingTraceOptionsFactory(double samplingRate, boolean stackTraceEnabled) {
    this(new SecureRandom(), samplingRate, stackTraceEnabled);
  }

  @Override
  public TraceOptions create() {
    boolean traceEnabled = random.nextDouble() <= samplingRate;
    return new TraceOptions()
        .overrideTraceEnabled(traceEnabled).overrideStackTraceEnabled(stackTraceEnabled);
  }

  @Override
  public TraceOptions create(TraceOptions traceOptions) {
    return traceOptions;
  }
}
