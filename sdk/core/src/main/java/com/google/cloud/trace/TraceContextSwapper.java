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

package com.google.cloud.trace;

/**
 * An interface used for swapping {@link TraceContextHandler}s.
 */
public interface TraceContextSwapper {

  /**
   * Swap the current {@link TraceContextHandler} with the provided {@link TraceContextHandler}
   * @param newHandler The new {@link TraceContextHandler}
   * @return The previous {@link TraceContextHandler}
   */
  TraceContextHandler swap(TraceContextHandler newHandler);

  /**
   * @return The current {@link TraceContextHandler}
   */
  TraceContextHandler currentHandler();
}
