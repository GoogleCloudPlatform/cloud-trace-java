package com.google.cloud.trace.guice.annotation;

import com.google.cloud.trace.util.Labels;

public interface Labeler {
  String overrideName();
  void addLabelsBeforeCall(Labels.Builder labelsBuilder);
  void addLabelsAfterCall(Labels.Builder labelsBuilder);
}
