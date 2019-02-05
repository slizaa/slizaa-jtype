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
public class JType_LabelProvider extends AbstractLabelDefinitionProvider
		implements ILabelDefinitionProvider, JType_Constants {

	/** - */
	private boolean _showFullyQualifiedName;

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
				otherwise(setBaseImage(ICONS_JAR_OBJ_SVG).and(setLabelText(propertyValue("name"))));

		// @formatter:on
	}

	private ILabelDefinitionProcessor handleGroup() {
		return setBaseImage(ICONS_FLDR_OBJ_SVG).and(setLabelText(propertyValue("name")));
	}

	/**
	 * <p>
	 * </p>
	 *
	 * @return
	 */
	protected ILabelDefinitionProcessor handleModule() {
		return setBaseImage(ICONS_JAR_OBJ_SVG).and(setLabelText(propertyValue("name")));
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
		when(nodeHasLabel("Package")).then(setBaseImage(ICONS_PACKAGE_OBJ_SVG)
				.and(setLabelText(propertyValue(this._showFullyQualifiedName ? "fqn" : "name", str -> str.replace('/', '.'))))).

		// Directories
		otherwise(setBaseImage(ICONS_FLDR_OBJ_SVG).and(setLabelText(propertyValue(this._showFullyQualifiedName ? "fqn" : "name"))));
		// @formatter:on
	}

	private ILabelDefinitionProcessor handleResource() {

	// @formatter:off
		return executeAll(

				exclusiveChoice().when(nodeHasLabel("ClassFile"))
						.then(setBaseImage(ICONS_CLASSF_OBJ_SVG))
						.otherwise(setBaseImage(ICONS_FILE_OBJ_SVG)),

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
				setIsOverlayImage(true),

				when(nodeHasProperty("final"))
						.then(setOverlayImage(ICONS_CLASS_OBJ_SVG, OverlayPosition.TOP_RIGHT)),

				when(nodeHasLabel("Class")).then(setBaseImage(ICONS_CLASS_OBJ_SVG)),

				when(nodeHasLabel("Annotation")).then(setBaseImage(ICONS_ANNOTATION_OBJ_SVG)),

				when(nodeHasLabel("Enum")).then(setBaseImage(ICONS_ENUM_OBJ_SVG)),

				when(nodeHasLabel("Interface")).then(setBaseImage(ICONS_INT_OBJ_SVG)));
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
        setIsOverlayImage(true),

        when(nodeHasPropertyWithValue("visibility", "public")).then(setBaseImage(ICONS_METHPUB_OBJ_SVG)),

        when(nodeHasPropertyWithValue("visibility", "private")).then(setBaseImage(ICONS_METHOD_PRIVATE_OBJ_SVG)),

        when(nodeHasPropertyWithValue("visibility", "protected")).then(setBaseImage(ICONS_METHOD_PROTECTED_OBJ_SVG)),

        when(nodeHasPropertyWithValue("visibility", "default")).then(setBaseImage(ICONS_METHDEF_OBJ_SVG)));
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
        setIsOverlayImage(true),

        when(nodeHasPropertyWithValue("visibility", "public")).then(setBaseImage(ICONS_FIELD_PUBLIC_OBJ_SVG)),

        when(nodeHasPropertyWithValue("visibility", "private")).then(setBaseImage(ICONS_FIELD_PRIVATE_OBJ_SVG)),

        when(nodeHasPropertyWithValue("visibility", "protected")).then(setBaseImage(ICONS_FIELD_PROTECTED_OBJ_SVG)),

        when(nodeHasPropertyWithValue("visibility", "default")).then(setBaseImage(ICONS_FIELD_DEFAULT_OBJ_SVG)));
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
