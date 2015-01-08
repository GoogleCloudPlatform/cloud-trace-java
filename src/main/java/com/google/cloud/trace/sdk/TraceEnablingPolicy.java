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

/**
 * Responsible for the determination of whether tracing is enabled or not
 * in the current context.
 */
public interface TraceEnablingPolicy {

  /**
   * Performs the tracing-enabled calculation for this node.
   *
   * @param alreadyEnabled Tracks whether tracing is already enabled on the context, or is
   *        unspecified.
   * @return Whether or not tracing is enabled going from here downstream.
   */
  boolean isTracingEnabled(boolean alreadyEnabled);
}
