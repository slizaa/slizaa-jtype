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
package org.slizaa.scanner.jtype.itest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slizaa.scanner.neo4j.testfwk.ContentDefinitionsUtils.simpleBinaryFile;

import java.util.Collections;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;
import org.slizaa.scanner.jtype.itest.examplecode.AbstractExampleClass;
import org.slizaa.scanner.jtype.itest.examplecode.ExampleClass;
import org.slizaa.scanner.jtype.itest.examplecode.ExampleInterface;
import org.slizaa.scanner.jtype.itest.examplecode.SimpleClassWithOneField;
import org.slizaa.scanner.jtype.itest.examplecode.SuperClass;
import org.slizaa.scanner.jtype.itest.examplecode.SuperInterface;
import org.slizaa.scanner.jtype.model.ITypeNode;
import org.slizaa.scanner.neo4j.testfwk.SlizaaClientRule;
import org.slizaa.scanner.neo4j.testfwk.SlizaaTestServerRule;

public class ExampleCodeTest {

  @ClassRule
  public static SlizaaTestServerRule _server = new SlizaaTestServerRule(simpleBinaryFile("jtype", "1.2.3",
      ExampleCodeTest.class.getProtectionDomain().getCodeSource().getLocation().getFile()));

  @Rule
  public SlizaaClientRule            _client = new SlizaaClientRule();

  /**
   * <p>
   * </p>
   */
  @Test
  public void testTypeType() {

    //
    StatementResult statementResult = _client.getSession().run("Match (t:Type {fqn: $name }) return t",
        Collections.singletonMap("name", ExampleInterface.class.getName()));
    Node node = statementResult.single().get(0).asNode();

    // asserts
    assertThat(node.labels()).containsOnly("Type", "Interface");
    assertThat(node.asMap()).containsEntry(ITypeNode.FQN, ExampleInterface.class.getName());
    assertThat(node.asMap()).containsEntry(ITypeNode.NAME, ExampleInterface.class.getSimpleName());
    assertThat(node.asMap()).containsEntry(ITypeNode.VISIBILITY, "public");
    assertThat(node.asMap()).containsEntry(ITypeNode.SOURCE_FILE_NAME, "ExampleInterface.java");
    assertThat(node.asMap()).containsEntry(ITypeNode.CLASS_VERSION, "52");
    assertThat(node.asMap()).containsEntry(ITypeNode.ABSTRACT, true);

    // Node node = getSingleNode(executeStatement("Match (t:Type {fqn: $name}) return t",
    // Collections.singletonMap("name", ExampleClass.class.getName())));
    // assertThat(node.hasLabel(convert(JTypeLabel.Type))).isTrue();
    // assertThat(node.hasLabel(convert(JTypeLabel.CLASS))).isTrue();
    // assertThat(node.getProperty(IMethodNode.VISIBILITY)).isEqualTo("public");

    // node = getTypeNode(ExampleInterface.class.getName());
    // assertThat(node.hasLabel(convert(JTypeLabel.Type)), is(true));
    // assertThat(node.hasLabel(convert(JTypeLabel.Interface)), is(true));
    //
    // node = getTypeNode(ExampleEnum.class.getName());
    // assertThat(node.hasLabel(convert(JTypeLabel.Type)), is(true));
    // assertThat(node.hasLabel(convert(JTypeLabel.ENUM)), is(true));
    //
    // node = getTypeNode(ExampleAnnotation.class.getName());
    // assertThat(node.hasLabel(convert(JTypeLabel.Type)), is(true));
    // assertThat(node.hasLabel(convert(JTypeLabel.ANNOTATION)), is(true));
  }

  /**
   * <p>
   * 
   * <pre>
   * public class org.slizaa.scanner.itest.jtype.simple.example.ExampleClass extends org.slizaa.scanner.itest.jtype.simple.example.SuperClass implements org.slizaa.scanner.itest.jtype.simple.example.SuperInterface
   * </pre>
   * </p>
   */
  @Test
  public void testImplements() {

    StatementResult statementResult = _client.getSession().run(
        "Match (t:Type {fqn: $name })-[rel:IMPLEMENTS]->(typeReference:TypeReference) return typeReference.fqn",
        Collections.singletonMap("name", ExampleClass.class.getName()));

    List<String> records = statementResult.list(rec -> rec.get(0).asString());

    assertThat(records).containsOnly(SuperInterface.class.getName());
  }

  /**
   * <p>
   * Tests that the 'EXTENDS' relationship exists.
   * 
   * <pre>
   * public class org.slizaa.scanner.itest.jtype.simple.example.ExampleClass extends org.slizaa.scanner.itest.jtype.simple.example.SuperClass implements org.slizaa.scanner.itest.jtype.simple.example.SuperInterface
   * </pre>
   * </p>
   */
  @Test
  public void testExtends_1() {

    StatementResult statementResult = _client.getSession().run(
        "Match (t:Type {fqn: $name })-[rel:EXTENDS]->(typeReference:TypeReference) return typeReference.fqn",
        Collections.singletonMap("name", ExampleClass.class.getName()));

    List<String> records = statementResult.list(rec -> rec.get(0).asString());

    assertThat(records).containsOnly(SuperClass.class.getName());
  }

  /**
   * <p>
   * Tests that the 'EXTENDS' relationship exists.
   * 
   * <pre>
   * public class org.slizaa.scanner.itest.jtype.simple.example.SuperClass
   * </pre>
   * </p>
   */
  @Test
  public void testExtends_2() {

    StatementResult statementResult = _client.getSession().run(
        "Match (t:Type {fqn: $name })-[rel:EXTENDS]->(typeReference:TypeReference) return typeReference.fqn",
        Collections.singletonMap("name", SuperClass.class.getName()));

    List<String> records = statementResult.list(rec -> rec.get(0).asString());

    assertThat(records).containsOnly(Object.class.getName());
  }

  /**
   * <p>
   * </p>
   */
  @Test
  public void testAbstract_1() {

    StatementResult statementResult = _client.getSession().run("Match (t:Type {fqn: $name }) return t.abstract",
        Collections.singletonMap("name", AbstractExampleClass.class.getName()));

    assertThat(statementResult.single().get(0).asBoolean()).isTrue();
  }

  /**
   * <p>
   * </p>
   */
  @Test
  public void testAbstract_2() {

    StatementResult statementResult = _client.getSession().run("Match (t:Type {fqn: $name }) return t.abstract",
        Collections.singletonMap("name", ExampleInterface.class.getName()));

    assertThat(statementResult.single().get(0).asBoolean()).isTrue();
  }

  /**
   * Tests that the METHOD_REFERENCE has 'IS_DEFINED_BY' and 'RETURNS' relationships:
   * 
   * <pre>
   * 4: invokeinterface #19,  1           // InterfaceMethod org/neo4j/graphdb/GraphDatabaseService.beginTx:()Lorg/neo4j/graphdb/Transaction;
   * </pre>
   */
  @Test
  public void testInvokesMethod() {

    //
    StatementResult statementResult = _client.getSession().run(
        "MATCH (m:Method {name: 'exampleInvokesMethod'})-[:INVOKES]->(mref:MethodReference)-[rel]->(typeReference:TypeReference) return mref.name, type(rel), typeReference.fqn");

    List<String[]> records = statementResult
        .list(rec -> new String[] { rec.get(0).asString(), rec.get(1).asString(), rec.get(2).asString() });

    assertThat(records).containsOnly(
        new String[] { "beginTx", "IS_DEFINED_BY", "org.neo4j.graphdb.GraphDatabaseService" },
        new String[] { "beginTx", "RETURNS", "org.neo4j.graphdb.Transaction" });
  }

  /**
   * <pre>
   * private java.io.Serializable _serializable;
   * descriptor: Ljava/io/Serializable;
   * flags: ACC_PRIVATE
   * </pre>
   */
  @Test
  public void testField_1() {

    //
    StatementResult statementResult = _client.getSession().run(
        "Match (t:Type {fqn: $name})-[:CONTAINS]->(f:Field) return f",
        Collections.singletonMap("name", SimpleClassWithOneField.class.getName()));
    List<Node> nodes = statementResult.list(rec -> rec.get(0).asNode());

    //
    assertThat(nodes).hasSize(1);
    assertThat(nodes.get(0).labels()).containsExactly("Field");
    assertThat(nodes.get(0).asMap()).containsOnlyKeys("accessFlags", "deprecated", "final", "fqn", "name", "static",
        "transient", "visibility", "volatile");
    assertThat(nodes.get(0).asMap()).containsEntry("accessFlags", "2");
    assertThat(nodes.get(0).asMap()).containsEntry("deprecated", false);
    assertThat(nodes.get(0).asMap()).containsEntry("final", false);
    assertThat(nodes.get(0).asMap()).containsEntry("fqn", "java.io.Serializable _serializable");
    assertThat(nodes.get(0).asMap()).containsEntry("name", "_serializable");
  }

  @Test
  public void testField_2() {

    //
    StatementResult statementResult = _client.getSession().run(
        "Match (t:Type {fqn: $name})-[:CONTAINS]->(f:Field) return f",
        Collections.singletonMap("name", ExampleClass.class.getName()));
    List<Node> nodes = statementResult.list(rec -> rec.get(0).asNode());

    //
    assertThat(nodes).hasSize(1);
    assertThat(nodes.get(0).labels()).containsExactly("Field");
    assertThat(nodes.get(0).asMap()).containsOnlyKeys("accessFlags", "deprecated", "final", "fqn", "name", "static",
        "transient", "visibility", "volatile");
    assertThat(nodes.get(0).asMap()).containsEntry("accessFlags", "2");
    assertThat(nodes.get(0).asMap()).containsEntry("deprecated", false);
    assertThat(nodes.get(0).asMap()).containsEntry("final", false);
    assertThat(nodes.get(0).asMap()).containsEntry("fqn", "java.io.Serializable _serializable");
    assertThat(nodes.get(0).asMap()).containsEntry("name", "_serializable");
  }

  @Test
  public void testFieldReference_1() {

    //
    StatementResult statementResult = _client.getSession().run(
        "Match (m:Method {name: $name})-[rel]->(fref) return rel",
        Collections.singletonMap("name", "fieldRef"));
    
//  StatementResult statementResult = _client.getSession().run(
//  "Match (m:Method) return m");
//    
    //
    for (Relationship relationship : statementResult.list(rec -> rec.get(0).asRelationship())) {
      System.out.println(relationship.type());
    }
    
//    INVOKES
//    REFERENCES
//    READ
//    USES
    
    
//    for (Node node : statementResult.list(rec -> rec.get(0).asNode())) {
//      System.out.println(node.asMap());
//    }
//    
    //
//    assertThat(nodes).hasSize(1);
//    assertThat(nodes.get(0).labels()).containsExactly("Field");
//    assertThat(nodes.get(0).asMap()).containsOnlyKeys("accessFlags", "deprecated", "final", "fqn", "name", "static",
//        "transient", "visibility", "volatile");
//    assertThat(nodes.get(0).asMap()).containsEntry("accessFlags", "2");
//    assertThat(nodes.get(0).asMap()).containsEntry("deprecated", false);
//    assertThat(nodes.get(0).asMap()).containsEntry("final", false);
//    assertThat(nodes.get(0).asMap()).containsEntry("fqn", "java.io.Serializable _serializable");
//    assertThat(nodes.get(0).asMap()).containsEntry("name", "_serializable");
  }

  //
  // @Test
  // public void testArrayField() {
  //
  // Node node = getTypeNode(ExampleClassWithArrays.class.getName());
  //
  // List<Node> fields = getFields(node, "field_1");
  // assertThat(fields.size(), is(1));
  // assertDataTypeReference(fields.get(0), convert(JTypeModelRelationshipType.IS_OF_TYPE), "int");
  //
  // fields = getFields(node, "field_2");
  // assertThat(fields.size(), is(1));
  // assertDataTypeReference(fields.get(0), convert(JTypeModelRelationshipType.IS_OF_TYPE), "boolean");
  // }
  //
  // @Test
  // public void testArrayMethod() {
  //
  // Node node = getTypeNode(ExampleClassWithArrays.class.getName());
  //
  // List<Node> methods = getMethods(node, "test");
  // assertThat(methods.size(), is(1));
  // assertDataTypeReference(methods.get(0), convert(JTypeModelRelationshipType.HAS_PARAMETER), "int");
  // }
  //
  // @Test
  // public void testConstructor() {
  //
  // Node node = getTypeNode(ExampleClass.class.getName());
  // assertThat(getMethods(node, "<init>").size(), is(2));
  //
  // for (Node methodNode : getMethods(node, "<init>")) {
  //
  // System.out.println(
  // " - " + methodNode.getSingleRelationship(convert(JTypeModelRelationshipType.RETURNS), Direction.OUTGOING));
  // }
  // }
}
