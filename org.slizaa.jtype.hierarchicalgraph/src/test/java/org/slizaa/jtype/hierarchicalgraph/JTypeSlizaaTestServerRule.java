/**
 *
 */
package org.slizaa.jtype.hierarchicalgraph;

import java.io.File;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.slizaa.core.mvnresolver.api.IMvnResolverService.IMvnResolverJob;
import org.slizaa.hierarchicalgraph.core.model.HierarchicalgraphPackage;
import org.slizaa.hierarchicalgraph.core.model.impl.CustomHierarchicalgraphFactoryImpl;
import org.slizaa.hierarchicalgraph.graphdb.model.GraphDbHierarchicalgraphPackage;
import org.slizaa.hierarchicalgraph.graphdb.model.impl.CustomGraphDbHierarchicalgraphFactoryImpl;
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
    
    configureModel();
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
    
    configureModel();
  }

  /**
   * <p>
   * </p>
   */
  private void configureModel() {
    
    EPackage.Registry.INSTANCE.put(HierarchicalgraphPackage.eNS_URI, new EPackage.Descriptor() {
      @Override
      public EPackage getEPackage() {
        return HierarchicalgraphPackage.eINSTANCE;
      }

      @Override
      public EFactory getEFactory() {
        return new CustomHierarchicalgraphFactoryImpl();
      }
    });

    EPackage.Registry.INSTANCE.put(GraphDbHierarchicalgraphPackage.eNS_URI, new EPackage.Descriptor() {
      @Override
      public EPackage getEPackage() {
        return GraphDbHierarchicalgraphPackage.eINSTANCE;
      }

      @Override
      public EFactory getEFactory() {
        return new CustomGraphDbHierarchicalgraphFactoryImpl();
      }
    });    
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
