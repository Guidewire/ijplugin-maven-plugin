/**
 * Copyright 2013-2014 Guidewire Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.guidewire.build.ijdevkitmvn.maven;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.File;
import java.util.Collections;

/**
 * Mojo that automatically records IntelliJ plugin manifest as resource.
 */
@Mojo(name = "manifest", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, threadSafe = true)
public class ManifestMojo extends AbstractMojo {
  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  @Parameter(defaultValue = "META-INF/plugin.xml")
  private File manifestLocation;

  @Component(role = MavenProjectHelper.class, hint = "default")
  protected MavenProjectHelper mavenProjectHelper;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("Adding plugin manifest at " + manifestLocation + " as resource");

    Resource resource = new Resource();
    resource.setDirectory(manifestLocation.getParent());
    resource.setIncludes(Collections.singletonList(manifestLocation.getName()));
    resource.setTargetPath("META-INF");
    resource.setFiltering(true);
    project.addResource(resource);
  }
}