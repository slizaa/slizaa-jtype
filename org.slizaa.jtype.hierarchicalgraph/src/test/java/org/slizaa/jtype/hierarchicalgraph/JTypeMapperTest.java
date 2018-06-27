/**
 *
 */
package org.slizaa.jtype.hierarchicalgraph;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slizaa.scanner.core.testfwk.ContentDefinitionProviderFactory.multipleBinaryMvnArtifacts;

import java.util.List;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.slizaa.core.boltclient.testfwk.BoltClientConnectionRule;
import org.slizaa.hierarchicalgraph.HGNode;
import org.slizaa.hierarchicalgraph.HGRootNode;
import org.slizaa.hierarchicalgraph.HierarchicalgraphPackage;
import org.slizaa.hierarchicalgraph.core.algorithms.AdjacencyMatrix;
import org.slizaa.hierarchicalgraph.graphdb.mapping.service.internal.DefaultMappingService;
import org.slizaa.hierarchicalgraph.graphdb.model.GraphDbHierarchicalgraphPackage;
import org.slizaa.hierarchicalgraph.graphdb.model.GraphDbNodeSource;
import org.slizaa.hierarchicalgraph.graphdb.model.impl.CustomGraphDbHierarchicalgraphFactoryImpl;
import org.slizaa.hierarchicalgraph.impl.CustomHierarchicalgraphFactoryImpl;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 *
 */
public class JTypeMapperTest {

  @ClassRule
  public static JTypeSlizaaTestServerRule SERVER = new JTypeSlizaaTestServerRule(
      multipleBinaryMvnArtifacts(new String[] { "com.google.guava", "guava", "23.6.1-jre" }));

  @ClassRule
  public static BoltClientConnectionRule  CLIENT = new BoltClientConnectionRule();

  @BeforeClass
  public static void classInit() {

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

  @Test
  public void testMatrix() {

    HGRootNode rootNode = new DefaultMappingService().convert(new JType_Hierarchical_MappingProvider(),
        CLIENT.getBoltClient(), null);

    //
    List<HGNode> packageNodes = CLIENT.getBoltClient()
        .syncExecCypherQuery("MATCH (:Package {fqn: 'com/google/common'})-[:CONTAINS]->(p:Package) RETURN id(p)")
        .list(record -> rootNode.lookupNode(record.get("id(p)").asLong()));

    //
    int[][] dependencies = AdjacencyMatrix.computeAdjacencyMatrix(packageNodes);

    for (int i = 0; i < dependencies.length; i++) {
      for (int j = 0; j < dependencies[i].length; j++) {
        System.out.print(String.format("%6d", dependencies[i][j]));
      }
      System.out.println();
    }
  }

  @Test
  public void testIt() {

    HGRootNode rootNode = new DefaultMappingService().convert(new JType_Hierarchical_MappingProvider(),
        CLIENT.getBoltClient(), null);

    //
    List<Long> list = CLIENT.getBoltClient().syncExecCypherQuery("MATCH (t:Type) WHERE t.fqn CONTAINS '$' RETURN id(t)")
        .list(record -> record.get("id(t)").asLong());

    for (Long l : list) {
      HGNode node = rootNode.lookupNode(l);
      assertThat(node.getParent().getNodeSource(GraphDbNodeSource.class).get().getLabels()).contains("Type");
    }
  }

  @Test
  public void testIt_2() {

    HGRootNode rootNode = new DefaultMappingService().convert(new JType_Hierarchical_MappingProvider(),
        CLIENT.getBoltClient(), null);

    //
    List<Long> list = CLIENT.getBoltClient().syncExecCypherQuery("MATCH (t:Type) WHERE t.fqn CONTAINS '$' RETURN id(t)")
        .list(record -> record.get("id(t)").asLong());

    for (Long l : list) {
      HGNode node = rootNode.lookupNode(l);
      assertThat(node.getParent().getNodeSource(GraphDbNodeSource.class).get().getLabels()).contains("Type");
    }
  }
}
