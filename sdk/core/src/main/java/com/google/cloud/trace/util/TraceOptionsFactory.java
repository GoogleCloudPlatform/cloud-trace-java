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

/**
 * An interface for generating new trace options. This factory could be used to making trace
 * sampling or stack trace collection decisions.
 *
 * @see TraceOptions
 */
public interface TraceOptionsFactory {
  /**
   * Returns a new trace options with default options values.
   *
   * @return the new trace options.
   */
  TraceOptions create();

  /**
   * Returns a new trace options with options values based on the given parent trace options.
   *
   * @param parent a trace options used as the basis for the new trace options' options values.
   * @return the new trace options.
   */
  TraceOptions create(TraceOptions parent);
}
