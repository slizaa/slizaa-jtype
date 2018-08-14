package org.slizaa.jtype.hierarchicalgraph.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slizaa.hierarchicalgraph.core.model.HGNode;
import org.slizaa.hierarchicalgraph.core.model.INodeSource;
import org.slizaa.hierarchicalgraph.graphdb.model.GraphDbNodeSource;

public class HGNodeUtils {

  /**
   * <p>
   * </p>
   *
   * @param node
   * @return
   */
  public static Map<String, String> getProperties(HGNode node) {

    //
    INodeSource nodeSource = node.getNodeSource();

    //
    if (nodeSource instanceof GraphDbNodeSource) {
      return ((GraphDbNodeSource) nodeSource).getProperties().map();
    }

    //
    else {
      return Collections.emptyMap();
    }
  }

  /**
   * <p>
   * </p>
   *
   * @param node
   * @return
   */
  public static List<String> getLabels(HGNode node) {

    //
    Map<String, String> props = getProperties(node);

    //
    if (props.containsKey("labels")) {
      String arrayString = props.get("labels");
      arrayString = arrayString.substring(1, arrayString.length() - 1);
      return Arrays.asList(arrayString.split("\\s*,\\s*"));
    }

    //
    return Collections.emptyList();
  }

  /**
   * <p>
   * </p>
   *
   * @param hgNode
   */
  public static void dumpNode(HGNode hgNode) {
    dumpNode(hgNode, 0);
  }

  /**
   * <p>
   * </p>
   *
   * @param hgNode
   * @param indent
   */
  private static void dumpNode(final HGNode node, final int indent) {

    //
    String indentSpace = new String(new char[indent]).replace('\0', ' ');

    //
    System.out.println(indentSpace + node.getIdentifier() + " : " + HGNodeUtils.getProperties(node).get("fqn"));

    //
    for (HGNode childNode : checkNotNull(node).getChildren()) {

      //
      dumpNode(childNode, indent + 1);
    }
  }
}
