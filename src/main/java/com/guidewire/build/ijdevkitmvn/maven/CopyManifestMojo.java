package com.guidewire.build.ijdevkitmvn.maven;

import com.google.common.io.Files;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;

/**
 * Mojo that automatically copies IntelliJ plugin manifest to the output directory directory.
 */
@Mojo(name = "copy-manifest", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, threadSafe = true)
public class CopyManifestMojo extends AbstractMojo {

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  @Parameter(defaultValue = "META-INF/plugin.xml")
  private File manifestLocation;

  @Parameter(defaultValue = "META-INF/plugin.xml")
  private String manifestDestination;

  @Parameter(property = "project.build.outputDirectory", required = true)
  private File outputDirectory;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    File destination = new File(outputDirectory, manifestDestination);
    getLog().info("Copying plugin manifest at " + manifestLocation + " to " + destination);

    try {
      Files.createParentDirs(destination);
      Files.copy(manifestLocation, destination);
    } catch (IOException e) {
      throw new MojoExecutionException("Cannot copy manifest file: " + e.getMessage(), e);
    }
  }
}