/*******************************************************************************
 * Copyright (c) 2011-2015 Slizaa project team. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Slizaa project team - initial API and implementation
 ******************************************************************************/
package org.slizaa.scanner.jtype.itest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slizaa.scanner.neo4j.testfwk.ContentDefinitionsUtils.multipleBinaryMvnArtifacts;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.StatementResult;
import org.slizaa.scanner.neo4j.testfwk.SlizaaClientRule;
import org.slizaa.scanner.neo4j.testfwk.SlizaaTestServerRule;

/**
  */
public class CreateSampleDatabase {

  @ClassRule
  public static SlizaaTestServerRule _server = new SlizaaTestServerRule(
      multipleBinaryMvnArtifacts(new String[] { "org.mapstruct", "mapstruct", "1.2.0.Final" },
          new String[] { "org.mapstruct", "mapstruct-processor", "1.2.0.Final" }));

  @Rule
  public SlizaaClientRule            _client = new SlizaaClientRule();

  /**
   * <p>
   * </p>
   *
   * @throws Exception
   */
  @Test
  public void test() throws Exception {

    //
    StatementResult statementResult = this._client.getSession().run("Match (t:Type) return count(t)");
    assertThat(statementResult.single().get(0).asInt()).isEqualTo(1054);

    //
    _server.exportDatabaseAsZipFile("mapstruct_1-2-0-Final-db.zip", false);
  }
}
