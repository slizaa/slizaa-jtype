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

public class DumpProceduresTest {

  @ClassRule
  public static SlizaaTestServerRule _server = new SlizaaTestServerRule(
      multipleBinaryMvnArtifacts(new String[] { "com.netflix.eureka", "eureka-core", "1.8.2" },
          new String[] { "com.netflix.eureka", "eureka-client", "1.8.2" }));

  @Rule
  public SlizaaClientRule            _client = new SlizaaClientRule();

  /**
   * <p>
   * </p>
   */
  @Test
  public void testDependencyResolver() {

    // StatementResult res = this._client.getSession()
    // .run("CALL dbms.procedures() YIELD name, signature WITH * WHERE name STARTS WITH 'slizaa.derivedRelationship'
    // RETURN name, signature");
    // System.out.println(res.single().asMap());

    // check types
    StatementResult statementResult = this._client.getSession()
        .run("MATCH (tref:TypeReference)-[rel:BOUND_TO {derived:true}]->(t:Type) RETURN count(rel)");
    assertThat(statementResult.single().get("count(rel)").asInt()).isEqualTo(2403);

    statementResult = this._client.getSession()
        .run("MATCH (t1:Type)-[rel:DEPENDS_ON {derived:true}]->(t2:Type) RETURN count(rel)");
    assertThat(statementResult.single().get("count(rel)").asInt()).isEqualTo(2061);

    // //
    // statementResult = this._client.getSession()
    // .run("MATCH (mref:MethodReference)-[rel:BOUND_TO {derived:true}]->(m:Method) RETURN count(rel)");
    // assertThat(statementResult.single().get("count(rel)").asInt()).isEqualTo(2540);
    //
    // statementResult = this._client.getSession()
    // .run("MATCH p=(sourceNode)-[rel]->(mref:MethodReference)-[:BOUND_TO]->(method:Method) RETURN count(p)");
    // System.out.println("result: " + statementResult.single().get("count(p)").asInt());
    // assertThat(statementResult.single().get("count(p)").asInt()).isEqualTo(2540);
    //
    // //
    // statementResult = this._client.getSession()
    // .run("MATCH (fref:FieldReference)-[rel:BOUND_TO {derived:true}]->(f:Field) RETURN count(rel)");
    // assertThat(statementResult.single().get("count(rel)").asInt()).isEqualTo(1492);

    // //
    // statementResult = this._client.getSession()
    // .run("MATCH (fref:FieldReference) WHERE NOT (fref)-[:BOUND_TO {derived:true}]->(:Field) RETURN count(fref)");
    // System.out.println(statementResult.single().get("count(fref)").asInt());
    // assertThat(statementResult.single().get("count(fref)").asInt()).isEqualTo(1492);

  }
}
