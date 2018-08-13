/*******************************************************************************
 * Copyright (c) 2011-2015 Slizaa project team. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Slizaa project team - initial API and implementation
 ******************************************************************************/
package org.slizaa.jtype.scanner.model;

import org.slizaa.scanner.core.spi.parser.model.RelationshipType;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public enum JTypeModelRelationshipType implements RelationshipType {

  EXTENDS, IMPLEMENTS, BOUND_TO, HAS_PARAMETER, RETURNS, THROWS, IS_OF_TYPE, READS, WRITES, READS_FIELD_OF_TYPE, WRITES_FIELD_OF_TYPE, ANNOTATED_BY, DEFINES_LOCAL_VARIABLE, USES_TYPE_CONSTANT, INVOKES, INVOKES_METHOD_FROM, INVOKED_METHOD_RETURNS, INVOKED_METHOD_HAS_PARAMETER, DEFINES_INNER_CLASS, IS_INNER_CLASS_DEFINED_BY, INNER_CLASS_REFERENCES, IS_DEFINED_BY,

  //
  DEPENDS_ON,

  @Deprecated
  REFERENCES, 
}
