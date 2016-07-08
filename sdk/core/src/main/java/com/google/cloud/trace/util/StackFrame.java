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

import java.util.Objects;

public class StackFrame {
  private final String className;
  private final String methodName;
  private final String fileName;
  private final Integer lineNumber;
  private final Integer columnNumber;

  public StackFrame(String className, String methodName, String fileName, Integer lineNumber,
      Integer columnNumber) {
    this.className = className;
    this.methodName = methodName;
    this.fileName = fileName;
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof StackFrame)) {
      return false;
    }

    StackFrame that = (StackFrame)obj;
    return Objects.equals(className, that.className)
        && Objects.equals(methodName, that.methodName)
        && Objects.equals(fileName, that.fileName)
        && Objects.equals(lineNumber, that.lineNumber)
        && Objects.equals(columnNumber, that.columnNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(className, methodName, fileName, lineNumber, columnNumber);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("className", className)
        .add("methodName", methodName)
        .add("fileName", fileName)
        .add("lineNumber", lineNumber)
        .add("columnNumber", columnNumber)
        .toString();
  }

  public String getClassName() {
    return className;
  }

  public String getMethodName() {
    return methodName;
  }

  public String getFileName() {
    return fileName;
  }

  public Integer getLineNumber() {
    return lineNumber;
  }

  public Integer getColumnNumber() {
    return columnNumber;
  }
}
