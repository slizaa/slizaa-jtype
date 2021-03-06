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
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.signature.SignatureReader;
import org.slizaa.jtype.scanner.bytecode.JTypeByteCodeParserFactory;
import org.slizaa.jtype.scanner.bytecode.internal.signature.JTypeClassSignatureVisitor;
import org.slizaa.jtype.scanner.bytecode.internal.signature.JTypeMethodSignatureVisitor;
import org.slizaa.jtype.scanner.bytecode.internal.signature.JTypeTypeSignatureVisitor;
import org.slizaa.jtype.scanner.model.IFieldNode;
import org.slizaa.jtype.scanner.model.IMethodNode;
import org.slizaa.jtype.scanner.model.ITypeNode;
import org.slizaa.jtype.scanner.model.IVisibility;
import org.slizaa.jtype.scanner.model.JTypeLabel;
import org.slizaa.jtype.scanner.model.JTypeModelRelationshipType;
import org.slizaa.jtype.scanner.model.JavaTypeUtils;
import org.slizaa.scanner.spi.parser.model.INode;
import org.slizaa.scanner.spi.parser.model.IRelationship;
import org.slizaa.scanner.spi.parser.model.NodeFactory;
import org.slizaa.scanner.spi.parser.model.resource.CoreModelRelationshipType;

/**
 */
public class JTypeClassVisitor extends ClassVisitor {

  /** - */
  private INode                      _typeBean;

  /** - */
  private TypeLocalReferenceCache    _classLocalReferenceCache;

  /** - */
  private JTypeByteCodeParserFactory _parserFactory;

  /**
   * <p>
   * Creates a new instance of type {@link JTypeClassVisitor}.
   * </p>
   *
   * @param batchInserter
   */
  public JTypeClassVisitor(JTypeByteCodeParserFactory parserFactory) {
    super(Opcodes.ASM6);

    //
    this._parserFactory = checkNotNull(parserFactory);

    //
    this._classLocalReferenceCache = new TypeLocalReferenceCache(this._parserFactory.getDatatypeNodeProviderMap());
  }

  /**
   * <p>
   * </p>
   *
   * @return
   */
  public TypeLocalReferenceCache getTypeLocalReferenceCache() {
    return this._classLocalReferenceCache;
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
   * {@inheritDoc}
   */
  @Override
  public void visit(final int version, final int access, final String name, final String signature,
      final String superName, final String[] interfaces) {

    //
    this._typeBean = NodeFactory.createNode();

    // add type bean to type local cache
    this._classLocalReferenceCache.setTypeBean(this._typeBean);

    // add the type of the type
    this._typeBean.addLabel(JTypeLabel.Type);
    this._typeBean.addLabel(getJTypeLabel(access));

    // class name
    this._typeBean.putProperty(ITypeNode.FQN, name.replace('/', '.'));
    this._typeBean.putProperty(ITypeNode.NAME, JavaTypeUtils.getSimpleName(name.replace('/', '.')));

    // class version
    this._typeBean.putProperty(ITypeNode.CLASS_VERSION, Integer.toString(version));

    // deprecated
    this._typeBean.putProperty(ITypeNode.DEPRECATED, (access & Opcodes.ACC_DEPRECATED) == Opcodes.ACC_DEPRECATED);

    // // access flags
    // _typeBean.putProperty(ITypeNode.ACCESS_FLAGS, Integer.toHexString(access).toUpperCase());

    //
    this._typeBean.putProperty(ITypeNode.ABSTRACT, (access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT);

    //
    this._typeBean.putProperty(ITypeNode.STATIC, (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC);

    //
    this._typeBean.putProperty(ITypeNode.FINAL, (access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL);

    //
    if ((access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC) {
      this._typeBean.putProperty(ITypeNode.VISIBILITY, IVisibility.PUBLIC);
    }
    //
    else if ((access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE) {
      this._typeBean.putProperty(ITypeNode.VISIBILITY, IVisibility.PRIVATE);
    }
    //
    else if ((access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED) {
      this._typeBean.putProperty(ITypeNode.VISIBILITY, IVisibility.PROTECTED);
    }
    //
    else {
      this._typeBean.putProperty(ITypeNode.VISIBILITY, IVisibility.DEFAULT);
    }

    // TODO!!
    if (signature != null) {

      // set signature
      this._typeBean.putProperty(ITypeNode.SIGNATURE, signature);

      JTypeClassSignatureVisitor sv = new JTypeClassSignatureVisitor(this._typeBean, this._classLocalReferenceCache);
      new SignatureReader(signature).accept(sv);
    }

    // add 'extends' references
    this._classLocalReferenceCache.addTypeReference(this._typeBean, superName, JTypeModelRelationshipType.EXTENDS);

    // add 'implements' references
    for (String ifaceName : interfaces) {

      switch (getJTypeLabel(access)) {
      case Class:
        this._classLocalReferenceCache.addTypeReference(this._typeBean, ifaceName,
            JTypeModelRelationshipType.IMPLEMENTS);
        break;
      case Interface:
        this._classLocalReferenceCache.addTypeReference(this._typeBean, ifaceName, JTypeModelRelationshipType.EXTENDS);
        break;
      case Annotation:
        this._classLocalReferenceCache.addTypeReference(this._typeBean, ifaceName, JTypeModelRelationshipType.EXTENDS);
        break;
      default:
        break;
      }
    }
  }

  /**
   * @inheritDoc
   */
  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {

    // class annotation
    this._classLocalReferenceCache.addTypeReference(this._typeBean, Type.getType(desc),
        JTypeModelRelationshipType.ANNOTATED_BY);

    //
    return new JTypeAnnotationVisitor();
  }

  /**
   * @inheritDoc
   */
  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

    // create and add new method bean
    INode methodBean = NodeFactory.createNode();
    this._typeBean.addRelationship(methodBean, CoreModelRelationshipType.CONTAINS);

    // add method name
    methodBean.putProperty(IMethodNode.NAME, name);
    String methodSignature = Utils.getMethodSignature(this._typeBean.getFullyQualifiedName() + "." + name, desc);
    methodBean.putProperty(IMethodNode.FQN, methodSignature);

    // set labels
    methodBean.addLabel(JTypeLabel.Method);
    if ("<init>".equals(name)) {
      methodBean.addLabel(JTypeLabel.Constructor);
    }

    // signature
    if (signature != null) {
      methodBean.putProperty("signature", signature);
      new SignatureReader(signature)
          .accept(new JTypeMethodSignatureVisitor(methodBean, this._classLocalReferenceCache));
    }

    //
    if ((access & Opcodes.ACC_NATIVE) == Opcodes.ACC_NATIVE) {
      methodBean.putProperty(IMethodNode.NATIVE, true);
    }

    if ((access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT) {
      methodBean.putProperty(IMethodNode.ABSTRACT, true);
    }
    if ((access & Opcodes.ACC_SYNTHETIC) == Opcodes.ACC_SYNTHETIC) {
      methodBean.putProperty(IMethodNode.SYNTHETIC, true);
    }
    if ((access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
      methodBean.putProperty(IMethodNode.STATIC, true);
    }
    if ((access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL) {
      methodBean.putProperty(IMethodNode.FINAL, true);
    }

    // Access modifiers: public, protected, and private
    if ((access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC) {
      methodBean.putProperty(IMethodNode.VISIBILITY, IVisibility.PUBLIC);
    } else if ((access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED) {
      methodBean.putProperty(IMethodNode.VISIBILITY, IVisibility.PROTECTED);
    } else if ((access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE) {
      methodBean.putProperty(IMethodNode.VISIBILITY, IVisibility.PRIVATE);
    } else {
      methodBean.putProperty(IMethodNode.VISIBILITY, IVisibility.DEFAULT);
    }

    // arguments
    Type[] types = Type.getArgumentTypes(desc);
    methodBean.putProperty(IMethodNode.PARAMETER_COUNT, types.length + 1);
    for (int i = 0; i < types.length; i++) {

      // TODO!!!
      addReference(methodBean, types[i], JTypeModelRelationshipType.HAS_PARAMETER);
      // relationship.putRelationshipProperty(IMethodNode.PARAMETER_INDEX, i);
    }

    // return type
    addReference(methodBean, Type.getReturnType(desc), JTypeModelRelationshipType.RETURNS);

    // exceptions
    if (exceptions != null) {
      for (String exception : exceptions) {
        addReference(methodBean, Type.getObjectType(exception), JTypeModelRelationshipType.THROWS);
      }
    }

    //
    return new JTypeMethodVisitor(this._typeBean, methodBean, this._classLocalReferenceCache);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FieldVisitor visitField(int access, String name, String desc, String signature, final Object value) {

    // create bean and add it to the type bean
    INode fieldBean = NodeFactory.createNode();
    fieldBean.addLabel(JTypeLabel.Field);
    this._typeBean.addRelationship(fieldBean, CoreModelRelationshipType.CONTAINS);

    //
    fieldBean.putProperty(IMethodNode.FQN,
        Utils.typeToString(Type.getType(desc)) + " " + this._typeBean.getFullyQualifiedName() + "." + name);

    // get the type
    addReference(fieldBean, Type.getType(desc), JTypeModelRelationshipType.IS_OF_TYPE);

    //
    fieldBean.putProperty(IFieldNode.NAME, name);

    // access flags
    fieldBean.putProperty(IFieldNode.ACCESS_FLAGS, Integer.toHexString(access).toUpperCase());

    // Access modifiers: public, protected, and private
    if ((access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC) {
      fieldBean.putProperty(IFieldNode.VISIBILITY, IVisibility.PUBLIC);
    } else if ((access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED) {
      fieldBean.putProperty(IFieldNode.VISIBILITY, IVisibility.PROTECTED);
    } else if ((access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE) {
      fieldBean.putProperty(IFieldNode.VISIBILITY, IVisibility.PRIVATE);
    } else {
      fieldBean.putProperty(IFieldNode.VISIBILITY, IVisibility.DEFAULT);
    }

    // Field-specific modifiers governing runtime behavior: transient and volatile
    fieldBean.putProperty(IFieldNode.TRANSIENT, (access & Opcodes.ACC_TRANSIENT) == Opcodes.ACC_TRANSIENT);
    fieldBean.putProperty(IFieldNode.VOLATILE, (access & Opcodes.ACC_VOLATILE) == Opcodes.ACC_VOLATILE);

    // Modifier restricting to one instance: static
    fieldBean.putProperty(IFieldNode.STATIC, (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC);

    // Modifier prohibiting value modification: final
    fieldBean.putProperty(IFieldNode.FINAL, (access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL);

    // deprecation
    fieldBean.putProperty(IFieldNode.DEPRECATED, (access & Opcodes.ACC_DEPRECATED) == Opcodes.ACC_DEPRECATED);

    //
    if (signature != null) {

      // signature
      fieldBean.putProperty(IFieldNode.SIGNATURE, signature);

      //
      new SignatureReader(signature)
          .acceptType(new JTypeTypeSignatureVisitor(fieldBean, this._classLocalReferenceCache));
    }

    // TODO
    return new JTypeFieldVisitor(this);
  }

  @Override
  public void visitSource(String source, String debug) {
    this._typeBean.putProperty(ITypeNode.SOURCE_FILE_NAME, source);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void visitOuterClass(String owner, String name, String rawSignature) {

    // owner
    this._classLocalReferenceCache.addTypeReference(this._typeBean, Type.getObjectType(owner),
        JTypeModelRelationshipType.REFERENCES);

    // TODO: EnclosingMethod!
    if (name != null && rawSignature != null) {

      // return type
      Type returnType = org.objectweb.asm.Type.getReturnType(rawSignature);
      if (!Utils.isVoidOrPrimitive(returnType)) {
        this._classLocalReferenceCache.addTypeReference(this._typeBean,
            Utils.resolveArrayType(returnType).getClassName(), JTypeModelRelationshipType.REFERENCES);
      }

      // arg types type
      org.objectweb.asm.Type[] types = org.objectweb.asm.Type.getArgumentTypes(rawSignature);
      for (int i = 0; i < types.length; i++) {
        if (!Utils.isVoidOrPrimitive(types[i])) {

          // TODO: array types!
          this._classLocalReferenceCache.addTypeReference(this._typeBean,
              Utils.resolveArrayType(types[i]).getClassName(), JTypeModelRelationshipType.REFERENCES);
        }
      }
    }
  }

  @Override
  public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
    // TODO Auto-generated method stub
    return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
  }

  @Override
  public void visitInnerClass(String name, String outerName, String innerName, int access) {

    //
    String fullyQualifiedName = name.replace('/', '.');
    String computedOuterFullyQualifiedName = fullyQualifiedName.substring(0, fullyQualifiedName.lastIndexOf('$'));

    //
    if (fullyQualifiedName.equals(this._typeBean.getFullyQualifiedName())) {

      // named inner class...
      if (outerName != null) {

        //
        this._classLocalReferenceCache.addTypeReference(this._typeBean, outerName.replace('/', '.'),
            JTypeModelRelationshipType.IS_INNER_CLASS_DEFINED_BY);

        this._typeBean.putProperty(ITypeNode.OUTER_CLASSNAME, outerName.replace('/', '.'));
      }

      // anonymous inner class
      else {

        //
        this._classLocalReferenceCache.addTypeReference(this._typeBean, computedOuterFullyQualifiedName,
            JTypeModelRelationshipType.IS_INNER_CLASS_DEFINED_BY);

        //
        this._typeBean.putProperty(ITypeNode.OUTER_CLASSNAME, computedOuterFullyQualifiedName);

      }

      this._typeBean.putProperty(ITypeNode.INNER_CLASS, true);

      // access flags
      // http://stackoverflow.com/questions/24622658/access-flag-for-private-inner-classes-in-java-spec-inconsistent-with-reflectio
      if (access != 0) {

        this._typeBean.putProperty(ITypeNode.INNER_CLASS_ACCESS_FLAGS, Integer.toHexString(access).toUpperCase());

        //
        if ((access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC) {
          this._typeBean.putProperty(ITypeNode.INNER_CLASS_ACCESS_LEVEL, IVisibility.PUBLIC);
        }
        //
        else if ((access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED) {
          this._typeBean.putProperty(ITypeNode.INNER_CLASS_ACCESS_LEVEL, IVisibility.PROTECTED);
        }
        //
        else if ((access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE) {
          this._typeBean.putProperty(ITypeNode.INNER_CLASS_ACCESS_LEVEL, IVisibility.PRIVATE);
        }
        //
        else {
          this._typeBean.putProperty(ITypeNode.INNER_CLASS_ACCESS_LEVEL, IVisibility.DEFAULT);
        }
      }
    }

    // TODO
    else if (computedOuterFullyQualifiedName.equals(this._typeBean.getFullyQualifiedName())) {

      //
      this._classLocalReferenceCache.addTypeReference(this._typeBean, fullyQualifiedName,
          JTypeModelRelationshipType.DEFINES_INNER_CLASS);
    }

    // //
    // else {
    // this._classLocalReferenceCache.addTypeReference(this._typeBean, outerName.replace('/', '.'),
    // JTypeModelRelationshipType.REFERENCES);
    // }

    //
    super.visitInnerClass(name, outerName, innerName, access);
  }

  @Override
  public void visitEnd() {
    // TODO Auto-generated method stub
    super.visitEnd();
  }

  /**
   * <p>
   * </p>
   *
   * @param fieldBean
   * @param type
   */
  private void addReference(INode fieldBean, Type type, JTypeModelRelationshipType relationshipType) {

    //
    Type t = Utils.resolveArrayType(type);

    //
    if (Utils.isVoid(type)) {
      return;
    } else if (Utils.isPrimitive(t)) {
      IRelationship relationship = fieldBean.addRelationship(
          Utils.getPrimitiveDatatypeNode(t, this._parserFactory.getDatatypeNodeProviderMap()), relationshipType);
    } else {
      this._classLocalReferenceCache.addTypeReference(fieldBean, t.getClassName(), relationshipType);
    }
  }

  public JTypeLabel getJTypeLabel(int access) {

    // handle annotation
    if ((access & Opcodes.ACC_ANNOTATION) == Opcodes.ACC_ANNOTATION) {
      return JTypeLabel.Annotation;
    }
    // handle interface
    else if ((access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE) {
      return JTypeLabel.Interface;
    }
    // handle enum
    else if ((access & Opcodes.ACC_ENUM) == Opcodes.ACC_ENUM) {
      return JTypeLabel.Enum;
    }
    // handle class
    else {
      return JTypeLabel.Class;
    }
  }
}
