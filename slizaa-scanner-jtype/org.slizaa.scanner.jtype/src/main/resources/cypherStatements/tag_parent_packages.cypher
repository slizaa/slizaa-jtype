/**
 * @slizaaStatement.group       org.slizaa.jtype.core
 * @slizaaStatement.name        tagParentPackages
 * @slizaaStatement.description Tags directories with tag PACKAGE if they contain packages
 */
MATCH (n:DIRECTORY)-[:CONTAINS*]->(t:PACKAGE) set n :PACKAGE
