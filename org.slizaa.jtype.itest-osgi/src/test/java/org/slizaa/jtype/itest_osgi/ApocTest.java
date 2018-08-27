package org.slizaa.jtype.itest_osgi;

import javax.inject.Inject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.osgi.framework.BundleException;
import org.slizaa.scanner.core.api.graphdb.IGraphDb;
import org.slizaa.scanner.core.api.graphdb.IGraphDbFactory;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 *
 */
public class ApocTest extends AbstractJTypeTest {

  @Rule
  public TemporaryFolder  folder = new TemporaryFolder();

  /** - */
  @Inject
  private IGraphDbFactory _dbFactory;

  /**
   * <p>
   * </p>
   *
   * @throws BundleException
   */
  @Test
  public void testApoc() throws Exception {

    //
    IGraphDb graphDb = this._dbFactory.newGraphDb(this.folder.newFolder()).create();

    graphDb.close();
  }
}