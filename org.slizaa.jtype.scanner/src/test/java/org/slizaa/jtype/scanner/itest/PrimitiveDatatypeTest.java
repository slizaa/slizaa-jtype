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
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.StatementResult;
import org.slizaa.core.boltclient.testfwk.BoltClientConnectionRule;
import org.slizaa.jtype.scanner.JTypeSlizaaTestServerRule;

public class PrimitiveDatatypeTest {

  @ClassRule
  public static JTypeSlizaaTestServerRule _server = new JTypeSlizaaTestServerRule(
      multipleBinaryMvnArtifacts(new String[] { "com.google.guava", "guava", "23.6.1-jre" }));

  @Rule
  public BoltClientConnectionRule         _client = new BoltClientConnectionRule();

  /**
   * <p>
   * </p>
   */
  @Test
  public void testPrimitiveDataType() {

    // check type references
    StatementResult statementResult = this._client.getBoltClient()
        .syncExecCypherQuery("MATCH (p:PrimitiveDataType) RETURN count(p)");
    assertThat(statementResult.single().get("count(p)").asInt()).isEqualTo(8);

    //
    statementResult = this._client.getBoltClient().syncExecCypherQuery(
        "MATCH (t:TypeReference) WHERE t.fqn IN ['byte', 'short', 'int', 'long', 'float', 'double', 'char', 'boolean'] RETURN count(t)");
    assertThat(statementResult.single().get("count(t)").asInt()).isEqualTo(0);

  }
}
