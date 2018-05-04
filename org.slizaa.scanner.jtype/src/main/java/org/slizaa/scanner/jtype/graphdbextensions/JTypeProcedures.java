package org.slizaa.scanner.jtype.graphdbextensions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

public class JTypeProcedures {

  @Context
  public GraphDatabaseService db;

  /** log instance to the standard log, `neo4j.log` */
  @Context
  public Log                  log;

  /**
   * <p>
   * </p>
   *
   * @param module
   * @return
   */
  @Procedure(name = "slizaa.createModule", mode = Mode.WRITE)
  public Stream<Output> createModule(@Name("fqn") String fqn, @Name("version") String version) {

    //
    List<Output> result = new LinkedList<>();

    //
    Node moduleNode = CreatorHelper.createModule(db, new FullyQualifiedName(checkNotNull(fqn)), version);
    result.add(new Output(moduleNode));

    //
    return result.stream();
  }

  @Procedure(name = "slizaa.createGroup", mode = Mode.WRITE)
  public Stream<Output> createGroup(@Name("groupFqn") String groupFqn) {

    //
    List<Output> result = new LinkedList<>();

    //
    result.add(new Output(CreatorHelper.createGroup(db, new FullyQualifiedName(groupFqn))));

    //
    return result.stream();
  }

  @Procedure(name = "slizaa.dump", mode = Mode.READ)
  public void dump(@Name("file") String file) {

    for (Node node : db.getAllNodes()) {
      System.out.println(node.getId() + " : " + node.getLabels() + " : " + node.getAllProperties());
    }
  }

  public class Output {
    public Node node;

    public Output(Node node) {
      this.node = node;
    }
  }

}