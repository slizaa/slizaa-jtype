package org.slizaa.jtype.scanner.apoc;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class VirtualNodesCreator {

  /** - */
  private GraphDatabaseService       _graphDatabaseService;

  /** - */
  private LoadingCache<String, Node> _virtualPackagesCache;

  /** - */
  private LoadingCache<String, Node> _virtualTypesCache;

  /**
   * <p>
   * Creates a new instance of type {@link VirtualNodesCreator}.
   * </p>
   *
   * @param graphDatabaseService
   */
  public VirtualNodesCreator(GraphDatabaseService graphDatabaseService) {

    //
    _graphDatabaseService = checkNotNull(graphDatabaseService);

    //
    _virtualPackagesCache = CacheBuilder.newBuilder().build(new CacheLoader<String, Node>() {
      public Node load(String packageName) {
        return createVirtualPackage(packageName);
      }
    });

    //
    _virtualTypesCache = CacheBuilder.newBuilder().build(new CacheLoader<String, Node>() {
      public Node load(String typeName) {
        return createVirtualType(typeName);
      }
    });
  }

  /**
   * <p>
   * </p>
   *
   * @param packageName
   * @return
   */
  public Node getOrCreateVirtualType(String typeName) {
    return _virtualTypesCache.getUnchecked(typeName);
  }

  /**
   * <p>
   * </p>
   *
   * @param packageName
   * @return
   */
  private Node createVirtualPackage(String packageName) {

    //
    Node parentNode = null;

    //
    int index = packageName.lastIndexOf('.');
    if (index != -1) {
      parentNode = VirtualNodesCreator.this._virtualPackagesCache.getUnchecked(packageName.substring(0, index));
    }

    //
    Node packageNode = createNode(Collections.singletonMap("fqn", packageName), Label.label("VPackage"));
    if (parentNode != null) {
      parentNode.createRelationshipTo(packageNode, RelationshipType.withName("CONTAINS"));
    }

    //
    return packageNode;
  }

  /**
   * <p>
   * </p>
   *
   * @param typeName
   * @return
   */
  private Node createVirtualType(String typeName) {

    //
    Node parentNode = null;

    //
    int index = typeName.lastIndexOf('.');
    if (index != -1) {
      parentNode = VirtualNodesCreator.this._virtualPackagesCache.getUnchecked(typeName.substring(0, index));
    }

    //
    Node typeNode = createNode(Collections.singletonMap("fqn", typeName), Label.label("MissingType"));
    if (typeNode != null) {
      parentNode.createRelationshipTo(typeNode, RelationshipType.withName("CONTAINS"));
    }

    //
    return typeNode;
  }

  /**
   * <p>
   * </p>
   *
   * @return
   */
  private Node createNode(Map<String, String> properties, Label... labels) {

    //
    Node node = _graphDatabaseService.createNode(labels);

    //
    System.out.println("Create Type-NODE: " + properties + " : " + Arrays.asList(labels));

    //
    for (Entry<String, String> entry : properties.entrySet()) {
      node.setProperty(entry.getKey(), entry.getValue());
    }

    //
    return node;
  }
}
