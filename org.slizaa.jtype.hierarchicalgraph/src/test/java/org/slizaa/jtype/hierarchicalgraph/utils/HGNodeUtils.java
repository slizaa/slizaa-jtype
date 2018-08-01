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

  public static void dumpChildren(HGNode hgNode) {
    for (HGNode node : checkNotNull(hgNode).getChildren()) {
      System.out.println(node.getIdentifier() + " : " + HGNodeUtils.getProperties(node));
    }
  }

}
