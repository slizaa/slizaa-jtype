package org.slizaa.scanner.jtype.apoc;

import java.util.stream.Stream;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Procedure;

import apoc.result.RelationshipResult;

public class JTypeProcedures {

  /** - */
  @Context
  public GraphDatabaseService db;

  @Procedure(name = "slizaa.jtype.test", mode = Mode.WRITE)
  public Stream<RelationshipResult> test() {

    // if (!existsOutgoingDerivedRelationship(from, to, originalRelationship.getType())) {

    System.out.println("SCHNULLI");

    return Stream.of();
    // }
    // System.out.println(String.format("SKIP: %s - %s - %s",from.getId(), to.getId(), originalRelationship.getType()));
    // return Stream.of();
  }
}
