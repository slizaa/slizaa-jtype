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
package org.slizaa.jtype.scanner.bytecode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.slizaa.jtype.scanner.bytecode.internal.JTypeClassVisitor;
import org.slizaa.jtype.scanner.model.JTypeLabel;
import org.slizaa.jtype.scanner.model.JTypeModelRelationshipType;
import org.slizaa.jtype.scanner.model.JavaUtils;
import org.slizaa.scanner.spi.contentdefinition.IContentDefinition;
import org.slizaa.scanner.spi.contentdefinition.filebased.IFile;
import org.slizaa.scanner.spi.parser.AbstractParser;
import org.slizaa.scanner.spi.parser.IParser;
import org.slizaa.scanner.spi.parser.IParserContext;
import org.slizaa.scanner.spi.parser.IParserFactory;
import org.slizaa.scanner.spi.parser.ParserType;
import org.slizaa.scanner.spi.parser.model.INode;
import org.slizaa.scanner.spi.parser.model.resource.CoreModelRelationshipType;

/**
 * <p>
 * A {@link IParser} that parses java class files and creates a graph representation of the contained types.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class JTypeByteCodeParser extends AbstractParser<JTypeByteCodeParserFactory> {

  /**
   * <p>
   * Creates a new instance of type {@link JTypeByteCodeParser}.
   * </p>
   * 
   * @param parserFactory
   *          the {@link IParserFactory} that created this {@link IParser}.
   */
  JTypeByteCodeParser(JTypeByteCodeParserFactory parserFactory) {
    super(parserFactory);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ParserType getParserType() {
    return ParserType.BINARY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canParse(IFile resource) {

    //
    if (!resource.getPath().endsWith(".class")) {
      return false;
    }

    //
    return JavaUtils.isValidJavaPackage(resource.getDirectory());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doParseResource(IContentDefinition content, IFile resource, INode resourceBean,
      IParserContext context) {

    // tag parent directory as package
    if (!context.getParentDirectoryNode().getLabels().contains(JTypeLabel.Package)) {
      context.getParentDirectoryNode().addLabel(JTypeLabel.Package);
    }

    // add CLASSFILE label
    resourceBean.addLabel(JTypeLabel.ClassFile);

    // create the visitor...
    JTypeClassVisitor visitor = new JTypeClassVisitor(this.getParserFactory());

    // ...parse the class
    ClassReader reader = new ClassReader(resource.getContent());
    reader.accept(visitor, ClassReader.EXPAND_FRAMES);

    // the constant pool may references classes that are not referenced from within the code, e.g. see
    // https://bugs.openjdk.java.net/browse/JDK-7153958
    Set<String> allReferencedTypes = visitor.getTypeLocalReferenceCache().getAllReferencedTypes();
    for (String constantPoolClass : getConstantPoolClasses(reader)) {
      if (!allReferencedTypes.contains(constantPoolClass.replace('/', '.'))) {
        visitor.getTypeLocalReferenceCache().addTypeReference(visitor.getTypeBean(),
            constantPoolClass.replace('/', '.'), JTypeModelRelationshipType.REFERENCES);
      }
    }

    // add the contained type
    resourceBean.addRelationship(visitor.getTypeBean(), CoreModelRelationshipType.CONTAINS);
  }

  /**
   * <p>
   * </p>
   *
   * @param reader
   * @return
   */
  public static Set<String> getConstantPoolClasses(ClassReader reader) {
    Set<String> strings = new HashSet<>();

    int itemCount = reader.getItemCount();
    char[] buffer = new char[reader.getMaxStringLength()];

    for (int n = 1; n < itemCount; n++) {
      int pos = reader.getItem(n);
      if (pos == 0 || reader.b[pos - 1] != 7) {
        continue;
      }

      Arrays.fill(buffer, (char) 0);
      String string = reader.readUTF8(pos, buffer);

      if (string.startsWith("[")) {
        continue;
      }
      if (string.length() < 1) {
        continue;
      }

      strings.add(string);
    }

    return strings;
  }
}
