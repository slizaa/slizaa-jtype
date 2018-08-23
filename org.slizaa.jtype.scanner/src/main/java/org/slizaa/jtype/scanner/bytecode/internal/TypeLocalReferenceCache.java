/*******************************************************************************
 * Copyright (c) 2011-2015 Slizaa project team. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Slizaa project team - initial API and implementation
 ******************************************************************************/
package org.slizaa.jtype.scanner.bytecode.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Type;
import org.slizaa.jtype.scanner.bytecode.IPrimitiveDatatypeNodeProvider;
import org.slizaa.jtype.scanner.model.JTypeModelRelationshipType;
import org.slizaa.scanner.core.spi.parser.model.INode;
import org.slizaa.scanner.core.spi.parser.model.IRelationship;
import org.slizaa.scanner.core.spi.parser.model.RelationshipType;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * <p>
 * A cache that stores type references.
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class TypeLocalReferenceCache {

  /** - */
  private IPrimitiveDatatypeNodeProvider                 _primitiveDatatypeNodes;

  /** - */
  private LoadingCache<String, INode>                    _typeReferenceNodeCache;

  /** - */
  private LoadingCache<FieldReferenceDescriptor, INode>  _fieldReferenceNodeCache;

  /** - */
  private LoadingCache<MethodReferenceDescriptor, INode> _methodReferenceNodeCache;

  /** - */
  private List<INode>                                    _dependsOnRelationshipTargets;

  /** - */
  private INode                                          _typeBean;

  /**
   * <p>
   * Creates a new instance of type {@link TypeLocalReferenceCache}.
   * </p>
   */
  public TypeLocalReferenceCache(IPrimitiveDatatypeNodeProvider primitiveDatatypeNodes) {
    this._primitiveDatatypeNodes = checkNotNull(primitiveDatatypeNodes);

    //
    this._typeReferenceNodeCache = CacheBuilder.newBuilder().build(new CacheLoader<String, INode>() {
      @Override
      public INode load(String referencedTypeName) {
        return JTypeNodeHelper.createTypeReferenceNode(referencedTypeName);
      }
    });

    //
    this._fieldReferenceNodeCache = CacheBuilder.newBuilder().build(new CacheLoader<FieldReferenceDescriptor, INode>() {
      @Override
      public INode load(FieldReferenceDescriptor referencedField) {
        return JTypeNodeHelper.createFieldReferenceNode(referencedField);
      }
    });

    //
    this._methodReferenceNodeCache = CacheBuilder.newBuilder()
        .build(new CacheLoader<MethodReferenceDescriptor, INode>() {
          @Override
          public INode load(MethodReferenceDescriptor referencedMethod) {
            return JTypeNodeHelper.createMethodReferenceNode(referencedMethod);
          }
        });

    //
    this._dependsOnRelationshipTargets = new ArrayList<>();
  }

  /**
   * <p>
   * </p>
   *
   * @return
   */
  public Set<String> getAllReferencedTypes() {
    return this._typeReferenceNodeCache.asMap().keySet();
  }

  /**
   * <p>
   * </p>
   *
   * @return
   */
  public INode getTypeBean() {
    return this._typeBean;
  }

  /**
   * <p>
   * </p>
   *
   * @param typeBean
   */
  public void setTypeBean(INode typeBean) {
    this._typeBean = typeBean;
  }

  /**
   * <p>
   * </p>
   *
   * @param startNode
   * @param fieldDescriptor
   * @param relationshipType
   * @return
   */
  public IRelationship addFieldReference(final INode startNode, final FieldReferenceDescriptor fieldDescriptor,
      final RelationshipType relationshipType) {

    // step 1: resolve the type of the referenced field (and add a depends-on-relationship)
    String fieldTypeFqn = fieldDescriptor.getFieldType().replace('/', '.');
    if (!fieldDescriptor.isPrimitive()) {
      INode fieldTypeBean = this._typeReferenceNodeCache.getUnchecked(fieldTypeFqn);
      addDependsOnRelationship(fieldTypeBean);

      addTypeReference(startNode, fieldTypeFqn,
          relationshipType == JTypeModelRelationshipType.READS ? JTypeModelRelationshipType.READS_FIELD_OF_TYPE
              : JTypeModelRelationshipType.WRITES_FIELD_OF_TYPE);
    }

    // step 2: resolve the type that contains the referenced field (and add a depends-on-relationship)
    INode fieldOwnerBean = this._typeReferenceNodeCache
        .getUnchecked(fieldDescriptor.getOwnerTypeName().replace('/', '.'));
    addDependsOnRelationship(fieldOwnerBean);

    // step 3: add the field access
    return startNode.addRelationship(this._fieldReferenceNodeCache.getUnchecked(fieldDescriptor), relationshipType);
  }

  /**
   * @param startNode
   * @param fieldDescriptor
   * @param relationshipType
   * @return
   */
  public IRelationship addMethodReference(final INode startNode,
      final MethodReferenceDescriptor methodReferenceDescriptor, final RelationshipType relationshipType) {

    //
    String ownerTypeName = methodReferenceDescriptor.getOwnerTypeName();
    // System.out.println("HAAHHEHE : " + Type.getObjectType(ownerTypeName));

    //
    TODO: try {
      INode fieldOwnerBean = this._typeReferenceNodeCache
          .getUnchecked(methodReferenceDescriptor.getOwnerTypeName().replace('/', '.'));
      addDependsOnRelationship(fieldOwnerBean);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.out.println(ownerTypeName);
    }

    // field access
    return startNode.addRelationship(this._methodReferenceNodeCache.getUnchecked(methodReferenceDescriptor),
        relationshipType);
  }

  /**
   * <p>
   * </p>
   *
   * @param referencedTypeName
   * @param relationshipType
   */
  public void addTypeReference(final INode startNode, String referencedTypeName,
      final RelationshipType relationshipType) {

    //
    if (referencedTypeName == null) {
      return;
    }

    //
    JTypeNodeHelper.assertNoPrimitiveType(referencedTypeName);

    //
    if (referencedTypeName.endsWith("[]")) {
      throw new RuntimeException(referencedTypeName);
    }

    //
    referencedTypeName = referencedTypeName.replace('/', '.');

    //
    INode targetBean = this._typeReferenceNodeCache.getUnchecked(referencedTypeName);
    addDependsOnRelationship(targetBean);
    startNode.addRelationship(targetBean, relationshipType);

    //
    String outerClassName = referencedTypeName;
    while (outerClassName.contains("$")) {
      outerClassName = outerClassName.substring(0, outerClassName.lastIndexOf("$"));
      addTypeReference(startNode, outerClassName, JTypeModelRelationshipType.INNER_CLASS_REFERENCES);
    }
  }

  public IRelationship addInnerClass(final INode outerClass, INode innerClass,
      final RelationshipType relationshipType) {

    checkNotNull(outerClass);
    checkNotNull(innerClass);
    checkNotNull(relationshipType);

    //
    addDependsOnRelationship(innerClass);
    return outerClass.addRelationship(innerClass, relationshipType);
  }

  /**
   * <p>
   * </p>
   *
   * @param referencedType
   * @param relationshipType
   */
  public void addTypeReference(final INode startNode, final Type referencedType,
      final RelationshipType relationshipType) {

    //
    if (referencedType == null) {
      throw new RuntimeException();
    }

    //
    if (Utils.isVoidOrPrimitive(referencedType)) {
      throw new RuntimeException();
    }

    //
    String referencedTypeName = Utils.getFullyQualifiedTypeName(referencedType);
    if (referencedTypeName == null) {
      throw new RuntimeException(referencedType.toString());
    }
    addTypeReference(startNode, referencedTypeName, relationshipType);
  }

  /**
   * <p>
   * </p>
   *
   * @param startNode
   * @param referencedTypes
   * @param relationshipType
   */
  public void addTypeReference(final INode startNode, final Type[] referencedTypes,
      final RelationshipType relationshipType) {

    //
    if (referencedTypes == null) {
      return;
    }

    //
    for (Type referencedType : referencedTypes) {
      addTypeReference(startNode, referencedType, relationshipType);
    }
  }

  /**
   * <p>
   * </p>
   *
   * @param targetBean
   */
  private void addDependsOnRelationship(INode targetBean) {
    if (!targetBean.getFullyQualifiedName().equals(this._typeBean.getFullyQualifiedName())
        && !this._dependsOnRelationshipTargets.contains(targetBean)) {

      this._dependsOnRelationshipTargets.add(targetBean);
      this._typeBean.addRelationship(targetBean, JTypeModelRelationshipType.DEPENDS_ON);
    }
  }
}
