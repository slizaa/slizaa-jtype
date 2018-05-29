/**
 * @slizaa.groupId            org.slizaa.jtype.core
 * @slizaa.statementId        mergeFieldReferences
 * @slizaa.requiredStatements bindFieldReferences
 */
MATCH (sourceNode)-[originalRelationship]->(fref:FieldReference)-[:BOUND_TO]->(field:Field) 
CALL slizaa.derivedRelationship(sourceNode, field, originalRelationship) YIELD rel
RETURN rel AS derivedRelationship
