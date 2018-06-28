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

    assertThat(dependencies).isEqualTo(new int[][] { { 74, 0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 18, 9, 0, 0 },
        { 0, 235, 0, 0, 0, 47, 3, 0, 0, 0, 0, 0, 15, 1, 76, 0 },
        { 0, 0, 483, 22, 0, 71, 0, 0, 0, 0, 0, 0, 21, 1, 24, 0 },
        { 2, 0, 0, 866, 0, 118, 0, 0, 0, 0, 0, 0, 123, 4, 67, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 495, 0, 0, 0, 0, 0, 0, 58, 0, 0, 0 }, { 3, 0, 0, 0, 0, 70, 237, 9, 0, 0, 0, 0, 55, 4, 14, 6 },
        { 2, 0, 0, 0, 0, 36, 0, 294, 0, 0, 0, 0, 11, 16, 0, 0 }, { 0, 0, 0, 0, 0, 30, 2, 3, 14, 0, 0, 2, 15, 1, 14, 0 },
        { 0, 4, 5, 2, 0, 15, 0, 0, 0, 66, 0, 0, 6, 0, 12, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 11, 0, 0, 0, 0, 0, 35, 17, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 36, 0, 0, 0, 0, 0, 0, 34, 106, 0, 0 },
        { 12, 0, 0, 0, 0, 443, 0, 0, 0, 0, 0, 0, 283, 22, 4105, 0 },
        { 3, 0, 0, 0, 0, 55, 0, 0, 0, 0, 0, 0, 21, 1, 75, 372 } });
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
