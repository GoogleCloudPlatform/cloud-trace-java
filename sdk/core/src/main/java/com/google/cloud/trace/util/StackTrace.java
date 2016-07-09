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

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a stack trace. A stack trace is composed of a collection of stack frames.
 *
 * @see StackFrame
 */
public class StackTrace {
  /**
   * A builder for creating a stack trace.
   */
  public static class Builder {
    private final ArrayList<StackFrame> stackFrames;

    private Builder() {
      this.stackFrames = new ArrayList<StackFrame>();
    }

    /**
     * Adds a new stack frame.
     *
     * @param className    a string that represents the class name of the stack frame.
     * @param methodName   a string that represents the method name of the stack frame.
     * @param fileName     a string that represents the file name of the stack frame.
     * @param lineNumber   the line number of the stack frame.
     * @param columnNumber the column number of the stack frame.
     */
    public Builder add(String className, String methodName, String fileName, Integer lineNumber,
        Integer columnNumber) {
      stackFrames.add(new StackFrame(className, methodName, fileName, lineNumber, columnNumber));
      return this;
    }

    /**
     * Builds a new stack trace.
     *
     * @return the new stack trace.
     */
    public StackTrace build() {
      return new StackTrace(ImmutableList.copyOf(stackFrames));
    }
  }

  /**
   * Returns a new builder.
   *
   * @return the new builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  private final ImmutableList<StackFrame> stackFrames;

  private StackTrace(ImmutableList<StackFrame> stackFrames) {
    this.stackFrames = stackFrames;
  }

  /**
   * Returns the list of stack frames.
   *
   * @return the list of stack frames.
   */
  public List<StackFrame> getStackFrames() {
    return stackFrames;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("stackFrames", stackFrames)
        .toString();
  }
}
