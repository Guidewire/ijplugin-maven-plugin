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

import java.util.Collections;

/**
 * Mojo that automatically records IntelliJ plugin manifest as resource.
 */
@Mojo(name = "manifest", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, threadSafe = true)
public class ManifestMojo extends AbstractMojo {
  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  @Parameter(defaultValue = "META-INF/plugin.xml")
  private String manifestLocation;

  @Component(role = MavenProjectHelper.class, hint = "default")
  protected MavenProjectHelper mavenProjectHelper;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("Adding plugin manifest at " + manifestLocation + " as resource");

    Resource resource = new Resource();
    resource.setDirectory(".");
    resource.setIncludes(Collections.<String>singletonList(manifestLocation));
    resource.setFiltering(true);
    project.addResource(resource);
  }
}