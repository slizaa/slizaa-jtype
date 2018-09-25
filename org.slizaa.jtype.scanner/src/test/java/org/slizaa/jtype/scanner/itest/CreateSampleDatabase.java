/*******************************************************************************
 * Copyright (c) 2011-2015 Slizaa project team. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Slizaa project team - initial API and implementation
 ******************************************************************************/
package org.slizaa.jtype.scanner.itest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slizaa.scanner.testfwk.ContentDefinitionProviderFactory.multipleBinaryMvnArtifacts;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.StatementResult;
import org.slizaa.core.boltclient.testfwk.BoltClientConnectionRule;
import org.slizaa.jtype.scanner.JTypeSlizaaTestServerRule;

/**
  */
public class CreateSampleDatabase {

  @ClassRule
  public static JTypeSlizaaTestServerRule _server = new JTypeSlizaaTestServerRule(
      multipleBinaryMvnArtifacts(new String[] { "org.mapstruct", "mapstruct", "1.2.0.Final" },
          new String[] { "org.mapstruct", "mapstruct-processor", "1.2.0.Final" }));

  @Rule
  public BoltClientConnectionRule         _client = new BoltClientConnectionRule();

  /**
   * <p>
   * </p>
   *
   * @throws Exception
   */
  @Test
  @Ignore
  public void test() throws Exception {

    //
    StatementResult statementResult = this._client.getBoltClient()
        .syncExecCypherQuery("Match (t:Type) return count(t)");
    assertThat(statementResult.single().get(0).asInt()).isEqualTo(1054);

    //
    _server.exportDatabaseAsZipFile("mapstruct_1-2-0-Final-db.zip", false);
  }
}
