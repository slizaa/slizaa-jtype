/**
 *
 */
package org.slizaa.jtype.hierarchicalgraph;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slizaa.scanner.core.testfwk.ContentDefinitionProviderFactory.multipleBinaryMvnArtifacts;

import java.util.Arrays;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Test;
import org.slizaa.core.boltclient.testfwk.BoltClientConnectionRule;
import org.slizaa.hierarchicalgraph.core.algorithms.AdjacencyMatrix;
import org.slizaa.hierarchicalgraph.core.model.HGNode;
import org.slizaa.hierarchicalgraph.core.model.HGRootNode;
import org.slizaa.hierarchicalgraph.graphdb.mapping.service.MappingFactory;
import org.slizaa.jtype.hierarchicalgraph.utils.JTypeSlizaaTestServerRule;

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

  @Test
  public void testMatrix() {

    HGRootNode rootNode = MappingFactory.createMappingServiceForStandaloneSetup()
        .convert(new JType_Hierarchical_MappingProvider(), CLIENT.getBoltClient(), null);

    //
    List<HGNode> packageNodes = CLIENT.getBoltClient()
        .syncExecCypherQuery("MATCH (:Package {fqn: 'com/google/common'})-[:CONTAINS]->(p:Package) RETURN id(p)")
        .list(record -> rootNode.lookupNode(record.get("id(p)").asLong()));

    //
    int[][] dependencies = AdjacencyMatrix.computeAdjacencyMatrix(packageNodes);

    for (int[] deps : dependencies) {
      System.out.println(Arrays.toString(deps));
    }

    //
    assertThat(dependencies).isEqualTo(new int[][] { { 74, 0, 0, 0, 0, 20, 0, 0, 0, 0, 0, 0, 18, 9, 0, 0 },
        { 0, 235, 0, 0, 0, 47, 3, 0, 0, 0, 0, 0, 15, 1, 76, 0 },
        { 0, 0, 484, 22, 0, 71, 0, 0, 0, 0, 0, 0, 21, 1, 24, 0 },
        { 2, 0, 0, 867, 0, 118, 0, 0, 0, 0, 0, 0, 123, 4, 67, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 495, 0, 0, 0, 0, 0, 0, 58, 0, 0, 0 }, { 3, 0, 0, 0, 0, 70, 237, 9, 0, 0, 0, 0, 55, 4, 14, 6 },
        { 2, 0, 0, 0, 0, 36, 0, 294, 0, 0, 0, 0, 11, 16, 0, 0 }, { 0, 0, 0, 0, 0, 30, 2, 3, 14, 0, 0, 2, 15, 1, 14, 0 },
        { 0, 4, 5, 2, 0, 15, 0, 0, 0, 66, 0, 0, 6, 0, 12, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 11, 0, 0, 0, 0, 0, 35, 17, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 36, 0, 0, 0, 0, 0, 0, 34, 106, 0, 0 },
        { 12, 0, 0, 0, 0, 443, 0, 0, 0, 0, 0, 0, 283, 22, 4114, 0 },
        { 3, 0, 0, 0, 0, 55, 0, 0, 0, 0, 0, 0, 21, 1, 75, 372 } });
  }
}
