package com.guidewire.build.ijdevkitmvn.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.shared.artifact.filter.collection.ArtifactFilterException;
import org.apache.maven.shared.artifact.filter.collection.FilterArtifacts;
import org.apache.maven.shared.artifact.filter.collection.ScopeFilter;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import java.io.File;
import java.io.IOException;
import java.util.Set;

@Mojo(name = "package-plugin", defaultPhase = LifecyclePhase.PACKAGE,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        threadSafe = true)
public class PackagePluginMojo extends AbstractMojo {

  @Parameter(property = "project", required = true, readonly = true)
  private MavenProject project;

  @Parameter(property = "project.build.outputDirectory", required = true)
  private String outputDirectory;

  @Parameter(defaultValue = "plugin")
  private String classifier;

  @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}-plugin.zip", required = true)
  private File destFile;

  @Parameter(defaultValue = "true")
  private boolean forceZip;

  @Parameter(defaultValue = Artifact.SCOPE_RUNTIME)
  private String includeScope;

  @Parameter
  private String excludeScope;

  @Component(hint = "zip", role = Archiver.class)
  private ZipArchiver zipArchiver;

  @Component
  private MavenProjectHelper projectHelper;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("Packaging IntelliJ plugin");

    Set<Artifact> libs;
    try {
      FilterArtifacts filter = new FilterArtifacts();
      filter.addFilter(new ScopeFilter(includeScope, excludeScope));
      libs = filter.filter(project.getArtifacts());

    } catch (ArtifactFilterException e) {
      throw new MojoFailureException(e.getMessage(), e);
    }

    if (libs.isEmpty() && !forceZip) {
      // .jar-based plugin, skip execution
      getLog().warn("Plugin does not require packaging dependencies and zip-packaging is not forced, " +
              "skipping .zip packaging");
      return;
    }

    // .zip-based plugin
    String prefix = project.getArtifactId() + "/lib/";
    try {
      File jar = project.getArtifact().getFile();
      zipArchiver.setDestFile(destFile);
      for (Artifact lib : libs) {
        zipArchiver.addFile(lib.getFile(), prefix + lib.getFile().getName());
      }
      zipArchiver.addFile(jar, prefix + jar.getName());
      zipArchiver.createArchive();
    } catch (IOException e) {
      throw new MojoFailureException("Cannot build plugin archive", e);
    }

    if (classifier != null) {
      projectHelper.attachArtifact(project, "zip", classifier, destFile);
    }
  }
}
