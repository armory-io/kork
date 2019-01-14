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


import com.netflix.spinnaker.config.secrets.engines.S3BucketEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class S3BucketEngineTest {

  S3BucketEngine s3BucketEngine = new S3BucketEngine();

  @Test
  public void identifier() {
    assertEquals("s3", s3BucketEngine.identifier());
  }

  @Test
  public void validate() {
    EncryptedSecret encryptedSecret = new EncryptedSecret("encrypted:s3!bucket:bucket-name!key:key-name");
    assertTrue(s3BucketEngine.validate(encryptedSecret));
  }

  @Test
  public void invalidParameters() {
    EncryptedSecret encryptedSecret = new EncryptedSecret("encrypted:s3!key:key-name");
    assertFalse(s3BucketEngine.validate(encryptedSecret));
  }

  @Test
  public void decrypt() {
    EncryptedSecret encryptedSecret = new EncryptedSecret("encrypted:s3!bucket:bucket-name");
    assertEquals("key is null", s3BucketEngine.decrypt(encryptedSecret));
  }

  @Test
  public void decryptWithKey() {
    EncryptedSecret encryptedSecret = new EncryptedSecret("encrypted:s3!bucket:bucket-name!key:key-name");
    assertEquals("got a key", s3BucketEngine.decrypt(encryptedSecret));
  }

}

