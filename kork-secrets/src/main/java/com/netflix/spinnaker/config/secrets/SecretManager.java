/*
 * Copyright 2019 Armory, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.config.secrets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class SecretManager {

  @Autowired
  private SecretEngineRegistry secretEngineRegistry;

  public String decrypt(String configValue) throws InvalidSecretFormatException, SecretDecryptionException {
    EncryptedSecret encryptedSecret = EncryptedSecret.parse(configValue);
    if (encryptedSecret == null) { return configValue; }

    SecretEngine secretEngine = secretEngineRegistry.getEngine(encryptedSecret.getEngineIdentifier());
    if (secretEngine == null) {
      throw new InvalidSecretFormatException("Secret Engine does not exist: " + encryptedSecret.getEngineIdentifier());

    }

    secretEngine.validate(encryptedSecret);
    return secretEngine.decrypt(encryptedSecret);
  }

  public String decryptFile(String filePathOrEncrypted) throws IOException, SecretDecryptionException {
    // similar but make temp file with decrypted content
    if (!EncryptedSecret.isEncryptedSecret(filePathOrEncrypted)) {
      return filePathOrEncrypted;
    }

    EncryptedSecret encryptedSecret = EncryptedSecret.parse(filePathOrEncrypted);
    SecretEngine secretEngine = secretEngineRegistry.getEngine(encryptedSecret.getEngineIdentifier());
    if (secretEngine == null) {
      throw new InvalidSecretFormatException("Secret Engine does not exist: " + encryptedSecret.getEngineIdentifier());
    }
    secretEngine.validate(encryptedSecret);
    return decryptedFilePath(secretEngine, encryptedSecret);
  }

  public String decryptedFilePath(SecretEngine secretEngine, EncryptedSecret encryptedSecret) throws IOException, SecretDecryptionException {
    String clearText = secretEngine.decrypt(encryptedSecret);
    File tempFile = File.createTempFile(secretEngine.identifier() + '-', ".secret");
    FileWriter fileWriter = new FileWriter(tempFile);
    fileWriter.write(clearText);
    fileWriter.close();
    tempFile.deleteOnExit();
    return tempFile.getAbsolutePath();
  }

  void setSecretEngineRegistry(SecretEngineRegistry secretEngineRegistry) {
    this.secretEngineRegistry = secretEngineRegistry;
  }


}
