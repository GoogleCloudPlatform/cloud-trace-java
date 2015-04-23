// Copyright 2014 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.trace.sdk;

import java.util.Properties;

/**
 * Meta {@link TraceEnablingPolicy} that passes forward trace-enabled values
 * that are already true.
 */
public class ForwardTraceEnablingPolicy implements TraceEnablingPolicy, CanInitFromProperties {

  private TraceEnablingPolicy enablingPolicy = new NeverTraceEnablingPolicy();

  @Override
  public boolean isTracingEnabled(boolean alreadyEnabled) {
    if (alreadyEnabled) {
      return true;
    }
    return enablingPolicy.isTracingEnabled(false);
  }

  @Override
  public void initFromProperties(Properties props) {
    String innerPolicyClassName = props.getProperty(getClass().getName() + ".enablingPolicy");
    if (innerPolicyClassName != null && !innerPolicyClassName.isEmpty()) {
      this.enablingPolicy =
          (TraceEnablingPolicy) ReflectionUtils.createFromProperties(innerPolicyClassName, props);
    }
  }

  public TraceEnablingPolicy getEnablingPolicy() {
    return enablingPolicy;
  }

  public void setEnablingPolicy(TraceEnablingPolicy enablingPolicy) {
    this.enablingPolicy = enablingPolicy;
  }
}
