package org.slizaa.jtype.scanner.apoc;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.slizaa.scanner.core.spi.parser.model.resource.CoreModelElementType;
import org.slizaa.scanner.core.spi.parser.model.resource.CoreModelRelationshipType;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class MissingTypesCreator {

  /** - */
  private final Label                LABEL_MISSING_TYPE = Label.label("MissingType");

  /** - */
  private final Label                LABEL_PACKAGE      = Label.label("Package");

  private final Label                LABEL_DIRECTORY    = Label.label("Directory");

  /** - */
  private final RelationshipType     REL_CONTAINS       = RelationshipType
      .withName(CoreModelRelationshipType.CONTAINS.name());

  /** - */
  private GraphDatabaseService       _graphDatabaseService;

  /** - */
  private LoadingCache<String, Node> _virtualPackagesCache;

  /** - */
  private LoadingCache<String, Node> _virtualTypesCache;

  /** - */
  private Node                       _missingTypeModuleNode;

  /**
   * <p>
   * Creates a new instance of type {@link MissingTypesCreator}.
   * </p>
   *
   * @param graphDatabaseService
   */
  public MissingTypesCreator(GraphDatabaseService graphDatabaseService) {

    //
    this._graphDatabaseService = checkNotNull(graphDatabaseService);

    //
    this._missingTypeModuleNode = this._graphDatabaseService
        .createNode(Label.label(CoreModelElementType.Module.name()));
    this._missingTypeModuleNode.setProperty("fqn", "<<Missing Types>>");

    //
    this._virtualPackagesCache = CacheBuilder.newBuilder().build(new CacheLoader<String, Node>() {
      @Override
      public Node load(String packageName) {
        return createVirtualPackage(packageName);
      }
    });

    //
    this._virtualTypesCache = CacheBuilder.newBuilder().build(new CacheLoader<String, Node>() {
      @Override
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
    return this._virtualTypesCache.getUnchecked(typeName);
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
      parentNode = MissingTypesCreator.this._virtualPackagesCache.getUnchecked(packageName.substring(0, index));
    }

    //
    Map<String, String> properties = new HashMap<>();
    properties.put("fqn", packageName);

    Node packageNode = createNode(properties, this.LABEL_PACKAGE, this.LABEL_DIRECTORY);
    if (parentNode != null) {
      parentNode.createRelationshipTo(packageNode, this.REL_CONTAINS);
    }
    this._missingTypeModuleNode.createRelationshipTo(packageNode, this.REL_CONTAINS);

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
      parentNode = MissingTypesCreator.this._virtualPackagesCache.getUnchecked(typeName.substring(0, index));
    }

    //
    Map<String, String> properties = new HashMap<>();
    properties.put("fqn", typeName);

    Node typeNode = createNode(properties, this.LABEL_MISSING_TYPE);
    if (parentNode != null) {
      parentNode.createRelationshipTo(typeNode, this.REL_CONTAINS);
    }
    this._missingTypeModuleNode.createRelationshipTo(typeNode, this.REL_CONTAINS);

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
    Node node = this._graphDatabaseService.createNode(labels);

    //
    for (Entry<String, String> entry : properties.entrySet()) {
      node.setProperty(entry.getKey(), entry.getValue());
    }

    //
    return node;
  }
}
