/**
 *
 */
package org.slizaa.jtype.hierarchicalgraph.utils;

import java.io.File;
import java.util.function.Consumer;

import org.slizaa.core.mvnresolver.api.IMvnResolverService.IMvnResolverJob;
import org.slizaa.scanner.spi.contentdefinition.IContentDefinitionProvider;
import org.slizaa.scanner.testfwk.SlizaaTestServerRule;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class JTypeSlizaaTestServerRule extends SlizaaTestServerRule {

  /**
   * <p>
   * Creates a new instance of type {@link JTypeSlizaaTestServerRule}.
   * </p>
   *
   * @param workingDirectory
   * @param contentDefinitions
   */
  public JTypeSlizaaTestServerRule(File workingDirectory, IContentDefinitionProvider contentDefinitions) {
    super(workingDirectory, contentDefinitions, createBackendLoaderConfigurer());
  }

  /**
   * <p>
   * Creates a new instance of type {@link JTypeSlizaaTestServerRule}.
   * </p>
   *
   * @param contentDefinitions
   */
  public JTypeSlizaaTestServerRule(IContentDefinitionProvider contentDefinitions) {
    super(contentDefinitions, createBackendLoaderConfigurer());
  }

  /**
   * <p>
   * </p>
   *
   * @return
   */
  private static Consumer<IMvnResolverJob> createBackendLoaderConfigurer() {

    // @formatter:off
    return job -> job
        .withDependency(mavenArtifact("org.slizaa.neo4j", "org.slizaa.neo4j.importer", versionAsInProject()))
        .withDependency(mavenArtifact("org.slizaa.neo4j", "org.slizaa.neo4j.graphdbfactory", versionAsInProject()))
        .withDependency(mavenArtifact("org.slizaa.jtype", "org.slizaa.jtype.scanner", versionAsInProject()))
        .withDependency(mavenArtifact("org.slizaa.jtype", "org.slizaa.jtype.scanner.apoc", versionAsInProject()))
        .withExclusionPattern("*:org.slizaa.scanner.spi-api")
        .withExclusionPattern("*:jdk.tools");
    // @formatter:on
  }
}
