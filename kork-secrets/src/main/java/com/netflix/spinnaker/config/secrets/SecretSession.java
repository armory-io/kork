package com.netflix.spinnaker.config.secrets;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SecretSession {
  private SecretManager secretManager;
  private Map<String, String> secretCache = new HashMap<>();
  private Map<String, Path> secretFileCache = new HashMap<>();

  public SecretSession(SecretManager secretManager) {
    this.secretManager = secretManager;
  }

  public String decrypt(String encryptedSecret) {
    if (secretCache.containsKey(encryptedSecret)) {
      return secretCache.get(encryptedSecret);
    } else {
      String decryptedValue = secretManager.decrypt(encryptedSecret);
      secretCache.put(encryptedSecret, decryptedValue);
      return decryptedValue;
    }
  }

  public Path decryptAsFile(String encryptedSecret) {
    if (secretFileCache.containsKey(encryptedSecret)) {
      return secretFileCache.get(encryptedSecret);
    }

    Path decryptedFile = secretManager.decryptAsFile(encryptedSecret);
    secretFileCache.put(encryptedSecret, decryptedFile);

    return decryptedFile;
  }

  public void clearCachedSecrets() {
    secretCache.clear();
    secretFileCache.clear();
    for (SecretEngine se : secretManager.getSecretEngineRegistry().getSecretEngineList()) {
      se.clearCache();
    }
  }
}
