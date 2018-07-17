package org.slizaa.jtype.hierarchicalgraph;

import org.slizaa.hierarchicalgraph.graphdb.mapping.cypher.AbstractQueryBasedDependencyProvider;

public class JType_DependencyProvider extends AbstractQueryBasedDependencyProvider {

  /**
   * {@inheritDoc}
   */
  @Override
  protected void initialize() {

    // @formatter:off
    addProxyDependencyDefinitions(
        "Match (t1:Type)-[r:DEPENDS_ON]->(tref:TypeReference)-[:BOUND_TO]->(t2:Type) RETURN id(t1), id(t2), id(r), 'DEPENDS_ON'",
        new String[] {
            "MATCH (n1)-[rel]->(ref)-[:BOUND_TO]->(n2) "
            + "WHERE id(n1) in {from} AND id(n2) in {to} "
            + "AND ("
            + "(n1:Type)-[rel:EXTENDS|:IMPLEMENTS]->(ref:TypeReference) "
            + "OR (n1:Method)-[rel:INVOKES]->(ref:MethodReference) "
            + "OR (n1:Method)-[rel:READS|:WRITES]->(ref:FieldReference) "
            + "OR (n1:Field)-[rel:IS_OF_TYPE]->(ref:TypeReference) "
            + "OR (n1:Method)-[rel:THROWS]->(ref:TypeReference) "
            + "OR (n1:Method)-[rel:RETURNS]->(ref:TypeReference)"
            + "OR (n1:Method)-[rel:HAS_PARAMETER]->(ref:TypeReference)"
            + ") "
            + "RETURN id(n1), id(n2), id(rel), type(rel)"
        });
    // @formatter:on
  }
}
