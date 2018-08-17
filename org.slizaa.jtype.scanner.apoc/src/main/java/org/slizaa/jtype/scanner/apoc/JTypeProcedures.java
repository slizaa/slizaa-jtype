package org.slizaa.jtype.scanner.apoc;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Procedure;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import apoc.result.RelationshipResult;

public class JTypeProcedures {

  /** - */
  @Context
  public GraphDatabaseService db;

  @Procedure(name = "slizaa.jtype.test", mode = Mode.WRITE)
  public Stream<RelationshipResult> test() {

    //
    LoadingCache<String, Node> graphs = CacheBuilder.newBuilder()
        .build(
            new CacheLoader<String, Node>() {
              public Node load(String key) {
                return createExpensiveGraph(key);
              }
            });

    //
    Result result = db.execute("MATCH (tref:TypeReference) WHERE NOT (tref)-[:BOUND_TO]->(:Type) RETURN tref.fqn");
    result.forEachRemaining(map -> {

      //
      String fullyQualifiedName = map.get("tref.fqn").toString();

      String[] packageNames = fullyQualifiedName.split("\\.");
      System.out.println("----");
      String pName = null;
      for (int i = 0; i < packageNames.length - 1; i++) {
        pName = pName == null ? packageNames[i] : pName + '.' + packageNames[i];
        System.out.println(pName);

        packageNodes.computeIfAbsent("hurz", mappingFunction);
      }

      // packageNodes.computeIfAbsent(key, mappingFunction)
      //
      // Node node = db.findNode(Label.label("Package"), "fqn", packageNode);
    });

    // //
    // result = db.execute("MATCH (mref:MethodReference) WHERE NOT (mref)-[:BOUND_TO]->(:Method) RETURN mref");
    // result.forEachRemaining(map -> System.out.println(map.get("mref")));
    //
    // //
    // System.out.println("----------------------------------------");
    //
    // //
    // result = db.execute("MATCH (fref:FieldReference) WHERE NOT (fref)-[:BOUND_TO]->(:Field) RETURN fref");
    // result.forEachRemaining(map -> System.out.println(map.get("fref")));

    // if (!existsOutgoingDerivedRelationship(from, to, originalRelationship.getType())) {
    return Stream.of();
    // }
    // System.out.println(String.format("SKIP: %s - %s - %s",from.getId(), to.getId(), originalRelationship.getType()));
    // return Stream.of();
  }
}
