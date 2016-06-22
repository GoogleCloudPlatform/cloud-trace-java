package com.google.cloud.trace.guice.auth;

import com.google.inject.AbstractModule;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ClientSecretsFileJndiModule extends AbstractModule {
  @Override
  protected void configure() {
    String clientSecretsFile;
    try {
      Context env = (Context) new InitialContext().lookup("java:comp/env");
      clientSecretsFile = (String) env.lookup("clientSecretsFile");
    } catch (NamingException ex) {
      throw new RuntimeException(ex);
    }
    bind(String.class).annotatedWith(ClientSecretsFile.class).toInstance(clientSecretsFile);
  }
}
