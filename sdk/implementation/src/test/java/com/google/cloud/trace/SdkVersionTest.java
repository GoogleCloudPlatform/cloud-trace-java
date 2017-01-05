package com.google.cloud.trace;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public class SdkVersionTest {
  @Test
  public void testGet() {
    String version = SdkVersion.get();
    assertThat(version).isNotEmpty();
    assertThat(version).doesNotContain("project.version");
    assertThat(version).matches("\\d+\\.\\d+\\.\\d+(-SNAPSHOT)?");
  }
}
