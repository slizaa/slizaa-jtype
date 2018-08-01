/**
 *
 */
package org.slizaa.jtype.scanner.bytecode.internal.signature;

import org.slizaa.jtype.scanner.bytecode.internal.TypeLocalReferenceCache;
import org.slizaa.scanner.core.spi.parser.model.INode;

/**
 * <p>
 * TypeSignature</i> = <tt>visitBaseType</tt> | <tt>visitTypeVariable</tt> | <tt>visitArrayType</tt> | (
 * <tt>visitClassType</tt> <tt>visitTypeArgument</tt>* ( <tt>visitInnerClassType</tt> <tt>visitTypeArgument</tt>* )*
 * <tt>visitEnd</tt> ) )
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class JTypeTypeSignatureVisitor extends AbstractSignatureVisitor {

  /**
   * <p>
   * Creates a new instance of type {@link JTypeTypeSignatureVisitor}.
   * </p>
   *
   * @param bean
   * @param classLocalTypeReferenceCache
   */
  public JTypeTypeSignatureVisitor(INode bean, TypeLocalReferenceCache classLocalTypeReferenceCache) {
    super(bean, classLocalTypeReferenceCache);
  }
}
