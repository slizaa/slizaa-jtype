/*******************************************************************************
 * Copyright (c) 2011-2015 Slizaa project team. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Slizaa project team - initial API and implementation
 ******************************************************************************/
package org.slizaa.jtype.scanner.bytecode;

import org.slizaa.jtype.scanner.bytecode.internal.PrimitiveDatatypeNodeProvider;
import org.slizaa.scanner.api.util.IProgressMonitor;
import org.slizaa.scanner.spi.annotations.ParserFactory;
import org.slizaa.scanner.spi.contentdefinition.IContentDefinitionProvider;
import org.slizaa.scanner.spi.parser.ICypherStatementExecutor;
import org.slizaa.scanner.spi.parser.IParser;
import org.slizaa.scanner.spi.parser.IParserFactory;

/**
 * <p>
 * The {@link IParserFactory} to create instances of {@link JTypeByteCodeParser}.
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
@ParserFactory
public class JTypeByteCodeParserFactory extends IParserFactory.Adapter implements IParserFactory {

  /** - */
  IPrimitiveDatatypeNodeProvider _datatypeNodeProvider = null;

  /**
   * <p>
   * </p>
   *
   * @return
   */
  public IPrimitiveDatatypeNodeProvider getDatatypeNodeProviderMap() {
    return this._datatypeNodeProvider;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IParser createParser(IContentDefinitionProvider contentDefinition) {
    return new JTypeByteCodeParser(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void batchParseStart(IContentDefinitionProvider contentDefinitions, ICypherStatementExecutor cypherStatementExecutor,
      IProgressMonitor subMonitor) throws Exception {

      this._datatypeNodeProvider = new PrimitiveDatatypeNodeProvider(cypherStatementExecutor);

    //
      cypherStatementExecutor.executeCypherStatement("create index on :Type(fqn)");
      cypherStatementExecutor.executeCypherStatement("create index on :TypeReference(fqn)");
      cypherStatementExecutor.executeCypherStatement("create index on :Field(fqn)");
      cypherStatementExecutor.executeCypherStatement("create index on :FieldReference(fqn)");
      cypherStatementExecutor.executeCypherStatement("create index on :Method(fqn)");
      cypherStatementExecutor.executeCypherStatement("create index on :MethodReference(fqn)");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void batchParseStop(IContentDefinitionProvider contentDefinition, ICypherStatementExecutor cypherStatementExecutor,
      IProgressMonitor subMonitor) {

    //
    this._datatypeNodeProvider = null;
  }
}
