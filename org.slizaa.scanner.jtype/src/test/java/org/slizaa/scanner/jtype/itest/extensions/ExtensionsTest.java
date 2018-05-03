/*******************************************************************************
 * Copyright (c) 2011-2015 Slizaa project team. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Slizaa project team - initial API and implementation
 ******************************************************************************/
package org.slizaa.scanner.jtype.itest.extensions;

import static org.slizaa.scanner.neo4j.testfwk.ContentDefinitionsUtils.simpleBinaryFile;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.StatementResult;
import org.slizaa.scanner.jtype.graphdbextensions.JTypeProcedures;
import org.slizaa.scanner.neo4j.testfwk.SlizaaClientRule;
import org.slizaa.scanner.neo4j.testfwk.SlizaaTestServerRule;

public class ExtensionsTest {

  /** - */
  @ClassRule
  public static SlizaaTestServerRule _server = new SlizaaTestServerRule(simpleBinaryFile("jtype", "1.2.3",
      ExtensionsTest.class.getProtectionDomain().getCodeSource().getLocation().getFile()))
          .withExtensionClass(JTypeProcedures.class);

  /** - */
  @Rule
  public SlizaaClientRule            _client = new SlizaaClientRule();

  @Test
  public void test_createGroup_1() {
    
    // create
    StatementResult statementResult = this._client.getSession().run("CALL slizaa.createGroup('spunk/dunk')");
    System.out.println(statementResult.single().asMap());
    
    // test
    statementResult = this._client.getSession().run("MATCH p=(g1:Group {name:'spunk'})-[:CONTAINS]->(g2:Group {name:'dunk'}) RETURN p");
    System.out.println(statementResult.single().asMap());
  }
  
  
  /**
   * <p>
   * </p>
   */
  @Test
  public void test_Procedure_1() {
    StatementResult statementResult = this._client.getSession().run("CALL slizaa.createModule( {fqn: 'spunk/dunk'} )");
    System.out.println(statementResult.single().asMap());
    // assertThat(statementResult.single().asMap().containsKey("slizaa.currentTimestamp()"));
  }
  
  /**
   * <p>
   * </p>
   */
  @Test
  public void test_Procedure_2() {
    StatementResult statementResult = this._client.getSession().run("CALL slizaa.createModules(['spunk/dunk', 'flunk/punk'])");
    System.out.println(statementResult.list(o -> o.get("out")));
    // assertThat(statementResult.single().asMap().containsKey("slizaa.currentTimestamp()"));
  }
}