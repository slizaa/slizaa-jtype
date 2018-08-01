/*******************************************************************************
 * Copyright (c) 2011-2015 Slizaa project team. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Slizaa project team - initial API and implementation
 ******************************************************************************/
package org.slizaa.jtype.scanner.bytecode.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.slizaa.jtype.scanner.model.JTypeModelRelationshipType;
import org.slizaa.scanner.core.spi.parser.model.INode;
import org.slizaa.scanner.core.spi.parser.model.IRelationship;
import org.slizaa.scanner.core.spi.parser.model.RelationshipType;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class JTypeMethodVisitor extends MethodVisitor {

  /** - */
  private TypeLocalReferenceCache _typeLocalReferenceCache;

  /** - */
  private INode                   _typeBean;

  /** - */
  private INode                   _methodNodeBean;

  /**
   * <p>
   * </p>
   *
   * @param methodBean
   *
   * @param recorder
   * @param type
   */
  public JTypeMethodVisitor(INode typeBean, INode methodNodeBean, TypeLocalReferenceCache typeReferenceHolder) {
    super(Opcodes.ASM6);

    //
    this._typeBean = checkNotNull(typeBean);
    this._methodNodeBean = checkNotNull(methodNodeBean);
    this._typeLocalReferenceCache = checkNotNull(typeReferenceHolder);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {

    // uncompressed frame
    if (type != Opcodes.F_NEW) {
      throw new IllegalStateException("ClassReader.accept() should be called with EXPAND_FRAMES flag");
    }

    //
    for (int i = 0; i < nStack; i++) {
      if (stack[i] instanceof String) {
        addTypeReference(this._methodNodeBean, Type.getObjectType((String) stack[i]),
            JTypeModelRelationshipType.REFERENCES);
      }
    }

    //
    super.visitFrame(type, nLocal, local, nStack, stack);
  }

  /**
   * <p>
   * </p>
   *
   * @inheritDoc
   */
  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {

    // class annotation
    // TODO: filter visible?
    if (visible) {
      addTypeReference(this._methodNodeBean, Type.getType(desc), JTypeModelRelationshipType.ANNOTATED_BY);
    }

    //
    return null;
  }

  /**
   * @inheritDoc
   */
  @Override
  public void visitFieldInsn(int opcode, String owner, String name, String desc) {

    //
    String opcode_str = null;

    // opcode
    switch (opcode) {
    case Opcodes.GETSTATIC: {
      opcode_str = "GETSTATIC";
      break;
    }
    case Opcodes.PUTSTATIC: {
      opcode_str = "PUTSTATIC";
      break;
    }
    case Opcodes.GETFIELD: {
      opcode_str = "GETFIELD";
      break;
    }
    case Opcodes.PUTFIELD: {
      opcode_str = "PUTFIELD";
      break;
    }
    default:
      // TODO
      throw new RuntimeException();
    }

    String ownerTypeName = Utils.getFullyQualifiedTypeName(Type.getObjectType(owner));
    String fieldType = Utils.getFullyQualifiedTypeName(Type.getType(desc));

    FieldReferenceDescriptor fieldDescriptor = new FieldReferenceDescriptor(ownerTypeName, name, fieldType,
        opcode == Opcodes.GETSTATIC || opcode == Opcodes.PUTSTATIC);

    //
    JTypeModelRelationshipType relationshipType = opcode == Opcodes.GETSTATIC || opcode == Opcodes.GETFIELD
        ? JTypeModelRelationshipType.READS
        : JTypeModelRelationshipType.WRITES;

    //
    this._typeLocalReferenceCache.addFieldReference(this._methodNodeBean, fieldDescriptor, relationshipType);
  }

  /**
   * @inheritDoc
   */
  @Override
  public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
    Type type = Type.getType(desc);
    if (!this._typeBean.getFullyQualifiedName().equals(type.getClassName())) {
      addTypeReference(this._methodNodeBean, type, JTypeModelRelationshipType.DEFINES_LOCAL_VARIABLE);
    }
  }

  @Override
  public void visitInvokeDynamicInsn(String name, String rawSignature, Handle bsm, Object... bsmArgs) {

    // TODO!!!!!
    this._typeLocalReferenceCache.addTypeReference(this._methodNodeBean, Type.getObjectType(bsm.getOwner()),
        JTypeModelRelationshipType.REFERENCES);

    // return type
    Type returnType = org.objectweb.asm.Type.getReturnType(bsm.getDesc());
    if (!Utils.isVoidOrPrimitive(returnType)) {
      addTypeReference(this._methodNodeBean, returnType, JTypeModelRelationshipType.REFERENCES);
    }

    // arg types type
    org.objectweb.asm.Type[] types = org.objectweb.asm.Type.getArgumentTypes(bsm.getDesc());
    for (int i = 0; i < types.length; i++) {
      if (!Utils.isVoidOrPrimitive(types[i])) {
        addTypeReference(this._methodNodeBean, types[i], JTypeModelRelationshipType.REFERENCES);
      }
    }
  }

  /**
   * @inheritDoc
   */
  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String rawSignature, boolean itf) {

    //
    IRelationship methodReferenceRelationship = this._typeLocalReferenceCache.addMethodReference(this._methodNodeBean,
        new MethodReferenceDescriptor(owner, name, rawSignature, itf), JTypeModelRelationshipType.INVOKES);

    //
    INode methodReference = methodReferenceRelationship.getTargetBean();

    // owner
    Type ownerTyper = Type.getObjectType(owner);
    addTypeReference(methodReference, ownerTyper, JTypeModelRelationshipType.IS_DEFINED_BY);
    addTypeReference(this._methodNodeBean, ownerTyper, JTypeModelRelationshipType.INVOKES_METHOD_FROM);

    // return type
    Type returnType = Type.getReturnType(rawSignature);
    addTypeReference(methodReference, returnType, JTypeModelRelationshipType.RETURNS);
    addTypeReference(this._methodNodeBean, returnType, JTypeModelRelationshipType.INVOKED_METHOD_RETURNS);

    // arg types type
    for (Type argumentType : Type.getArgumentTypes(rawSignature)) {
      addTypeReference(methodReference, argumentType, JTypeModelRelationshipType.HAS_PARAMETER);
      addTypeReference(this._methodNodeBean, argumentType, JTypeModelRelationshipType.INVOKED_METHOD_HAS_PARAMETER);
    }
  }

  /**
   * @inheritDoc
   */
  @Override
  public void visitMultiANewArrayInsn(String desc, int dims) {

    //
    addTypeReference(this._methodNodeBean, Type.getType(desc), JTypeModelRelationshipType.REFERENCES);
  }

  /**
   * @inheritDoc
   */
  @Override
  public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {

    //
    if (visible) {
      addTypeReference(this._methodNodeBean, Type.getType(desc), JTypeModelRelationshipType.REFERENCES);
    }

    //
    return null;
  }

  /**
   * @inheritDoc
   */
  @Override
  public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {

    if (type != null) {

      //
      addTypeReference(this._methodNodeBean, Type.getObjectType(type), JTypeModelRelationshipType.REFERENCES);
    }
  }

  /**
   * @inheritDoc
   */
  @Override
  public void visitTypeInsn(int opcode, String type) {

    if (opcode != Opcodes.CHECKCAST && opcode != Opcodes.NEW && opcode != Opcodes.INSTANCEOF
        && opcode != Opcodes.ANEWARRAY) {

    }

    //
    addTypeReference(this._methodNodeBean, Type.getObjectType(type), JTypeModelRelationshipType.REFERENCES);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visitLdcInsn(Object cst) {

    //
    if (cst instanceof Type) {
      addTypeReference(this._methodNodeBean, (Type) cst, JTypeModelRelationshipType.USES_TYPE_CONSTANT);
    }
  }

  /**
   * <p>
   * </p>
   *
   * @param startNode
   * @param targetTypeName
   * @param relationshipType
   */
  private void addTypeReference(INode startNode, Type targetType, RelationshipType relationshipType) {

    //
    if (targetType != null && !Utils.isVoidOrPrimitive(targetType)) {

      // add the type reference
      this._typeLocalReferenceCache.addTypeReference(startNode, Utils.resolveArrayType(targetType), relationshipType);
    }
  }
}
