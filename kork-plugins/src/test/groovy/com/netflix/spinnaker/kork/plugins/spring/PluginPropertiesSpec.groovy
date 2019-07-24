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
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification
import spock.lang.Subject

import java.nio.file.Paths

class PluginPropertiesSpec extends Specification {

  ByteArrayInputStream inputStream
  ObjectMapper objectMapper
  PluginProperties pluginProperties
  ArrayList<PluginProperties.PluginConfiguration> pluginConfigurationArrayList

  @Subject
  PluginLoader subject

  def setup() {
    objectMapper = new ObjectMapper(new YAMLFactory());
  }

  def "Should validate only unique plugins are configured"() {
    when:
    subject = new PluginLoader()
    PluginProperties.PluginConfiguration pluginConfiguration1 = new PluginProperties.PluginConfiguration("foo/bar", [], true)
    PluginProperties.PluginConfiguration pluginConfiguration2 = new PluginProperties.PluginConfiguration("foo/bar", [], true)
    pluginConfigurationArrayList = [ pluginConfiguration1, pluginConfiguration2 ]
    pluginProperties = new PluginProperties(pluginConfigurationArrayList)
    pluginProperties.validate()

    then:
    thrown MalformedPluginConfigurationException
  }

  def "Should allow multiple unique plugins to be configured"() {
    when:
    subject = new PluginLoader()
    PluginProperties.PluginConfiguration pluginConfiguration1 = new PluginProperties.PluginConfiguration("foo/bar", [], true)
    PluginProperties.PluginConfiguration pluginConfiguration2 = new PluginProperties.PluginConfiguration("cat/dog", [], true)
    pluginConfigurationArrayList = [ pluginConfiguration1, pluginConfiguration2 ]
    pluginProperties = new PluginProperties(pluginConfigurationArrayList)
    pluginProperties.validate()

    then:
    noExceptionThrown()
  }

  def "Should plugin name is namespaced"() {
    when:
    subject = new PluginLoader()
    PluginProperties.PluginConfiguration pluginConfiguration1 = new PluginProperties.PluginConfiguration("bar", [], true)
    pluginConfigurationArrayList = [ pluginConfiguration1 ]
    pluginProperties = new PluginProperties(pluginConfigurationArrayList)
    pluginProperties.validate()

    then:
    thrown MalformedPluginConfigurationException
  }

  def "Should allow for namespaced plugins"() {
    when:
    subject = new PluginLoader()
    PluginProperties.PluginConfiguration pluginConfiguration1 = new PluginProperties.PluginConfiguration("foo/bar", [], true)
    pluginConfigurationArrayList = [ pluginConfiguration1 ]
    pluginProperties = new PluginProperties(pluginConfigurationArrayList)
    pluginProperties.validate()

    then:
    noExceptionThrown()

  }

}
