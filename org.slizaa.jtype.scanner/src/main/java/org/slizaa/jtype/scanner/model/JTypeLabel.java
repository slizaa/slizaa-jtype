/*******************************************************************************
 * Copyright (c) 2011-2015 Slizaa project team. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Slizaa project team - initial API and implementation
 ******************************************************************************/
package org.slizaa.jtype.scanner.model;

import org.slizaa.scanner.core.spi.parser.model.Label;

public enum JTypeLabel implements Label {
  Void, PrimitiveDataType, Type, Method, Field, TypeReference, MethodReference, FieldReference, Annotation, Class, Enum, Interface, Constructor, Package, ClassFile,

  //
  MissingType;
}
