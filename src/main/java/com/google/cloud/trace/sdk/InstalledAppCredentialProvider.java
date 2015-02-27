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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

/**
 * Authorizes using the Installed Application approach. See
 * https://developers.google.com/accounts/docs/OAuth2InstalledApp.
 * 
 * TODO: Handle refreshing via the refresh token. Currently when an access token
 * expires, you just go to the data store and delete the stored credentials.
 */
public class InstalledAppCredentialProvider implements CredentialProvider, CanInitFromProperties {

  /**
   * Directory to store user credentials. Defaults to a location under the home directory of the
   * running user.
   */
  private File dataStoreDir = new File(System.getProperty("user.home"), ".store/cloud-trace-sdk");

  /** Instance of the HTTP transport. */
  private HttpTransport httpTransport;

  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application.
   */
  private FileDataStoreFactory dataStoreFactory;

  /**
   * The client secrets file for the installed application.
   */
  private File clientSecretsFile;

  public InstalledAppCredentialProvider() throws GeneralSecurityException, IOException {
    httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    dataStoreFactory = new FileDataStoreFactory(dataStoreDir);
  }

  /**
   * Initializes the installed application credentials parameters from a properties file.
   */
  @Override
  public void initFromProperties(Properties props) {
    String clientSecretsFileName =
        props.getProperty(getClass().getName() + ".clientSecretsFileName");
    if (clientSecretsFileName != null) {
      this.clientSecretsFile = new File(clientSecretsFileName);
    }
    String dataStoreDirName =
        props.getProperty(getClass().getName() + ".dataStoreDirName");
    if (clientSecretsFileName != null) {
      this.dataStoreDir = new File(dataStoreDirName);
    }
  }

  public File getDataStoreDir() {
    return dataStoreDir;
  }

  public void setDataStoreDir(File dataStoreDir) {
    this.dataStoreDir = dataStoreDir;
  }

  public File getClientSecretsFile() {
    return clientSecretsFile;
  }

  public void setClientSecretsFile(File clientSecretsFile) {
    this.clientSecretsFile = clientSecretsFile;
  }

  @Override
  public Credential getCredential(List<String> scopes) throws CloudTraceException {
    if (clientSecretsFile == null) {
      throw new IllegalStateException("Client-secrets file is required");
    }
    
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    GoogleClientSecrets clientSecrets;
    try {
      clientSecrets = GoogleClientSecrets.load(jsonFactory,
          new FileReader(clientSecretsFile));
      GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
          jsonFactory, clientSecrets, scopes).setDataStoreFactory(dataStoreFactory)
          .build();
      return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    } catch (IOException e) {
      throw new CloudTraceException("Exception getting oauth2 credential", e);
    }
  }
}
