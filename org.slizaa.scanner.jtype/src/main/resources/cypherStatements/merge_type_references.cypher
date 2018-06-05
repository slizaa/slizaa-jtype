/**
 * @slizaa.groupId            org.slizaa.jtype.core
 * @slizaa.statementId        mergeTypeReferences
 * @slizaa.requiredStatements bindTypeReferences
 */
MATCH (sourceNode)-[originalRelationship]->(tref:TypeReference)-[:BOUND_TO]->(type:Type) 
CALL slizaa.derivedRelationship(sourceNode, type, originalRelationship) YIELD rel
RETURN rel AS derivedRelationship