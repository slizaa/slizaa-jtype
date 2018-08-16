package org.slizaa.jtype.hierarchicalgraph;

import org.slizaa.hierarchicalgraph.core.model.HGNode;
import org.slizaa.hierarchicalgraph.core.model.spi.INodeComparator;
import org.slizaa.hierarchicalgraph.graphdb.model.GraphDbNodeSource;

public class JType_NodeComparator implements INodeComparator {

  /**
   * {@inheritDoc}
   */
  @Override
  public int category(Object element) {

    //
    if (!(hasGraphDbNodeSource(element))) {
      return 0;
    }

    //
    if (hasLabel(element, "Directory")) {
      if (hasLabel(element, "Package")) {
        return 20;
      } else {
        return 50;
      }
    }

    //
    if (hasLabel(element, "Resource")) {
      return 30;
    }

    //
    if (hasLabel(element, "Type")) {
      return 10;
    } else if (hasLabel(element, "Field")) {
      return 20;
    } else if (hasLabel(element, "Method")) {
      return 30;
    }

    //
    return 1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compare(Object node1, Object node2) {

    //
    if (!(hasGraphDbNodeSource(node1) || hasGraphDbNodeSource(node2))) {
      return 0;
    }

    //
    if (hasLabel(node1, node2, "Field") || hasLabel(node1, node2, "Method") || hasLabel(node1, node2, "Type")) {
      return compareProperties(node1, node2, "name");
    }

    //
    if (hasLabel(node1, node2, "Directory") || hasLabel(node1, node2, "Resource")) {
      return compareProperties(node1, node2, "name");
    }

    //
    if (hasLabel(node1, node2, "Module")) {
      return compareProperties(node1, node2, "name");
    }

    //
    return -1;
  }

  /**
   * <p>
   * </p>
   *
   * @param node
   * @param label
   * @return
   */
  private boolean hasLabel(Object node, String label) {
    return ((GraphDbNodeSource) ((HGNode) node).getNodeSource()).getLabels().contains(label);
  }

  /**
   * <p>
   * </p>
   *
   * @param node1
   * @param node2
   * @param label
   * @return
   */
  private boolean hasLabel(Object node1, Object node2, String label) {
    return hasLabel(node1, label) && hasLabel(node2, label);
  }

  /**
   * <p>
   * </p>
   *
   * @param node
   * @param label
   * @return
   */
  private int compareProperties(Object node1, Object node2, String property) {

    //
    if (!(((GraphDbNodeSource) ((HGNode) node1).getNodeSource()).getProperties().containsKey(property))) {
      return 0;
    }

    //
    if (!(((GraphDbNodeSource) ((HGNode) node2).getNodeSource()).getProperties().containsKey(property))) {
      return 0;
    }

    //
    return ((GraphDbNodeSource) ((HGNode) node1).getNodeSource()).getProperties().get(property)
        .compareTo(((GraphDbNodeSource) ((HGNode) node2).getNodeSource()).getProperties().get(property));
  }

  /**
   * <p>
   * </p>
   *
   * @param object
   * @return
   */
  private boolean hasGraphDbNodeSource(Object object) {
    return object instanceof HGNode && ((HGNode) object).getNodeSource() instanceof GraphDbNodeSource;
  }
}
