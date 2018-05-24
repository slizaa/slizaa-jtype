/**
 * @slizaa.groupId     org.slizaa.jtype.core
 * @slizaa.statementId bindTypeReferences
 */
MATCH (t:Type) MATCH (tref:TypeReference) WHERE t.fqn = tref.fqn AND NOT (tref)-[:BOUND_TO]->(t) CREATE (tref)-[:BOUND_TO { derived: true }]->(t)