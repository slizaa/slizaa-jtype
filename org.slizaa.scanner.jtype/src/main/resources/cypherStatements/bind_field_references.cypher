/**
 * @slizaa.groupId            org.slizaa.jtype.core
 * @slizaa.statementId        bindFieldReferences
 * @slizaa.requiredStatements bindTypeReferences
 */
MATCH (fref:FieldReference) MATCH (f:Field) WHERE fref.fqn = f.fqn CREATE (fref)-[:BOUND_TO {derived:true}]->(f)