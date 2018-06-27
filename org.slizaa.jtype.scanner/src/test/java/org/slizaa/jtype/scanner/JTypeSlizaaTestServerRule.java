/**
 *
 */
package org.slizaa.jtype.scanner;

import java.io.File;
import java.util.function.Consumer;

import org.slizaa.core.mvnresolver.api.IMvnResolverService.IMvnResolverJob;
import org.slizaa.scanner.core.spi.contentdefinition.IContentDefinitionProvider;
import org.slizaa.scanner.core.testfwk.SlizaaTestServerRule;

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
        .withDependency("org.slizaa.scanner.neo4j:org.slizaa.scanner.neo4j.importer:1.0.0-SNAPSHOT")
        .withDependency("org.slizaa.scanner.neo4j:org.slizaa.scanner.neo4j.graphdbfactory:1.0.0-SNAPSHOT")
        .withExclusionPattern("*:org.slizaa.scanner.core.spi-api")
        .withExclusionPattern("*:jdk.tools");
    // @formatter:on
  }
}
