/**
 * @slizaa.groupId            org.slizaa.jtype.core
 * @slizaa.statementId        mergeMethodReferences
 * @slizaa.requiredStatements bindMethodReferences
 */
MATCH (sourceNode)-[originalRelationship]->(mref:MethodReference)-[:BOUND_TO]->(method:Method) 
CALL slizaa.derivedRelationship(sourceNode, method, originalRelationship) YIELD rel
RETURN rel AS derivedRelationship
