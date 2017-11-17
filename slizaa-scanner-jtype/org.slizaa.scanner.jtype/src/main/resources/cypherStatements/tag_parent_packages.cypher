/**
 * @slizaa.groupId org.slizaa.jtype.core
 * @slizaa.statementId tagParentPackages
 */
MATCH (n:DIRECTORY)-[:CONTAINS*]->(t:PACKAGE) set n :PACKAGE
