/**
 * @slizaa.groupId            org.slizaa.jtype.core
 * @slizaa.statementId        bindMethodReferences
 * @slizaa.requiredStatements bindTypeReferences
 */
MATCH (mref:MethodReference) MATCH (m:Method) WHERE mref.name = m.name AND mref.fqn = m.fqn AND NOT (mref)-[:BOUND_TO]->(m) CREATE (mref)-[:BOUND_TO {derived:true}]->(m)
