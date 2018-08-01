package org.slizaa.jtype.scanner.bytecode.internal.signature;

import static com.google.common.base.Preconditions.checkNotNull;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;
import org.slizaa.jtype.scanner.bytecode.internal.TypeLocalReferenceCache;
import org.slizaa.jtype.scanner.model.JTypeModelRelationshipType;
import org.slizaa.scanner.core.spi.parser.model.INode;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public abstract class AbstractSignatureVisitor extends SignatureVisitor {

  /** - */
  private INode                   _bean;

  /** - */
  private TypeLocalReferenceCache _classLocalTypeReferenceCache;

  /**
   * <p>
   * Creates a new instance of type {@link AbstractSignatureVisitor}.
   * </p>
   *
   * @param bean
   * @param classLocalTypeReferenceCache
   */
  public AbstractSignatureVisitor(INode bean, TypeLocalReferenceCache classLocalTypeReferenceCache) {
    super(Opcodes.ASM6);

    //
    this._bean = checkNotNull(bean);

    //
    this._classLocalTypeReferenceCache = checkNotNull(classLocalTypeReferenceCache);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void visitClassType(final String name) {
    this._classLocalTypeReferenceCache.addTypeReference(this._bean, name, JTypeModelRelationshipType.REFERENCES);
  }
}
