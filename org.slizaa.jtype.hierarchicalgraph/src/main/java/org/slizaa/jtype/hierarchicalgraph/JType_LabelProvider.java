package org.slizaa.jtype.hierarchicalgraph;

import java.util.function.Function;

import org.slizaa.hierarchicalgraph.core.model.HGNode;
import org.slizaa.hierarchicalgraph.graphdb.mapping.spi.ILabelDefinitionProvider;
import org.slizaa.hierarchicalgraph.graphdb.mapping.spi.labelprovider.AbstractLabelDefinitionProvider;
import org.slizaa.hierarchicalgraph.graphdb.mapping.spi.labelprovider.dsl.ILabelDefinitionProcessor;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class JType_LabelProvider extends AbstractLabelDefinitionProvider implements ILabelDefinitionProvider {

  /** - */
  private boolean                     _showFullyQualifiedName;

  /** - */
  private final MethodSignatureParser _methodSignatureParser;

  /**
   * <p>
   * </p>
   *
   * @param showFullyQualifiedName
   */
  public JType_LabelProvider(boolean showFullyQualifiedName) {
    this._showFullyQualifiedName = showFullyQualifiedName;

    //
    this._methodSignatureParser = new MethodSignatureParser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ILabelDefinitionProcessor createLabelDefinitionProcessor() {

    // @formatter:off
		return exclusiveChoice().

        // Group
        when(nodeHasLabel("Group")).then(handleGroup()).

		    // Module
				when(nodeHasLabel("Module")).then(handleModule()).

				// Package
				when(nodeHasLabel("Directory")).then(handleDirectory()).

				// Resource
				when(nodeHasLabel("Resource")).then(handleResource()).

				// Type
				when(nodeHasLabel("Type")).then(handleType()).

        // Method
        when(nodeHasLabel("Method")).then(handleMethod()).

        // Field
        when(nodeHasLabel("Field")).then(handleField()).

				// all other nodes
				otherwise(setBaseImage("icons/jar_obj.png").and(setLabelText(propertyValue("name"))));

		// @formatter:on
  }

  private ILabelDefinitionProcessor handleGroup() {
    return setBaseImage("icons/fldr_obj.png").and(setLabelText(propertyValue("name")));
  }

  /**
   * <p>
   * </p>
   *
   * @return
   */
  protected ILabelDefinitionProcessor handleModule() {
    return setBaseImage("icons/jar_obj.png").and(setLabelText(propertyValue("name")));
  }

  /**
   * <p>
   * </p>
   *
   * @return
   */
  protected ILabelDefinitionProcessor handleDirectory() {

    // @formatter:off
		return exclusiveChoice().

		// Packages
		when(nodeHasLabel("Package")).then(setBaseImage("icons/package_obj.png")
				.and(setLabelText(propertyValue(this._showFullyQualifiedName ? "fqn" : "name", str -> str.replace('/', '.'))))).

		// Directories
		otherwise(setBaseImage("icons/fldr_obj.png").and(setLabelText(propertyValue(this._showFullyQualifiedName ? "fqn" : "name"))));
		// @formatter:on
  }

  private ILabelDefinitionProcessor handleResource() {

    // @formatter:off
		return executeAll(

				exclusiveChoice().when(nodeHasLabel("ClassFile"))
						.then(setBaseImage("icons/classf_obj.png"))
						.otherwise(setBaseImage("icons/file_obj.png")),

				setLabelText(propertyValue("name")));
		// @formatter:on
  }

  /**
   * <p>
   * </p>
   *
   * @return
   */
  protected ILabelDefinitionProcessor handleType() {

    // @formatter:off
		return executeAll(

				setLabelText(propertyValue("name")),

				when(nodeHasProperty("final"))
						.then(setOverlayImage("icons/class_obj.png", OverlayPosition.TOP_RIGHT)),

				when(nodeHasLabel("Class")).then(setBaseImage("icons/class_obj.png")),

				when(nodeHasLabel("Annotation")).then(setBaseImage("icons/annotation_obj.png")),

				when(nodeHasLabel("Enum")).then(setBaseImage("icons/enum_obj.png")),

				when(nodeHasLabel("Interface")).then(setBaseImage("icons/int_obj.png")));
		// @formatter:on
  }

  /**
   * <p>
   * </p>
   *
   * @return
   */
  protected ILabelDefinitionProcessor handleMethod() {

    // @formatter:off
    return executeAll(

        setLabelText(convertMethodSignature(propertyValue("fqn"))),

        when(nodeHasPropertyWithValue("visibility", "public")).then(setBaseImage("icons/methpub_obj.png")),

        when(nodeHasPropertyWithValue("visibility", "private")).then(setBaseImage("icons/methpri_obj.png")),

        when(nodeHasPropertyWithValue("visibility", "protected")).then(setBaseImage("icons/methpri_obj.png")),

        when(nodeHasPropertyWithValue("visibility", "default")).then(setBaseImage("icons/methdef_obj.png")));
    // @formatter:on
  }

  /**
   * <p>
   * </p>
   *
   * @return
   */
  protected ILabelDefinitionProcessor handleField() {

    // @formatter:off
    return executeAll(

        setLabelText(propertyValue("fqn")),

        when(nodeHasPropertyWithValue("visibility", "public")).then(setBaseImage("icons/field_public_obj.png")),

        when(nodeHasPropertyWithValue("visibility", "private")).then(setBaseImage("icons/field_private_obj.png")),

        when(nodeHasPropertyWithValue("visibility", "protected")).then(setBaseImage("icons/field_protected_obj.png")),

        when(nodeHasPropertyWithValue("visibility", "default")).then(setBaseImage("icons/field_default_obj.png")));
    // @formatter:on
  }

  /**
   * <p>
   * </p>
   *
   * @param function
   * @return
   */
  protected Function<HGNode, String> convertMethodSignature(Function<HGNode, String> function) {
    return (node) -> this._methodSignatureParser.parse(function.apply(node));
  }
}
