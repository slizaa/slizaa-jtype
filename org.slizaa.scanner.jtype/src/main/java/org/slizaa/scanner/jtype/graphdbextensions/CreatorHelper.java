package org.slizaa.scanner.jtype.graphdbextensions;

import static com.google.common.base.Preconditions.checkNotNull;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Result;
import org.slizaa.scanner.core.spi.parser.model.resource.CoreModelElementType;
import org.slizaa.scanner.core.spi.parser.model.resource.CoreModelRelationshipType;
import org.slizaa.scanner.core.spi.parser.model.resource.IGroupNode;
import org.slizaa.scanner.core.spi.parser.model.resource.IModuleNode;

import com.google.common.collect.ImmutableMap;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class CreatorHelper {

  /** - */
  private static Label            GROUP_LABEL           = Label.label(CoreModelElementType.Group.name());

  /** - */
  private static Label            MODULE_LABEL          = Label.label(CoreModelElementType.Module.name());

  /** - */
  private static RelationshipType CONTAINS_RELATIONSHIP = RelationshipType
      .withName(CoreModelRelationshipType.CONTAINS.name());

  /**
   * <p>
   * </p>
   *
   * @param db
   *          the database
   * @param module
   *          the module description
   */
  public static Node createModule(GraphDatabaseService db, FullyQualifiedName fqn, String version) {

    // check the module node
    Result result = checkNotNull(db).execute("Match (m:Module {fqn:'$fqn', version:'$version'}) return m",
        ImmutableMap.of("fqn", fqn.toString(), "version", version));

    if (result.hasNext()) {
      return (Node) result.next().get("m");
    }

    // TODO: check properties

    Node parentGroup = fqn.hasParent() ? createGroup(db, fqn.getParent()) : null;

    // create the new node
    Node node = checkNotNull(db).createNode();

    //
    node.addLabel(MODULE_LABEL);

    // set the properties
    node.setProperty(IModuleNode.FQN, fqn.toString());
    node.setProperty(IModuleNode.PROPERTY_MODULE_NAME, fqn.getSimpleName());
    node.setProperty(IModuleNode.PROPERTY_MODULE_VERSION, version);

    // relink parent
    if (parentGroup != null) {
      parentGroup.createRelationshipTo(node, CONTAINS_RELATIONSHIP);
    }

    // return the newly created node
    return node;
  }

  /**
   * <p>
   * </p>
   *
   * @param db
   * @param fqn
   * @return
   */
  public static Node createGroup(GraphDatabaseService db, FullyQualifiedName fqn) {

    // check the group node
    Node group = checkNotNull(db).findNode(GROUP_LABEL, IGroupNode.FQN, checkNotNull(fqn).toString());
    if (group != null) {
      return group;
    }

    // create the parent group
    Node parentGroup = null;
    if (fqn.hasParent()) {
      parentGroup = createGroup(db, fqn.getParent());
    }

    // create the new node
    Node node = checkNotNull(db).createNode();

    // set the labels
    node.addLabel(Label.label(CoreModelElementType.Group.name()));

    // set the properties
    node.setProperty(IGroupNode.FQN, fqn.getFullyQualifiedName());
    node.setProperty(IGroupNode.NAME, fqn.getSimpleName());

    // relink parent
    if (parentGroup != null) {
      parentGroup.createRelationshipTo(node, CONTAINS_RELATIONSHIP);
    }

    // return the newly created node
    return node;
  }

}
