/**
 *
 */
package org.slizaa.jtype.hierarchicalgraph;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slizaa.scanner.testfwk.ContentDefinitionProviderFactory.multipleBinaryMvnArtifacts;

import org.junit.ClassRule;
import org.junit.Test;
import org.slizaa.core.boltclient.testfwk.BoltClientConnectionRule;
import org.slizaa.hierarchicalgraph.core.model.HGCoreDependency;
import org.slizaa.hierarchicalgraph.core.model.HGProxyDependency;
import org.slizaa.hierarchicalgraph.core.model.HGRootNode;
import org.slizaa.hierarchicalgraph.graphdb.mapping.service.MappingFactory;
import org.slizaa.jtype.hierarchicalgraph.utils.HGNodeUtils;
import org.slizaa.jtype.hierarchicalgraph.utils.JTypeSlizaaTestServerRule;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 *
 */
public class ResolveAggregatedDependenciesTest {

  @ClassRule
  public static JTypeSlizaaTestServerRule SERVER = new JTypeSlizaaTestServerRule(
      multipleBinaryMvnArtifacts(new String[] { "com.fasterxml.jackson.core", "jackson-annotations", "2.5.0" },
          new String[] { "com.fasterxml.jackson.core", "jackson-core", "2.5.1" },
          new String[] { "com.fasterxml.jackson.core", "jackson-databind", "2.5.1" },
          new String[] { "com.fasterxml.jackson.module", "jackson-module-jsonSchema", "2.5.1" },
          new String[] { "com.github.fge", "jackson-coreutils", "1.8" }));

  @ClassRule
  public static BoltClientConnectionRule  CLIENT = new BoltClientConnectionRule();

  @Test
  public void testResolveAggregatedLibraries() {

    //
    HGRootNode rootNode = MappingFactory.createMappingServiceForStandaloneSetup()
        .convert(new JType_Hierarchical_MappingProvider(), CLIENT.getBoltClient(), null);

    //
    int unresolvedCount = 0;

    //
    for (HGCoreDependency dependency : rootNode.getAccumulatedIncomingCoreDependencies()) {

      //
      if (dependency instanceof HGProxyDependency) {

        HGProxyDependency proxyDependency = (HGProxyDependency) dependency;

        //
        proxyDependency.resolveProxyDependencies();

        //
        if (proxyDependency.getResolvedCoreDependencies().size() == 0) {

          //
          unresolvedCount++;

          //
          System.out.println(HGNodeUtils.getProperties(proxyDependency.getFrom()).get("fqn") + " -"
              + proxyDependency.getType() + "-> " + HGNodeUtils.getProperties(proxyDependency.getTo()).get("fqn"));
        }
      }

    }

    //
    assertThat(unresolvedCount == 0);
  }
}
