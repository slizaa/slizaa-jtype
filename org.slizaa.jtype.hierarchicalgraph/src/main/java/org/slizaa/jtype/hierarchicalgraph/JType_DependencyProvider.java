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
        new String[] {
            "Match (t1:Type)-[r:DEPENDS_ON]->(tref:TypeReference)-[:BOUND_TO]->(t2:Type) RETURN id(t1), id(t2), id(r), 'DEPENDS_ON'",
            "Match (t1:Type)-[r:DEPENDS_ON]->(tref:TypeReference)-[:BOUND_TO]->(t2:MissingType) RETURN id(t1), id(t2), id(r), 'DEPENDS_ON'"
        },
        new String[] {
            "MATCH (n1)-[rel]->(ref)-[:BOUND_TO]->(n2) "
            + "WHERE id(n1) in {from} AND id(n2) in {to} "
            + "AND ("
            + "(n1:Type)-[rel:EXTENDS|:IMPLEMENTS]->(ref:TypeReference) "
            + "OR (n1:Type)-[rel:ANNOTATED_BY]->(ref:TypeReference)"
            + "OR (n1:Type)-[rel:REFERENCES]->(ref:TypeReference) "
            + "OR (n1:Type)-[rel:DEFINES_INNER_CLASS]->(ref:TypeReference)"
            + "OR (n1:Type)-[rel:IS_INNER_CLASS_DEFINED_BY]->(ref:TypeReference)"
            + "OR (n1:Field)-[rel:IS_OF_TYPE]->(ref:TypeReference) "
            + "OR (n1:Field)-[rel:ANNOTATED_BY]->(ref:TypeReference) "
            + "OR (n1:Field)-[rel:REFERENCES]->(ref:TypeReference) "
            + "OR (n1:Method)-[rel:INVOKES]->(ref:MethodReference) "
            + "OR (n1:Method)-[rel:READS|:WRITES]->(ref:FieldReference) "
            + "OR (n1:Method)-[rel:READS_FIELD_OF_TYPE|:WRITES_FIELD_OF_TYPE]->(ref:TypeReference) "
            + "OR (n1:Method)-[rel:THROWS]->(ref:TypeReference) "
            + "OR (n1:Method)-[rel:RETURNS]->(ref:TypeReference) "
            + "OR (n1:Method)-[rel:HAS_PARAMETER]->(ref:TypeReference)"
            + "OR (n1:Method)-[rel:DEFINES_LOCAL_VARIABLE]->(ref:TypeReference) "
            + "OR (n1:Method)-[rel:USES_TYPE_CONSTANT]->(ref:TypeReference) "
            + "OR (n1:Method)-[rel:ANNOTATED_BY]->(ref:TypeReference) "
            + "OR (n1:Method)-[rel:INVOKES_METHOD_FROM]->(ref:TypeReference) "
            + "OR (n1:Method)-[rel:INVOKED_METHOD_RETURNS]->(ref:TypeReference) "
            + "OR (n1:Method)-[rel:INVOKED_METHOD_HAS_PARAMETER]->(ref:TypeReference) "
            + "OR (n1:Method)-[rel:REFERENCES]->(ref:TypeReference) "

            + ") "
            + "RETURN id(n1), id(n2), id(rel), type(rel)"
        });
    // @formatter:on
  }
}
