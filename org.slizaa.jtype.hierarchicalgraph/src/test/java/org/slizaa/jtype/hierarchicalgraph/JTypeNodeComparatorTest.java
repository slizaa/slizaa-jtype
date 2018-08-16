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
import org.slizaa.hierarchicalgraph.graphdb.model.GraphDbNodeSource;
import org.slizaa.jtype.hierarchicalgraph.utils.HGNodeUtils;
import org.slizaa.jtype.hierarchicalgraph.utils.JTypeSlizaaTestServerRule;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 *
 */
public class JTypeNodeComparatorTest {

  @ClassRule
  public static JTypeSlizaaTestServerRule SERVER = new JTypeSlizaaTestServerRule(
      multipleBinaryMvnArtifacts(new String[] { "com.google.guava", "guava", "23.6.1-jre" }));

  @ClassRule
  public static BoltClientConnectionRule  CLIENT = new BoltClientConnectionRule();

  @Test
  public void testNodeComparator() {

    HGRootNode rootNode = MappingFactory.createMappingServiceForStandaloneSetup()
        .convert(new JType_Hierarchical_MappingProvider(), CLIENT.getBoltClient(), null);

    //
    List<HGNode> packageNodes = CLIENT.getBoltClient()
        .syncExecCypherQuery("MATCH (p:Package {fqn: 'com/google/common/base'}) RETURN id(p)")
        .list(record -> rootNode.lookupNode(record.get("id(p)").asLong()));

    //
    assertThat(packageNodes).hasSize(1);

    List<HGNode> children = packageNodes.get(0).getChildren();

    //
    for (HGNode child : children) {
      GraphDbNodeSource nodeSource = child.getNodeSource(GraphDbNodeSource.class).get();
//      System.out.println(nodeSource.getLabels() + " : " + nodeSource.getProperties());
    }
  }
}
