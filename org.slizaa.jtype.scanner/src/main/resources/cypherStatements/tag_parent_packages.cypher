/**
 * @slizaa.groupId org.slizaa.jtype.core
 * @slizaa.statementId tagParentPackages
 */
MATCH (n:Directory)-[:CONTAINS*]->(t:Package) set n :Package
