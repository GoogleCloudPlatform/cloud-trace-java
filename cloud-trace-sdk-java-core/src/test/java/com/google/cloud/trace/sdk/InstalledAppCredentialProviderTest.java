// Copyright 2015 Google Inc. All rights reserved.
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

package com.google.cloud.trace.sdk;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Properties;

/**
 * Tests for the {@link InstalledAppCredentialProvider} class.
 */
@RunWith(JUnit4.class)
public class InstalledAppCredentialProviderTest {

  private InstalledAppCredentialProvider provider;

  @Test
  public void testInitFromProperties() throws Exception {
    provider = new InstalledAppCredentialProvider();
    Properties props = new Properties();
    props.setProperty(InstalledAppCredentialProvider.class.getName() + ".clientSecretsFileName",
        "foobar.txt");
    props.setProperty(InstalledAppCredentialProvider.class.getName() + ".dataStoreDirName",
        "/a/b/c");
    provider.initFromProperties(props);
    assertEquals("foobar.txt", provider.getClientSecretsFile().getName());
    assertEquals("/a/b/c", provider.getDataStoreDir().getAbsolutePath());
  }
}
