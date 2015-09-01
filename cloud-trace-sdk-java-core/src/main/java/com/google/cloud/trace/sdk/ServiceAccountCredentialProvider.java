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
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

/**
 * Authorizes using the Service Account approach. See
 * https://developers.google.com/accounts/docs/OAuth2ServiceAccount.
 */
public class ServiceAccountCredentialProvider implements CredentialProvider, CanInitFromProperties {

  /**
   * The email address of the service account.
   */
  private String emailAddress;

  /**
   * The private key file for the service account.
   */
  private File p12File;

  /**
   * Initializes the service account credentials parameters from a properties file.
   */
  @Override
  public void initFromProperties(Properties props) {
    this.emailAddress = props.getProperty(getClass().getName() + ".emailAddress");

    String p12FileName = props.getProperty(getClass().getName() + ".p12FileName");
    if (p12FileName != null) {
      this.p12File = new File(p12FileName);
    }
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public File getP12File() {
    return p12File;
  }

  public void setP12File(File p12File) {
    this.p12File = p12File;
  }

  @Override
  public Credential getCredential(List<String> scopes) throws CloudTraceException {
    if (p12File == null) {
      throw new IllegalStateException("P12 file must be set");
    }
    if (emailAddress == null || emailAddress.isEmpty()) {
      throw new IllegalStateException("Email address must be set");
    }

    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    HttpTransport httpTransport;
    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      GoogleCredential credential = new GoogleCredential.Builder()
          .setTransport(httpTransport)
          .setJsonFactory(jsonFactory)
          .setServiceAccountId(emailAddress)
          .setServiceAccountPrivateKeyFromP12File(p12File)
          .setServiceAccountScopes(scopes)
          .build();
      credential.refreshToken();
      return credential;
    } catch (GeneralSecurityException | IOException e) {
      throw new CloudTraceException("Exception getting oauth2 credential", e);
    }
  }
}
