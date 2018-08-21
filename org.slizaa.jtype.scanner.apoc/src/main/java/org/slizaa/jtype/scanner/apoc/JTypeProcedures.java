package org.slizaa.jtype.scanner.apoc;

import java.util.stream.Stream;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Result;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Procedure;

import apoc.result.RelationshipResult;

public class JTypeProcedures {

  /** - */
  @Context
  public GraphDatabaseService _databaseService;

  @Procedure(name = "slizaa.jtype.createMissingTypes", mode = Mode.WRITE)
  public Stream<RelationshipResult> test() {

    //
    final VirtualNodesCreator virtualPackageCreator = new VirtualNodesCreator(_databaseService);

    //
    Result result = _databaseService
        .execute("MATCH (tref:TypeReference) WHERE NOT (tref)-[:BOUND_TO]->(:Type) RETURN tref, tref.fqn");

    //
    result.forEachRemaining(map -> {

      //
      String fullyQualifiedPackageName = map.get("tref.fqn").toString();

      //
      Node typeReferenceNode = (Node) map.get("tref");
      Node virtualTypeNode = virtualPackageCreator.getOrCreateVirtualType(fullyQualifiedPackageName);

      //
      typeReferenceNode.createRelationshipTo(virtualTypeNode, RelationshipType.withName("BOUND_TO"));
    });

    //
    return Stream.of();
  }
}
