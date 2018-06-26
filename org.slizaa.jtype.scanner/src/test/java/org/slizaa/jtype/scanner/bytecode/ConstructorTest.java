package org.slizaa.jtype.scanner.bytecode;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.slizaa.jtype.scanner.model.JTypeLabel;
import org.slizaa.scanner.core.spi.parser.model.INode;
import org.slizaa.scanner.core.spi.parser.model.resource.CoreModelRelationshipType;

import net.bytebuddy.ByteBuddy;

public class ConstructorTest extends AbstractByteBuddyBytecodeTest {

  @Test
  public void constructorTest() {

    INode node = parse(() -> new ByteBuddy().subclass(Object.class));

    assertThat(node.getRelationships(CoreModelRelationshipType.CONTAINS)).hasSize(1);

    //
    INode fieldNode = node.getRelationships(CoreModelRelationshipType.CONTAINS).get(0).getTargetBean();

    assertThat(fieldNode.getLabels()).containsOnly(JTypeLabel.Method, JTypeLabel.Constructor);
    assertThat(fieldNode.getProperty("name")).isEqualTo("<init>");
  }
}
