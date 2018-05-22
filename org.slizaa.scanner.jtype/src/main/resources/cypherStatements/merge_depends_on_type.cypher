/**
 * @slizaa.groupId            org.slizaa.jtype.core
 * @slizaa.statementId        mergeDependsOnType
 * @slizaa.requiredStatements bindTypeReferences
 */
MATCH (t1:Type)-[:DEPENDS_ON]->(:TypeReference)-[:BOUND_TO]->(t2:Type) MERGE (t1)-[:DEPENDS_ON { derived: true }]->(t2)