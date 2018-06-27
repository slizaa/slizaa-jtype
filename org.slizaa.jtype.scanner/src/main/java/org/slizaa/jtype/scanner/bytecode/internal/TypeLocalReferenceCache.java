/*******************************************************************************
 * Copyright (c) 2011-2015 Slizaa project team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Slizaa project team - initial API and implementation
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
    _primitiveDatatypeNodes = checkNotNull(primitiveDatatypeNodes);

    //
    _typeReferenceNodeCache = CacheBuilder.newBuilder().build(new CacheLoader<String, INode>() {
      public INode load(String referencedTypeName) {
        return JTypeNodeHelper.createTypeReferenceNode(referencedTypeName);
      }
    });

    //
    _fieldReferenceNodeCache = CacheBuilder.newBuilder().build(new CacheLoader<FieldReferenceDescriptor, INode>() {
      public INode load(FieldReferenceDescriptor referencedField) {
        return JTypeNodeHelper.createFieldReferenceNode(referencedField);
      }
    });

    //
    _methodReferenceNodeCache = CacheBuilder.newBuilder().build(new CacheLoader<MethodReferenceDescriptor, INode>() {
      public INode load(MethodReferenceDescriptor referencedMethod) {
        return JTypeNodeHelper.createMethodReferenceNode(referencedMethod);
      }
    });

    //
    _dependsOnRelationshipTargets = new ArrayList<>();
  }

  /**
   * <p>
   * </p>
   *
   * @return
   */
  public Set<String> getAllReferencedTypes() {
    return _typeReferenceNodeCache.asMap().keySet();
  }

  /**
   * <p>
   * </p>
   *
   * @return
   */
  public INode getTypeBean() {
    return _typeBean;
  }

  /**
   * <p>
   * </p>
   *
   * @param typeBean
   */
  public void setTypeBean(INode typeBean) {
    _typeBean = typeBean;
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
    INode fieldTypeBean = _typeReferenceNodeCache.getUnchecked(fieldDescriptor.getFieldType().replace('/', '.'));
    if (!fieldDescriptor.isPrimitive()) {
      addDependsOnRelationship(fieldTypeBean);
    }

    // step 2: resolve the type that contains the referenced field (and add a depends-on-relationship)
    INode fieldOwnerBean = _typeReferenceNodeCache.getUnchecked(fieldDescriptor.getOwnerTypeName().replace('/', '.'));
    addDependsOnRelationship(fieldOwnerBean);

    // step 3: add the field access
    return startNode.addRelationship(_fieldReferenceNodeCache.getUnchecked(fieldDescriptor), relationshipType);
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
    INode fieldOwnerBean = _typeReferenceNodeCache
        .getUnchecked(methodReferenceDescriptor.getOwnerTypeName().replace('/', '.'));
    addDependsOnRelationship(fieldOwnerBean);

    // field access
    return startNode.addRelationship(_methodReferenceNodeCache.getUnchecked(methodReferenceDescriptor),
        relationshipType);
  }

  /**
   * <p>
   * </p>
   * 
   * @param referencedTypeName
   * @param relationshipType
   */
  public IRelationship addTypeReference(final INode startNode, String referencedTypeName,
      final RelationshipType relationshipType) {

    //
    if (referencedTypeName == null) {
      return null;
    }

    // TODO
    if (referencedTypeName.equals("boolean")) {
      throw new RuntimeException();
    }
    // TODO
    if (referencedTypeName.equals("int")) {
      throw new RuntimeException();
    }

    // TODO
    if (referencedTypeName.endsWith("[]")) {
      throw new RuntimeException();
    }

    referencedTypeName = referencedTypeName.replace('/', '.');

    //
    INode targetBean = _typeReferenceNodeCache.getUnchecked(referencedTypeName);
    addDependsOnRelationship(targetBean);
    return startNode.addRelationship(targetBean, relationshipType);
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
  public IRelationship addTypeReference(final INode startNode, final Type referencedType,
      final RelationshipType relationshipType) {

    //
    if (referencedType == null) {
      throw new RuntimeException();
      // return null;
    }

    //
    if (Utils.isVoidOrPrimitive(referencedType)) {
      throw new RuntimeException();
      // return null;
    }

    String referencedTypeName = Utils.getFullyQualifiedTypeName(referencedType);

    //
    if (referencedTypeName == null) {
      throw new RuntimeException(referencedType.toString());
      // return null;
    }

    //
    return addTypeReference(startNode, referencedTypeName, relationshipType);
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
    if (!targetBean.getFullyQualifiedName().equals(_typeBean.getFullyQualifiedName())
        && !_dependsOnRelationshipTargets.contains(targetBean)) {

      _dependsOnRelationshipTargets.add(targetBean);
      _typeBean.addRelationship(targetBean, JTypeModelRelationshipType.DEPENDS_ON);
    }
  }
}