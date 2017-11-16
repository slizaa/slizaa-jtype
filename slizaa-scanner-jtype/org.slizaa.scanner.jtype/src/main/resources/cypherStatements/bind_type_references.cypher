/**
 * @slizaa.group       org.slizaa.jtype.core
 * @slizaa.name        bindTypeReferences
 * @slizaa.description 
 */
MATCH (t:TYPE) MATCH (tref:TYPE_REFERENCE) WHERE t.fqn = tref.fqn CREATE (tref)-[:BOUND_TO]->(t)