/*******************************************************************************
 * Copyright (c) 2011-2015 Slizaa project team. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Slizaa project team - initial API and implementation
 ******************************************************************************/
package org.slizaa.scanner.jtype.itest;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slizaa.scanner.neo4j.testfwk.ContentDefinitionsUtils.simpleBinaryFile;

import java.util.Collections;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.slizaa.scanner.jtype.itest.examplecode.AbstractExampleClass;
import org.slizaa.scanner.jtype.itest.examplecode.ExampleAnnotation;
import org.slizaa.scanner.jtype.itest.examplecode.ExampleClass;
import org.slizaa.scanner.jtype.itest.examplecode.ExampleEnum;
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
  public void test_Interface() {

    // get the type node
    Node node = getTypeNode(ExampleInterface.class);

    // asserts
    assertThat(node.labels()).containsExactly("Type", "Interface");
    assertThat(node.asMap()).containsOnlyKeys(ITypeNode.CLASS_VERSION, ITypeNode.ABSTRACT, ITypeNode.SOURCE_FILE_NAME,
        ITypeNode.DEPRECATED, ITypeNode.FINAL, ITypeNode.FQN, ITypeNode.NAME, ITypeNode.STATIC, ITypeNode.VISIBILITY);

    assertThat(node.asMap()).containsEntry(ITypeNode.FQN, ExampleInterface.class.getName());
    assertThat(node.asMap()).containsEntry(ITypeNode.NAME, ExampleInterface.class.getSimpleName());
    assertThat(node.asMap()).containsEntry(ITypeNode.VISIBILITY, "public");
    assertThat(node.asMap()).containsEntry(ITypeNode.SOURCE_FILE_NAME, "ExampleInterface.java");
    assertThat(node.asMap()).containsEntry(ITypeNode.ABSTRACT, true);
  }

  /**
   * <p>
   * </p>
   */
  @Test
  public void test_Class() {

    // get the type node
    Node node = getTypeNode(ExampleClass.class);

    // asserts
    assertThat(node.labels()).containsExactly("Type", "Class");
    assertThat(node.asMap()).containsOnlyKeys(ITypeNode.CLASS_VERSION, ITypeNode.ABSTRACT, ITypeNode.SOURCE_FILE_NAME,
        ITypeNode.DEPRECATED, ITypeNode.FINAL, ITypeNode.FQN, ITypeNode.NAME, ITypeNode.STATIC, ITypeNode.VISIBILITY);

    assertThat(node.asMap()).containsEntry(ITypeNode.FQN, ExampleClass.class.getName());
    assertThat(node.asMap()).containsEntry(ITypeNode.NAME, ExampleClass.class.getSimpleName());
    assertThat(node.asMap()).containsEntry(ITypeNode.VISIBILITY, "public");
    assertThat(node.asMap()).containsEntry(ITypeNode.SOURCE_FILE_NAME, "ExampleClass.java");
    assertThat(node.asMap()).containsEntry(ITypeNode.ABSTRACT, false);
  }

  /**
   * <p>
   * </p>
   */
  @Test
  public void test_Enum() {

    // get the type node
    Node node = getTypeNode(ExampleEnum.class);

    // asserts
    assertThat(node.labels()).containsExactly("Type", "Enum");
    assertThat(node.asMap()).containsOnlyKeys(ITypeNode.CLASS_VERSION, ITypeNode.ABSTRACT, ITypeNode.SOURCE_FILE_NAME,
        ITypeNode.DEPRECATED, ITypeNode.FINAL, ITypeNode.FQN, ITypeNode.NAME, ITypeNode.STATIC, ITypeNode.VISIBILITY,
        ITypeNode.SIGNATURE);

    assertThat(node.asMap()).containsEntry(ITypeNode.FQN, ExampleEnum.class.getName());
    assertThat(node.asMap()).containsEntry(ITypeNode.NAME, ExampleEnum.class.getSimpleName());
    assertThat(node.asMap()).containsEntry(ITypeNode.VISIBILITY, "public");
    assertThat(node.asMap()).containsEntry(ITypeNode.SOURCE_FILE_NAME, "ExampleEnum.java");
    assertThat(node.asMap()).containsEntry(ITypeNode.ABSTRACT, false);
  }

  /**
   * <p>
   * </p>
   */
  @Test
  public void test_Annotation() {

    // get the type node
    Node node = getTypeNode(ExampleAnnotation.class);

    // asserts
    assertThat(node.labels()).containsExactly("Type", "Annotation");
    assertThat(node.asMap()).containsOnlyKeys(ITypeNode.CLASS_VERSION, ITypeNode.ABSTRACT, ITypeNode.SOURCE_FILE_NAME,
        ITypeNode.DEPRECATED, ITypeNode.FINAL, ITypeNode.FQN, ITypeNode.NAME, ITypeNode.STATIC, ITypeNode.VISIBILITY);

    assertThat(node.asMap()).containsEntry(ITypeNode.FQN, ExampleAnnotation.class.getName());
    assertThat(node.asMap()).containsEntry(ITypeNode.NAME, ExampleAnnotation.class.getSimpleName());
    assertThat(node.asMap()).containsEntry(ITypeNode.VISIBILITY, "public");
    assertThat(node.asMap()).containsEntry(ITypeNode.SOURCE_FILE_NAME, "ExampleAnnotation.java");
    assertThat(node.asMap()).containsEntry(ITypeNode.ABSTRACT, true);
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

    StatementResult statementResult = this._client.getSession().run(
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

    StatementResult statementResult = this._client.getSession().run(
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

    StatementResult statementResult = this._client.getSession().run(
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

    StatementResult statementResult = this._client.getSession().run("Match (t:Type {fqn: $name }) return t.abstract",
        Collections.singletonMap("name", AbstractExampleClass.class.getName()));

    assertThat(statementResult.single().get(0).asBoolean()).isTrue();
  }

  /**
   * <p>
   * </p>
   */
  @Test
  public void testAbstract_2() {

    StatementResult statementResult = this._client.getSession().run("Match (t:Type {fqn: $name }) return t.abstract",
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
    StatementResult statementResult = this._client.getSession().run(
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
    List<Node> nodes = getFieldNodes(SimpleClassWithOneField.class);

    //
    assertThat(nodes).hasSize(1);
    assertThat(nodes.get(0).labels()).containsExactly("Field");

    assertThat(nodes.get(0).asMap()).containsOnlyKeys("accessFlags", "deprecated", "final", "fqn", "name", "static",
        "transient", "visibility", "volatile");
    assertThat(nodes.get(0).asMap()).containsEntry("accessFlags", "4");
    assertThat(nodes.get(0).asMap()).containsEntry("deprecated", false);
    assertThat(nodes.get(0).asMap()).containsEntry("final", false);
    assertThat(nodes.get(0).asMap()).containsEntry("fqn",
        "java.io.Serializable org.slizaa.scanner.jtype.itest.examplecode.SimpleClassWithOneField._serializable");
    assertThat(nodes.get(0).asMap()).containsEntry("name", "_serializable");
  }

  /**
   * <p>
   * </p>
   */
  @Test
  public void testField_2() {

    //
    List<Node> nodes = getFieldNodes(ExampleClass.class);

    //
    assertThat(nodes).hasSize(1);
    assertThat(nodes.get(0).labels()).containsExactly("Field");
    assertThat(nodes.get(0).asMap()).containsOnlyKeys("accessFlags", "deprecated", "final", "fqn", "name", "static",
        "transient", "visibility", "volatile");
    assertThat(nodes.get(0).asMap()).containsEntry("accessFlags", "2");
    assertThat(nodes.get(0).asMap()).containsEntry("deprecated", false);
    assertThat(nodes.get(0).asMap()).containsEntry("final", false);
    assertThat(nodes.get(0).asMap()).containsEntry("fqn",
        "java.io.Serializable org.slizaa.scanner.jtype.itest.examplecode.ExampleClass._serializable");
    assertThat(nodes.get(0).asMap()).containsEntry("name", "_serializable");
  }

  @Test
  public void testMethodReference_1() {

    //
    StatementResult statementResult = this._client.getSession()
        .run("MATCH (m:Method {name: 'exampleInvokesMethod'})-[:INVOKES]->(mref:MethodReference) return mref");

    List<Node> nodes = statementResult.list(rec -> rec.get(0).asNode());

    //
    for (Node node : nodes) {
      System.out.println(node.asMap());
    }

    // assertThat(nodes).hasSize(1);
    // assertThat(nodes.get(0).labels()).containsExactly("FieldReference");
    // assertThat(nodes.get(0).asMap()).containsOnlyKeys("name", "type", "ownerTypeFqn", "static");
    // assertThat(nodes.get(0).asMap()).containsEntry("name", "_serializable");
    // assertThat(nodes.get(0).asMap()).containsEntry("type", "java.io.Serializable");
    // assertThat(nodes.get(0).asMap()).containsEntry("ownerTypeFqn",
    // "org.slizaa.scanner.jtype.itest.examplecode.SimpleClassWithOneField");
    // assertThat(nodes.get(0).asMap()).containsEntry("static", false);
  }

  /**
   * <p>
   * </p>
   */
  @Test
  public void testFieldReference_1() {

    //
    StatementResult statementResult = this._client.getSession().run(
        "Match (m:Method {name: $name})-[:READS]->(fref:FieldReference) return fref",
        Collections.singletonMap("name", "fieldRef"));

    List<Node> nodes = statementResult.list(rec -> rec.get(0).asNode());

    //
    for (Node node : nodes) {
      System.out.println(node.asMap());
    }

    assertThat(nodes).hasSize(1);
    assertThat(nodes.get(0).labels()).containsExactly("FieldReference");
    assertThat(nodes.get(0).asMap()).containsOnlyKeys("fqn", "name", "type", "ownerTypeFqn", "static");
    assertThat(nodes.get(0).asMap()).containsEntry("name", "_serializable");
    assertThat(nodes.get(0).asMap()).containsEntry("type", "java.io.Serializable");
    assertThat(nodes.get(0).asMap()).containsEntry("ownerTypeFqn",
        "org.slizaa.scanner.jtype.itest.examplecode.SimpleClassWithOneField");
    assertThat(nodes.get(0).asMap()).containsEntry("static", false);
    assertThat(nodes.get(0).asMap()).containsEntry("fqn",
        "java.io.Serializable org.slizaa.scanner.jtype.itest.examplecode.SimpleClassWithOneField._serializable");
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

  /**
   * <p>
   * </p>
   *
   * @param clazz
   * @return
   */
  private Node getTypeNode(Class<?> clazz) {

    //
    StatementResult statementResult = this._client.getSession().run("Match (t:Type {fqn: $name }) return t",
        Collections.singletonMap("name", checkNotNull(clazz).getName()));

    //
    return statementResult.single().get(0).asNode();
  }

  /**
   * <p>
   * </p>
   *
   * @param clazz
   * @return
   */
  private List<Node> getFieldNodes(Class<?> clazz) {

    //
    StatementResult statementResult = this._client.getSession().run(
        "Match (t:Type {fqn: $name})-[:CONTAINS]->(f:Field) return f",
        Collections.singletonMap("name", checkNotNull(clazz).getName()));

    //
    return statementResult.list(rec -> rec.get(0).asNode());
  }
}
