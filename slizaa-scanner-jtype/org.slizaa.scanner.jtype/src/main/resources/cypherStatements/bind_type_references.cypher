/**
 * @slizaa.groupId     org.slizaa.jtype.core
 * @slizaa.statementId bindTypeReferences
 */
MATCH (t:Type) MATCH (tref:TypeReference) WHERE t.fqn = tref.fqn CREATE (tref)-[:BOUND_TO]->(t)