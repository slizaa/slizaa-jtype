/*******************************************************************************
 * Copyright (c) 2011-2015 Slizaa project team. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Slizaa project team - initial API and implementation
 ******************************************************************************/
package org.slizaa.jtype.scanner.bytecode.internal;

import org.objectweb.asm.Type;
import org.slizaa.jtype.scanner.model.IFieldReferenceNode;
import org.slizaa.jtype.scanner.model.IMethodReferenceNode;
import org.slizaa.jtype.scanner.model.ITypeReferenceNode;
import org.slizaa.jtype.scanner.model.JTypeLabel;
import org.slizaa.scanner.core.spi.parser.model.INode;
import org.slizaa.scanner.core.spi.parser.model.NodeFactory;

public class JTypeNodeHelper {

  /**
   * <p>
   * </p>
   *
   * @param batchInserter
   * @param type
   * @return
   */
  public static INode createTypeReferenceNode(final Type type) {

    //
    if (type == null) {
      return null;
    }

    //
    String fqn = Utils.getFullyQualifiedTypeName(type);

    //
    if (fqn != null) {
      return createTypeReferenceNode(fqn);
    }

    //
    return null;
  }

  /**
   * <p>
   * </p>
   *
   * @param batchInserter
   * @param fullyQualifiedName
   * @return
   */
  public static INode createTypeReferenceNode(String fullyQualifiedName) {

    //
    if (fullyQualifiedName.startsWith("[")) {
      Type arrayType = Type.getType(fullyQualifiedName);
      Type type = Utils.resolveArrayType(arrayType);
      fullyQualifiedName = type.getClassName();
    }

    //
    INode node = NodeFactory.createNode();
    node.addLabel(JTypeLabel.TypeReference);
    node.putProperty(ITypeReferenceNode.FQN, fullyQualifiedName.replace('/', '.'));
    return node;
  }

  /**
   * <p>
   * </p>
   *
   * @param fieldDescriptor
   * @return
   */
  public static INode createFieldReferenceNode(final FieldReferenceDescriptor fieldReferenceDescriptor) {

    //
    INode node = NodeFactory.createNode();
    node.addLabel(JTypeLabel.FieldReference);
    node.putProperty(IFieldReferenceNode.OWNER_TYPE_FQN, fieldReferenceDescriptor.getOwnerTypeName().replace('/', '.'));
    node.putProperty(IFieldReferenceNode.NAME, fieldReferenceDescriptor.getFieldName());
    node.putProperty(IFieldReferenceNode.TYPE, fieldReferenceDescriptor.getFieldType());
    node.putProperty(IFieldReferenceNode.STATIC, fieldReferenceDescriptor.isStatic());
    node.putProperty(IMethodReferenceNode.FQN, fieldReferenceDescriptor.getFieldType() + " "
        + fieldReferenceDescriptor.getOwnerTypeName() + "." + fieldReferenceDescriptor.getFieldName());

    //
    return node;
  }

  // TODO: MOVE
  public static INode createMethodReferenceNode(MethodReferenceDescriptor referencedMethod) {

    String ownerTypeName = referencedMethod.getOwnerTypeName().replace('/', '.');

    //
    INode node = NodeFactory.createNode();
    node.addLabel(JTypeLabel.MethodReference);
    node.putProperty(IMethodReferenceNode.OWNER_TYPE_FQN, referencedMethod.getOwnerTypeName().replace('/', '.'));
    node.putProperty(IMethodReferenceNode.NAME, referencedMethod.getMethodName());
    node.putProperty(IMethodReferenceNode.SIGNATURE, referencedMethod.getMethodSignature());
    node.putProperty(IMethodReferenceNode.FQN, Utils.getMethodSignature(
        ownerTypeName + "." + referencedMethod.getMethodName(), referencedMethod.getMethodSignature()));
    node.putProperty(IMethodReferenceNode.IS_INTERFACE, referencedMethod.isInterface());

    //
    return node;
  }

  /**
   * Checks whether the value contains the flag.
   *
   * @param value
   *          the value
   * @param flag
   *          the flag
   * @return <code>true</code> if (value & flag) == flag, otherwise <code>false</code>.
   */
  public static boolean hasFlag(int value, int flag) {
    return (value & flag) == flag;
  }
}
