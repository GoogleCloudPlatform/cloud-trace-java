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

public class ThrowableStackTraceHelper {
  public static StackTrace.Builder createBuilder(Throwable throwable) {
    return addFrames(StackTrace.builder(), throwable);
  }

  public static StackTrace.Builder addFrames(StackTrace.Builder builder, Throwable throwable) {
    for (StackTraceElement element : throwable.getStackTrace()) {
      Integer lineNumber;
      if (element.getLineNumber() < 0) {
        lineNumber = null;
      } else {
        lineNumber = element.getLineNumber();
      }
      builder.add(
          element.getClassName(), element.getMethodName(), element.getFileName(), lineNumber, null);
    }
    return builder;
  }
}
