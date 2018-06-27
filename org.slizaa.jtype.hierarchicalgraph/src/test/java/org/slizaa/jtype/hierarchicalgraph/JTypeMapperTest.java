/**
 *
 */
package org.slizaa.jtype.hierarchicalgraph;

import static org.slizaa.scanner.core.testfwk.ContentDefinitionProviderFactory.multipleBinaryMvnArtifacts;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.slizaa.core.boltclient.testfwk.BoltClientConnectionRule;
import org.slizaa.hierarchicalgraph.HGRootNode;
import org.slizaa.hierarchicalgraph.HierarchicalgraphPackage;
import org.slizaa.hierarchicalgraph.graphdb.mapping.service.internal.DefaultMappingService;
import org.slizaa.hierarchicalgraph.graphdb.model.GraphDbHierarchicalgraphPackage;
import org.slizaa.hierarchicalgraph.graphdb.model.impl.CustomGraphDbHierarchicalgraphFactoryImpl;
import org.slizaa.hierarchicalgraph.impl.CustomHierarchicalgraphFactoryImpl;
import org.slizaa.scanner.core.testfwk.ConsoleLogProgressMonitor;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 *
 */
public class JTypeMapperTest {

  @ClassRule
  public static JTypeSlizaaTestServerRule _server = new JTypeSlizaaTestServerRule(
      multipleBinaryMvnArtifacts(new String[] { "org.mapstruct", "mapstruct", "1.2.0.Final" },
          new String[] { "org.mapstruct", "mapstruct-processor", "1.2.0.Final" }));

  @Rule
  public BoltClientConnectionRule         _client = new BoltClientConnectionRule();

  @Test
  public void testIt() {

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

    DefaultMappingService defaultMappingService = new DefaultMappingService();

    HGRootNode rootNode = defaultMappingService.convert(new JType_Hierarchical_MappingProvider(),
        this._client.getBoltClient(), new ConsoleLogProgressMonitor());

    System.out.println(rootNode);
  }
}
