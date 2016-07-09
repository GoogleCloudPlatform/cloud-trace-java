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
 * A class that is a collection of span labels.
 *
 * @see Label
 */
public class Labels {
  /**
   * A builder for creating a collection of span labels.
   */
  public static class Builder {
    private final ArrayList<Label> labels;

    private Builder() {
      this.labels = new ArrayList<Label>();
    }

    /**
     * Adds a new label to the builder.
     *
     * @param key   a string that is the label key.
     * @param value a string that is the label value.
     * @return this.
     */
    public Builder add(String key, String value) {
      labels.add(new Label(key, value));
      return this;
    }

    /**
     * Builds a new collection of span labels.
     *
     * @return the new collection of span labels.
     */
    public Labels build() {
      return new Labels(ImmutableList.copyOf(labels));
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

  private final ImmutableList<Label> labels;

  private Labels(ImmutableList<Label> labels) {
    this.labels = labels;
  }

  /**
   * Returns the list of span labels.
   *
   * @return the list of span labels.
   */
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
