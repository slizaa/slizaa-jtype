package org.slizaa.jtype.itest_osgi;

import javax.inject.Inject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
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

//    //
//    IGraphDb graphDb = _dbFactory.newGraphDb(folder.newFolder()).create();
//
//    //
//    Driver driver = GraphDatabase.driver("bolt://localhost:5001");
//    Session session = driver.session();
//    StatementResult statementResult = session.run("CALL slizaa.jtype.createMissingTypes()");
//    statementResult.forEachRemaining(r -> System.out.println(r));
//    
//    System.out.println("BUMM");
//    
//    driver.close();
//    graphDb.close();
  }
}