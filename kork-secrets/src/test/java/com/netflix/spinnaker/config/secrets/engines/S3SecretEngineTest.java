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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.yaml.snakeyaml.Yaml;

import static org.junit.Assert.*;

public class S3SecretEngineTest {

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  S3SecretEngine s3Engine = new S3SecretEngine();

  @Test
  public void identifier() {
    assertEquals("s3", s3Engine.identifier());
  }

  @Test
  public void validateNoRegion() {
    EncryptedSecret encryptedSecret = EncryptedSecret.parse("encrypted:s3!b:bucket-name!k:key-name");
    exceptionRule.expect(InvalidSecretFormatException.class);
    s3Engine.validate(encryptedSecret);
  }

  @Test
  public void validateInvalidRegion() {
    EncryptedSecret encryptedSecret = EncryptedSecret.parse("encrypted:s3!b:bucket-name!r:us-nowhere-1");
    exceptionRule.expect(InvalidSecretFormatException.class);
    s3Engine.validate(encryptedSecret);
  }

  @Test
  public void validateNoBucket() {
    EncryptedSecret encryptedSecret = EncryptedSecret.parse("encrypted:s3!r:us-west-2!k:key-name");
    exceptionRule.expect(InvalidSecretFormatException.class);
    s3Engine.validate(encryptedSecret);
  }

  @Test
  public void validate() {
    EncryptedSecret encryptedSecret = EncryptedSecret.parse("encrypted:s3!b:mybucket!r:us-east-1!f:file.txt!k:key-name");
    s3Engine.validate(encryptedSecret);
  }

//  @Test
//  public void decryptFile() {
//    EncryptedSecret encryptedSecret = EncryptedSecret.parse("encrypted:s3!b:somebucket!r:us-west-2!f:versions.yml");
//    String val = s3Engine.decrypt(encryptedSecret);
//    assertNotNull(val);
//  }

//  @Test
//  public void decryptWithKey() {
//    EncryptedSecret encryptedSecret = EncryptedSecret.parse("encrypted:s3!b:halconfig!r:us-west-2!f:versions.yml!k:latestSpinnaker");
//    String val = s3Engine.decrypt(encryptedSecret);
//    assertEquals("2.1.0", val);
//  }

}

