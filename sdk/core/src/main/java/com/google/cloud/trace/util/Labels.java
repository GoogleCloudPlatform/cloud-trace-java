package com.google.cloud.trace.util;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class Labels {
  public static class Builder {
    private final ArrayList<Label> labels;

    private Builder() {
      this.labels = new ArrayList<Label>();
    }

    public Builder add(String key, String value) {
      labels.add(new Label(key, value));
      return this;
    }

    public Labels build() {
      return new Labels(ImmutableList.copyOf(labels));
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private final ImmutableList<Label> labels;

  private Labels(ImmutableList<Label> labels) {
    this.labels = labels;
  }

  public List<Label> getLabels() {
    return labels;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("labels", labels)
        .toString();
  }
}
