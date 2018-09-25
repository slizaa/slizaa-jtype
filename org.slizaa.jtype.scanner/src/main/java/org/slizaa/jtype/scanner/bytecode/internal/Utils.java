package org.slizaa.jtype.scanner.bytecode.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import org.objectweb.asm.Type;
import org.slizaa.jtype.scanner.bytecode.IPrimitiveDatatypeNodeProvider;
import org.slizaa.scanner.spi.parser.model.INode;

public class Utils {

  /**
   * <p>
   * </p>
   *
   * @param type
   * @return
   */
  public static boolean isArray(Type type) {
   return checkNotNull(type).getSort() == Type.ARRAY;
  }
  
  /**
   * <p>
   * </p>
   */
  public static Type resolveArrayType(Type type) {
    switch (checkNotNull(type).getSort()) {
    case Type.ARRAY:
      return resolveArrayType(type.getElementType());
    default:
      return type;
    }
  }

  public static boolean isVoidOrPrimitive(Type type) {
    return isVoid(type) || isPrimitive(type);
  }

  public static boolean isVoid(Type type) {

    type = resolveArrayType(type);

    return type.getSort() == Type.VOID;
  }

  /**
   * <p>
   * </p>
   *
   * @param type
   * @return
   */
  public static boolean isPrimitive(Type type) {

    type = resolveArrayType(type);

    return type.getSort() == Type.BOOLEAN | type.getSort() == Type.BYTE | type.getSort() == Type.CHAR
        | type.getSort() == Type.DOUBLE | type.getSort() == Type.FLOAT | type.getSort() == Type.INT
        | type.getSort() == Type.LONG | type.getSort() == Type.SHORT;
  }

  public static INode getPrimitiveDatatypeNode(Type type,
      IPrimitiveDatatypeNodeProvider primitiveDatatypeNodeProvider) {

    type = resolveArrayType(type);

    switch (type.getSort()) {
    case Type.BOOLEAN: {
      return primitiveDatatypeNodeProvider.getPrimitiveDatatypeBoolean();
    }
    case Type.BYTE: {
      return primitiveDatatypeNodeProvider.getPrimitiveDatatypeByte();
    }
    case Type.CHAR: {
      return primitiveDatatypeNodeProvider.getPrimitiveDatatypeChar();
    }
    case Type.DOUBLE: {
      return primitiveDatatypeNodeProvider.getPrimitiveDatatypeDouble();
    }
    case Type.FLOAT: {
      return primitiveDatatypeNodeProvider.getPrimitiveDatatypeFloat();
    }
    case Type.INT: {
      return primitiveDatatypeNodeProvider.getPrimitiveDatatypeInt();
    }
    case Type.LONG: {
      return primitiveDatatypeNodeProvider.getPrimitiveDatatypeLong();
    }
    case Type.SHORT: {
      return primitiveDatatypeNodeProvider.getPrimitiveDatatypeShort();
    }
    default:
      // TODO
      throw new RuntimeException();
    }
  }

  /**
   * <p>
   * </p>
   *
   * @param type
   * @return
   */
  public static String typeToString(Type type) {

    //
    Type t = Utils.resolveArrayType(type);

    //
    if (Utils.isVoid(t)) {
      return "void";
    }
    //
    else if (Utils.isPrimitive(t)) {
      switch (t.getSort()) {
      case Type.BOOLEAN: {
        return "boolean";
      }
      case Type.BYTE: {
        return "byte";
      }
      case Type.CHAR: {
        return "char";
      }
      case Type.DOUBLE: {
        return "double";
      }
      case Type.FLOAT: {
        return "float";
      }
      case Type.INT: {
        return "int";
      }
      case Type.LONG: {
        return "long";
      }
      case Type.SHORT: {
        return "short";
      }
      default: {
        // TODO
        throw new RuntimeException(t.toString());
      }
      }
    }
    //
    else {
      return t.getClassName();
    }
  }

  // /**
  // * Returns the Java type name corresponding to the given internal name.
  // *
  // * @param desc
  // * The internal name.
  // * @return The type name.
  // */
  // public static String getObjectType(String desc) {
  // return getType(Type.getObjectType(desc));
  // }
  //
  // /**
  // * Returns the Java type name type corresponding to the given type descriptor.
  // *
  // * @param desc
  // * The type descriptor.
  // * @return The type name.
  // */
  // public static String getType(String desc) {
  // return getType(Type.getType(desc));
  // }
  //
  // /**
  // * Return the type name of the given ASM type.
  // *
  // * @param t
  // * The ASM type.
  // * @return The type name.
  // */
  // public static String getType(final Type t) {
  // switch (t.getSort()) {
  // case Type.ARRAY:
  // return getType(t.getElementType());
  // default:
  // return t.getClassName();
  // }
  // }

  /**
   * @param type
   * @return
   */
  public static String getFullyQualifiedTypeName(Type type) {
    return resolveArrayType(type).getClassName();
  }

  /**
   * Return a method signature.
   *
   * @param name
   *          The method name.
   * @param rawSignature
   *          The signature containing parameter, return and exception values.
   * @return The method signature.
   */
  public static String getMethodSignature(String name, String rawSignature) {
    StringBuilder signature = new StringBuilder();
    String returnType = org.objectweb.asm.Type.getReturnType(rawSignature).getClassName();
    if (returnType != null) {
      signature.append(returnType);
      signature.append(' ');
    }
    signature.append(name);
    signature.append('(');
    org.objectweb.asm.Type[] types = org.objectweb.asm.Type.getArgumentTypes(rawSignature);
    for (int i = 0; i < types.length; i++) {
      if (i > 0) {
        signature.append(',');
      }
      signature.append(types[i].getClassName());
    }
    signature.append(')');
    return signature.toString();
  }

  /**
   * Return a field signature.
   *
   * @param name
   *          The field name.
   * @param rawSignature
   *          The signature containing the type value.
   * @return The field signature.
   */
  public static String getFieldSignature(String name, String rawSignature) {
    return String.format("%s %s", org.objectweb.asm.Type.getType(rawSignature).getClassName(), name);
  }
}
