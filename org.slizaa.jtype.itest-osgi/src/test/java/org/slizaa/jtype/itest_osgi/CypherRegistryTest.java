package org.slizaa.jtype.itest_osgi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleException;
import org.osgi.util.tracker.ServiceTracker;
import org.slizaa.scanner.core.api.cypherregistry.ICypherStatementRegistry;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 *
 */
public class CypherRegistryTest extends AbstractJTypeTest {

  /** - */
  private ICypherStatementRegistry _registry;

  /**
   * <p>
   * </p>
   *
   * @throws Exception
   */
  @Before
  public void init() throws Exception {

    // checkStart
    startAllBundles();

    //
    ServiceTracker<ICypherStatementRegistry, ICypherStatementRegistry> serviceTracker = new ServiceTracker<>(
        bundleContext(), ICypherStatementRegistry.class, null);
    serviceTracker.open();

    //
    this._registry = serviceTracker.getService();
    assertThat(this._registry).isNotNull();
  }

  /**
   * <p>
   * </p>
   *
   * @throws BundleException
   */
  @Test
  public void testCypherStatementRegistry() throws BundleException {
    assertThat(this._registry.getAllStatements()).hasSize(7);
  }
}