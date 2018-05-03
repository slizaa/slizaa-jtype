package org.slizaa.scanner.jtype.graphdbextensions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.slizaa.scanner.core.spi.parser.model.resource.CoreModelElementType;
import org.slizaa.scanner.core.spi.parser.model.resource.CoreModelRelationshipType;
import org.slizaa.scanner.core.spi.parser.model.resource.IGroupNode;

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
  public static Node createModule(GraphDatabaseService db, String fqn, String version) {
    
    // check the module node
    // TODO
    checkNotNull(db).execute("Match (m:Module {fqn:'$fqn', version:'$version'})");
    Node group = checkNotNull(db).findNode(GROUP_LABEL, IGroupNode.FQN, checkNotNull(fqn).toString());
    if (group != null) {
      return group;
    }

    // TODO: check properties

    // create the new node
    Node node = checkNotNull(db).createNode();

    //
    node.addLabel(Label.label(CoreModelElementType.Module.name()));

    // set the properties
    for (String propertyKey : checkNotNull(module).keySet()) {
      node.setProperty(propertyKey, module.get(propertyKey));
    }

    // return the newly created node
    return node;
  }

  // public static Node createGroup(GraphDatabaseService db, FullyQualifiedName fullyQualifiedName) {
  //
  // // check the group node
  // Node group = checkNotNull(db).findNode(GROUP_LABEL, IGroupNode.FQN, checkNotNull(fullyQualifiedName).toString());
  // if (group != null) {
  // return group;
  // }
  //
  // // create the parent group
  // String groupName = fqn;
  // Node parentGroup = null;
  // int index = fqn.lastIndexOf('/');
  // if (index != -1) {
  // String parentFqn = fqn.substring(0, index);
  // groupName = fqn.substring(index + 1);
  // parentGroup = createGroup(db, parentFqn);
  // }
  //
  // // create the new node
  // Node node = checkNotNull(db).createNode();
  //
  // // set the labels
  // node.addLabel(Label.label(CoreModelElementType.Group.name()));
  //
  // // set the properties
  // node.setProperty(IGroupNode.FQN, fqn);
  // node.setProperty(IGroupNode.NAME, groupName);
  //
  // // TODO: RELILNK PARENT!
  // if (parentGroup != null) {
  // parentGroup.createRelationshipTo(node, CONTAINS_RELATIONSHIP);
  // }
  //
  // // return the newly created node
  // return node;
  // }

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
