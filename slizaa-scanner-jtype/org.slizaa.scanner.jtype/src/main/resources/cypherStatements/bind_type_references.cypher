/**
 * @slizaa.groupId     org.slizaa.jtype.core
 * @slizaa.statementId bindTypeReferences
 */
MATCH (t:TYPE) MATCH (tref:TYPE_REFERENCE) WHERE t.fqn = tref.fqn CREATE (tref)-[:BOUND_TO]->(t)