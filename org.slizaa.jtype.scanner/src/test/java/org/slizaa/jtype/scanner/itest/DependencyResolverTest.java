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

public class DependencyResolverTest {

  @ClassRule
  public static JTypeSlizaaTestServerRule _server = new JTypeSlizaaTestServerRule(
      multipleBinaryMvnArtifacts(new String[] { "com.netflix.eureka", "eureka-core", "1.8.2" },
          new String[] { "com.netflix.eureka", "eureka-client", "1.8.2" }));

  @Rule
  public BoltClientConnectionRule         _client = new BoltClientConnectionRule();

  /**
   * <p>
   * </p>
   */
  @Test
  public void testDependencyResolver() {

    // check type references
    StatementResult statementResult = this._client.getBoltClient()
        .syncExecCypherQuery("MATCH (tref:TypeReference)-[rel:BOUND_TO {derived:true}]->(t:Type) RETURN count(rel)");
    assertThat(statementResult.single().get("count(rel)").asInt()).isEqualTo(2408);

    statementResult = this._client.getBoltClient().syncExecCypherQuery(
        "MATCH p=(t1:Type)-[:DEPENDS_ON]->(tref:TypeReference)-[:BOUND_TO {derived:true}]->(t2:Type) RETURN count(p)");
    assertThat(statementResult.single().get("count(p)").asInt()).isEqualTo(2061);

    statementResult = this._client.getBoltClient()
        .syncExecCypherQuery("MATCH p=(sourceNode)-[rel]->(tref:TypeReference)-[:BOUND_TO]->(t:Type) RETURN count(p)");
    assertThat(statementResult.single().get("count(p)").asInt()).isEqualTo(32286);

    // check method references
    statementResult = this._client.getBoltClient().syncExecCypherQuery(
        "MATCH (mref:MethodReference)-[rel:BOUND_TO {derived:true}]->(m:Method) RETURN count(rel)");
    assertThat(statementResult.single().get("count(rel)").asInt()).isEqualTo(2540);

    statementResult = this._client.getBoltClient().syncExecCypherQuery(
        "MATCH p=(sourceNode)-[rel]->(mref:MethodReference)-[:BOUND_TO]->(method:Method) RETURN count(p)");
    assertThat(statementResult.single().get("count(p)").asInt()).isEqualTo(4129);

    // check field references
    statementResult = this._client.getBoltClient()
        .syncExecCypherQuery("MATCH (fref:FieldReference)-[rel:BOUND_TO {derived:true}]->(f:Field) RETURN count(rel)");
    assertThat(statementResult.single().get("count(rel)").asInt()).isEqualTo(1492);

    statementResult = this._client.getBoltClient().syncExecCypherQuery(
        "MATCH p=(sourceNode)-[rel]->(fref:FieldReference)-[:BOUND_TO]->(f:Field) RETURN count(p)");
    assertThat(statementResult.single().get("count(p)").asInt()).isEqualTo(5333);

    // unbound type references (3514)
    statementResult = this._client.getBoltClient()
        .syncExecCypherQuery("MATCH (tref:TypeReference) WHERE NOT (tref)-[:BOUND_TO]->(:Type) RETURN count(tref)");
    assertThat(statementResult.single().get("count(tref)").asInt()).isEqualTo(3490);

    // unbound method references (3549)
    statementResult = this._client.getBoltClient()
        .syncExecCypherQuery("MATCH (mref:MethodReference) WHERE NOT (mref)-[:BOUND_TO]->(:Method) RETURN count(mref)");
    assertThat(statementResult.single().get("count(mref)").asInt()).isEqualTo(3549);

    // unbound field references (150)
    statementResult = this._client.getBoltClient()
        .syncExecCypherQuery("MATCH (fref:FieldReference) WHERE NOT (fref)-[:BOUND_TO]->(:Field) RETURN count(fref)");
    assertThat(statementResult.single().get("count(fref)").asInt()).isEqualTo(150);
  }

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
  }
}
