/**
 * @slizaa.groupId            org.slizaa.jtype.core
 * @slizaa.statementId        mergeMethodReferences
 * @slizaa.requiredStatements bindMethodReferences
 */
MATCH (sourceNode)-[rel]->(mref:MethodReference)-[:BOUND_TO]->(method:Method) 
RETURN sourceNode, type(rel), method