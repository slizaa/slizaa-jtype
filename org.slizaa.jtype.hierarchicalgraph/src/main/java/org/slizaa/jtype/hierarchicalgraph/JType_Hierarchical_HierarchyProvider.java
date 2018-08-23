package org.slizaa.jtype.hierarchicalgraph;

import org.slizaa.hierarchicalgraph.graphdb.mapping.cypher.AbstractQueryBasedHierarchyProvider;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class JType_Hierarchical_HierarchyProvider extends AbstractQueryBasedHierarchyProvider {

  /**
   * <p>
   * </p>
   *
   * @return
   */
  @Override
  protected String[] toplevelNodeIdQueries() {
    return new String[] { "Match (module:Module) Return id(module) as id" };
  }

  /**
   * <p>
   * </p>
   *
   * @return
   */
  @Override
  protected String[] parentChildNodeIdsQueries() {
    return new String[] { "MATCH (g:Group)-[:CONTAINS]->(m:Module) RETURN id(g), id(m)",
        "MATCH (g1:Group)-[:CONTAINS]->(g2:Group) RETURN id(g1), id(g2)",
        "MATCH (module:Module)-[:CONTAINS]->(d:Directory) WHERE NOT (:Directory)-[:CONTAINS]->(d) RETURN id(module), id(d)",
        "MATCH (d1:Directory)-[:CONTAINS]->(d2:Directory) RETURN id(d1), id(d2)",
        "MATCH (d:Directory)-[:CONTAINS]->(mt:MissingType) RETURN id(d), id(mt)",
        "MATCH (d:Directory)-[:CONTAINS]->(r:Resource) WHERE NOT (r)-[:CONTAINS]->(:Type {innerClass: true}) RETURN id(d), id(r)",
        "MATCH (r:Resource)-[:CONTAINS]->(t:Type) WHERE NOT EXISTS(t.innerClass) RETURN id(r), id(t)",
        "MATCH (t:Type)-[rel:DEFINES_INNER_CLASS]->(target)-[:BOUND_TO]->(boundedTarget) RETURN id(t), id(boundedTarget)",
        "MATCH (t:Type)-[:CONTAINS]->(m:Method) RETURN id(t), id(m)",
        "MATCH (t:Type)-[:CONTAINS]->(f:Field) RETURN id(t), id(f)" };
  }
}
