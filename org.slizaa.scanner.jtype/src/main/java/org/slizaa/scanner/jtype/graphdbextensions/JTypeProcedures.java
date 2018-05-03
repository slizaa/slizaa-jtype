package org.slizaa.scanner.jtype.graphdbextensions;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;
import org.neo4j.procedure.UserFunction;

public class JTypeProcedures {

  @Context
  public GraphDatabaseService db;

  /** log instance to the standard log, `neo4j.log` */
  @Context
  public Log                  log;

  @UserFunction(name = "slizaa.currentTimestamp")
  @Description("slizaa.currentTimestamp() - returns System.currentTimeMillis()")
  public long currentTimestamp() {
    return System.currentTimeMillis();
  }

  /**
   * <p>
   * </p>
   *
   * @param module
   * @return
   */
  @Procedure(name = "slizaa.createModule", mode = Mode.WRITE)
  public Stream<Output> createModule(@Name("module") Map<String, String> module) {

    //
    List<Output> result = new LinkedList<>();

    //
    result.add(new Output(CreatorHelper.createModule(db, module)));

    //
    return result.stream();
  }

  @Procedure(name = "slizaa.createNewModules", mode = Mode.WRITE)
  public Stream<Output> createNewModules(@Name("moduleList") List<Map<String, String>> modules) {

    //
    List<Output> result = new LinkedList<>();

    //
    for (Map<String, String> module : modules) {
      result.add(new Output(CreatorHelper.createModule(db, module)));
    }

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