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

package com.netflix.spinnaker.kork.plugins.spring

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import spock.lang.Specification
import spock.lang.Subject
import java.nio.file.Paths

class PluginDownloaderSpec extends Specification {

  ObjectMapper objectMapper

  @Subject
  PluginDownloader subject

  def setup() {
    objectMapper = new ObjectMapper(new YAMLFactory());
  }

  def "Should download plugins"() {
    given:
    subject = Spy(PluginDownloader)

    when:
    subject.setPluginDirectory(pluginDirectory)
    subject.downloadJar(pluginDirectory, jarLocation) >> null
    subject.writeJarToLocalFile(_, _) >> null

    then:
    subject.getUrlFromJarLocation(jarLocation) == Paths.get(expected).toUri().toURL()

    where:
    pluginDirectory            | jarLocation                              | expected
    "/opt/spinnaker/plugins/"  | "/home/spinnaker/foo-bar-1.2.3.jar"      | "/home/spinnaker/foo-bar-1.2.3.jar"
    "/opt/spinnaker/plugins/"  | "https://example.com/foo-bar-1.2.3.jar"  | "/opt/spinnaker/plugins/foo-bar-1.2.3.jar"
  }

}
