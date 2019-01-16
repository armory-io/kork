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

package com.netflix.spinnaker.config.secrets.engines;

import com.netflix.spinnaker.config.secrets.EncryptedSecret;
import com.netflix.spinnaker.config.secrets.InvalidSecretFormatException;
import com.netflix.spinnaker.config.secrets.SecretDecryptionException;
import com.netflix.spinnaker.config.secrets.SecretEngine;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
public abstract class AbstractStorageSecretEngine implements SecretEngine {
  protected final static String STORAGE_BUCKET = "b";
  protected final static String STORAGE_REGION = "r";
  protected final static String STORAGE_FILE_URI = "f";
  protected final static String STORAGE_PROP_KEY = "k";

  // Local cache of downloaded and parsed files
  protected Map<String, Map<String,Object>> cache = new HashMap<>();

  public String decrypt(EncryptedSecret encryptedSecret) throws SecretDecryptionException {
    String fileUri = encryptedSecret.getParams().get(STORAGE_FILE_URI);
    String key = encryptedSecret.getParams().get(STORAGE_PROP_KEY);

    InputStream is = null;
    try {
      if (key == null || !cache.containsKey(fileUri)) {
        // We don't cache direct file references
        is = downloadRemoteFile(encryptedSecret);
      }

      // Return the whole content as a string
      if (key == null) {
        return new String(readAll(is));
      }

      // Parse as YAML
      if (!cache.containsKey(fileUri)) {
        parseAsYaml(fileUri, is);
      }
      return getParsedValue(fileUri, key);

    } catch (IOException e) {
      throw new SecretDecryptionException(e);

    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {}
      }
    }
  }

  /**
   * @param encryptedSecret
   * @return true
   * @throws InvalidSecretFormatException
   */
  public void validate(EncryptedSecret encryptedSecret) throws InvalidSecretFormatException {
    Set<String> paramNames = encryptedSecret.getParams().keySet();
    if (!paramNames.contains(STORAGE_BUCKET)) {
      throw new InvalidSecretFormatException("Storage bucket parameter is missing (" + STORAGE_BUCKET + "=...)");
    }
    if (!paramNames.contains(STORAGE_REGION)) {
      throw new InvalidSecretFormatException("Storage region parameter is missing (" + STORAGE_REGION + "=...)");
    }
    if (!paramNames.contains(STORAGE_FILE_URI)) {
      throw new InvalidSecretFormatException("Storage file parameter is missing (" + STORAGE_FILE_URI + "=...)");
    }
  }


  protected abstract InputStream downloadRemoteFile(EncryptedSecret encryptedSecret) throws IOException;


  protected byte[] readAll(InputStream inputStream) throws IOException {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      byte[] buf = new byte[4096];
      for (;;) {
        int read = inputStream.read(buf, 0, buf.length);
        if (read <= 0) {
          break;
        }
        out.write(buf, 0, read);
      }
      return out.toByteArray();
    }
  }

  protected void parseAsYaml(String fileURI, InputStream inputStream) {
    Map<String,Object> parsed = (Map<String, Object>) new Yaml().load(inputStream);
    cache.put(fileURI, parsed);
  }

  protected String getParsedValue(String fileURI, String yamlPath) throws SecretDecryptionException {
    String[] pathElts = yamlPath.split("\\.");
    Map<String,Object> parsed = cache.get(fileURI);

    for (String pathElt : pathElts) {
      Object o = parsed.get(pathElt);
      if (o instanceof Map) {
        parsed = (Map<String, Object>) o;
      } else if (o instanceof List) {
        parsed = ((List<Map<String, Object>>) o).get(Integer.valueOf(pathElt));
      } else {
        return (String) o;
      }
    }
    throw new SecretDecryptionException("Invalid secret key specified: " + yamlPath);
  }
}
