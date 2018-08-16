package org.slizaa.jtype.scanner.apoc;

import java.util.stream.Stream;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
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

    System.out.println("HURRA");
    
    //
    Result result = db.execute("MATCH (tref:TypeReference) WHERE NOT (tref)-[:BOUND_TO]->(:Type) RETURN tref");
    result.forEachRemaining(map -> System.out.println(map.get("tref")));
    
    System.out.println("----------------------------------------");
    
    //
    result = db.execute("MATCH (mref:MethodReference) WHERE NOT (mref)-[:BOUND_TO]->(:Method) RETURN mref");
    result.forEachRemaining(map -> System.out.println(map.get("mref")));
    
    //
    System.out.println("----------------------------------------");

    //
    result = db.execute("MATCH (fref:FieldReference) WHERE NOT (fref)-[:BOUND_TO]->(:Field) RETURN fref");
    result.forEachRemaining(map -> System.out.println(map.get("fref")));
    
    // if (!existsOutgoingDerivedRelationship(from, to, originalRelationship.getType())) {
    return Stream.of();
    // }
    // System.out.println(String.format("SKIP: %s - %s - %s",from.getId(), to.getId(), originalRelationship.getType()));
    // return Stream.of();
  }
}
