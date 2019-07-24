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

package com.netflix.spinnaker.kork.plugins.spring;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

@Component
public class PluginDownloader {

  @Setter public String pluginDirectory;
  private OkHttpClient okHttpClient;

  PluginDownloader(String pluginDirectory, OkHttpClient okHttpClient) {
    this.pluginDirectory = pluginDirectory;
    this.okHttpClient = okHttpClient;
  }

  PluginDownloader() {
    this(
        Optional.ofNullable(System.getenv("PLUGIN_JAR_DIRECTORY_LOCATION"))
            .orElse(PluginLoader.DEFAULT_PLUGIN_DIRECTORY),
        new OkHttpClient());
  }

  /**
   * This method returns a list of local filepaths to jars. If any jars are not local, it will
   * download the jars and put them in a configurable location.
   *
   * @param pluginConfigurations
   * @return
   * @throws IOException
   */
  public URL[] downloadPluginJars(List<PluginProperties.PluginConfiguration> pluginConfigurations)
      throws IOException {
    List<String> pluginLocations = getPluginLocations(pluginConfigurations);
    List<URL> urls = new ArrayList<>();
    for (String pluginLocation : pluginLocations) {
      urls.add(getUrlFromJarLocation(pluginLocation));
    }
    return urls.toArray(new URL[0]);
  }

  /**
   * Get a list of jar locations across all pluginConfigurations
   *
   * @param pluginConfigurations
   * @return
   */
  private List<String> getPluginLocations(
      List<PluginProperties.PluginConfiguration> pluginConfigurations) {
    return pluginConfigurations.stream()
        .map(PluginProperties.PluginConfiguration::getJars)
        .flatMap(Collection::stream)
        .distinct()
        .collect(Collectors.toList());
  }

  /**
   * This method returns the local filepath for a single jar. If the jar is not present, it will
   * download the jar.
   *
   * @param jarLocation
   * @return
   * @throws IOException
   */
  public URL getUrlFromJarLocation(String jarLocation) throws IOException {
    if (jarLocation.matches("https?.*")) {
      InputStream inputStream = downloadJar(pluginDirectory, jarLocation);
      jarLocation = pluginDirectory + FilenameUtils.getBaseName(jarLocation) + ".jar";
      writeJarToLocalFile(jarLocation, inputStream);
    }

    return Paths.get(jarLocation).toUri().toURL();
  }

  /**
   * Given an inputStream and a localJarLocation, write the input stream to that file.
   *
   * @param localJarLocation
   * @param inputStream
   * @throws IOException
   */
  public void writeJarToLocalFile(String localJarLocation, InputStream inputStream)
      throws IOException {
    File localFile = new File(localJarLocation);
    FileUtils.copyInputStreamToFile(inputStream, localFile);
  }

  /**
   * Returns an input stream that contains the contents of the jar
   *
   * @param jarLocation
   * @return
   * @throws IOException
   */
  public InputStream downloadJar(String jarLocation) throws IOException {
    Request request = new Request.Builder().url(jarLocation).method("GET", null).build();
    Response response = okHttpClient.newCall(request).execute();
    return response.body().byteStream();
  }
}
