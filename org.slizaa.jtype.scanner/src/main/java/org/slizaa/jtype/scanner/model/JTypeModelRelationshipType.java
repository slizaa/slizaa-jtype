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
package org.slizaa.jtype.scanner.model;

import org.slizaa.scanner.core.spi.parser.model.RelationshipType;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public enum JTypeModelRelationshipType implements RelationshipType {

  EXTENDS, IMPLEMENTS, BOUND_TO, HAS_PARAMETER, RETURNS, THROWS, IS_OF_TYPE, READS, WRITES, ANNOTATED_BY, USES, INVOKES, DEFINES_INNER_CLASS, IS_INNER_CLASS_DEFINED_BY, IS_DEFINED_BY,

  //
  DEPENDS_ON,

  @Deprecated
  REFERENCES, 
}