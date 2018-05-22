/**
 * @slizaa.groupId            org.slizaa.jtype.core
 * @slizaa.statementId        mergeFieldReferences
 * @slizaa.requiredStatements bindFieldReferences
 */
MATCH (sourceNode)-[rel]->(fref:FieldReference)-[:BOUND_TO]->(field:Field) RETURN sourceNode, type(rel), field