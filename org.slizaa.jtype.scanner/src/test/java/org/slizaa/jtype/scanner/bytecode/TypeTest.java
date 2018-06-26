package org.slizaa.jtype.scanner.bytecode;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.slizaa.jtype.scanner.model.JTypeLabel;
import org.slizaa.scanner.core.spi.parser.model.INode;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;

public class TypeTest extends AbstractByteBuddyBytecodeTest {

  @Test
  public void publicClass() {

    INode node = parse(() -> new ByteBuddy().subclass(Object.class).modifiers(Visibility.PUBLIC));

    assertThat(node.getProperties()).hasSize(8);
    assertThat(node.getProperty("name")).isEqualTo("Type");
    assertThat(node.getProperty("fqn")).isEqualTo("example.Type");
    assertThat(node.getProperty("classVersion")).isEqualTo("52");
    assertThat(node.getProperty("visibility")).isEqualTo("public");

    assertThat(node.getLabels()).containsExactly(JTypeLabel.Type, JTypeLabel.Class);
  }

  @Test
  public void publicInterface() {

    INode node = parse(() -> new ByteBuddy().makeInterface());

    assertThat(node.getProperties()).hasSize(8);
    assertThat(node.getProperty("abstract")).isEqualTo(true);
    assertThat(node.getProperty("name")).isEqualTo("Type");
    assertThat(node.getProperty("fqn")).isEqualTo("example.Type");
    assertThat(node.getProperty("classVersion")).isEqualTo("52");
    assertThat(node.getProperty("visibility")).isEqualTo("public");

    assertThat(node.getLabels()).containsExactly(JTypeLabel.Type, JTypeLabel.Interface);
  }

  @Test
  public void publicEnum() {

    INode node = parse(() -> new ByteBuddy().makeEnumeration("hurz", "purz"));

    assertThat(node.getProperties()).hasSize(9);
    assertThat(node.getProperty("final")).isEqualTo(true);
    assertThat(node.getProperty("name")).isEqualTo("Type");
    assertThat(node.getProperty("fqn")).isEqualTo("example.Type");
    assertThat(node.getProperty("classVersion")).isEqualTo("52");
    assertThat(node.getProperty("visibility")).isEqualTo("public");
    assertThat(node.getProperty("signature")).isEqualTo("Ljava/lang/Enum<Lexample/Type;>;");

    assertThat(node.getLabels()).containsExactly(JTypeLabel.Type, JTypeLabel.Enum);
  }
  
  @Test
  public void publicAnnotation() {

    INode node = parse(() -> new ByteBuddy().makeAnnotation());

    assertThat(node.getProperties()).hasSize(8);
    assertThat(node.getProperty("abstract")).isEqualTo(true);
    assertThat(node.getProperty("name")).isEqualTo("Type");
    assertThat(node.getProperty("fqn")).isEqualTo("example.Type");
    assertThat(node.getProperty("classVersion")).isEqualTo("52");
    assertThat(node.getProperty("visibility")).isEqualTo("public");

    assertThat(node.getLabels()).containsExactly(JTypeLabel.Type, JTypeLabel.Annotation);
  }
  
  @Test
  public void packagePrivateClass() {

    INode node = parse(() -> new ByteBuddy().subclass(Object.class).modifiers(Visibility.PACKAGE_PRIVATE));

    assertThat(node.getProperties()).hasSize(8);
    assertThat(node.getProperty("name")).isEqualTo("Type");
    assertThat(node.getProperty("fqn")).isEqualTo("example.Type");
    assertThat(node.getProperty("classVersion")).isEqualTo("52");
    assertThat(node.getProperty("visibility")).isEqualTo("default");

    assertThat(node.getLabels()).containsExactly(JTypeLabel.Type, JTypeLabel.Class);
  }
}
