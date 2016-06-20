package com.google.cloud.trace.util;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class StackTrace {
  public static class Builder {
    private final ArrayList<StackFrame> stackFrames;

    private Builder() {
      this.stackFrames = new ArrayList<StackFrame>();
    }

    public Builder add(String className, String methodName, String fileName, Integer lineNumber,
        Integer columnNumber) {
      stackFrames.add(new StackFrame(className, methodName, fileName, lineNumber, columnNumber));
      return this;
    }

    public StackTrace build() {
      return new StackTrace(ImmutableList.copyOf(stackFrames));
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private final ImmutableList<StackFrame> stackFrames;

  private StackTrace(ImmutableList<StackFrame> stackFrames) {
    this.stackFrames = stackFrames;
  }

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
