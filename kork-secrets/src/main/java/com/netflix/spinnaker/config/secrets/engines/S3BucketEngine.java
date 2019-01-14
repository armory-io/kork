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
import com.netflix.spinnaker.config.secrets.SecretEngine;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class S3BucketEngine implements SecretEngine {

  private static String IDENTIFIER = "s3";
  private static Set<String> required_params = new HashSet<>();

  public S3BucketEngine() {
    required_params.add("bucket");
  }

  public String identifier() { return S3BucketEngine.IDENTIFIER;}

  public String decrypt(EncryptedSecret encryptedSecret) {
    // Download file from S3, read into string
    String key = encryptedSecret.getParams().get("key");
    if (key == null) {
      // Return string
      return "key is null";
    } else {
      // Parse string as yaml
      // return yaml.get(key)
      return "got a key";
    }
  }

  // TODO (cmotevasselani): return boolean AND throw exception?? do we need to throw exception here?
  public boolean validate(EncryptedSecret encryptedSecret) throws InvalidSecretFormatException {
    Set<String> paramNames = encryptedSecret.getParams().keySet();
    return paramNames.containsAll(required_params);
  }

  public EncryptedSecret encrypt(String secretToEncrypt) throws UnsupportedOperationException {
    throw new UnsupportedOperationException("This operation is not supported");
  }
}
