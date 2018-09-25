package org.slizaa.jtype.scanner.jdeps;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slizaa.scanner.testfwk.ContentDefinitionProviderFactory.simpleBinaryFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.neo4j.driver.v1.StatementResult;
import org.slizaa.core.boltclient.testfwk.BoltClientConnectionRule;
import org.slizaa.jtype.scanner.JTypeSlizaaTestServerRule;
import org.slizaa.jtype.scanner.jdeps.internal.JavapWrapper;

@Ignore
@RunWith(Parameterized.class)
public class JdepsTest {

  @Rule
  public JTypeSlizaaTestServerRule _slizaaTestServerRule = new JTypeSlizaaTestServerRule(
      simpleBinaryFile("dummy", "dummy", getJarFile()));

  @Rule
  public BoltClientConnectionRule  _client               = new BoltClientConnectionRule();

  @Rule
  public JDepsRule                 _jDepsRule            = new JDepsRule(() -> getJarFile());

  /** - */
  private File                     _jarFile;

  /**
   * <p>
   * Creates a new instance of type {@link JdepsTest}.
   * </p>
   *
   * @param jarFile
   */
  public JdepsTest(File jarFile) {
    this._jarFile = checkNotNull(jarFile);
  }

  /**
   * <p>
   * </p>
   *
   * @return
   */
  public File getJarFile() {
    return this._jarFile;
  }

  /**
   * <p>
   * </p>
   */
  @Test
  public void test() {
    System.out.println("Testing " + this._jarFile.getAbsolutePath());
    this._jDepsRule.getJdepAnalysis().keySet().stream().sorted().forEach(fqn -> assertSameReferences(fqn));
    System.out.println("Done " + this._jarFile.getAbsolutePath());
  }

  /**
   * <p>
   * </p>
   *
   * @param fqn
   */
  private void assertSameReferences(String fqn) {

    //
    StatementResult statementResult = this._client.getBoltClient().syncExecCypherQuery(
        "Match (t:TYPE {fqn: $name})-[:DEPENDS_ON]->(tr:TYPE_REFERENCE) return tr.fqn",
        Collections.singletonMap("name", fqn));

    //
    List<String> referencesDetectedBySlizaa = statementResult.list(record -> record.get(0).asString());

    //
    try {
      assertThat(referencesDetectedBySlizaa)
          .containsExactlyInAnyOrder(this._jDepsRule.getJdepAnalysis().get(fqn).toArray(new String[0]));
    } catch (AssertionError e) {
      JavapWrapper.doIt(this._jarFile.getAbsolutePath(), fqn);
      throw e;
    }
  }

  @Parameters
  public static Collection<Object[]> data() {

    // the result
    Collection<Object[]> result = new ArrayList<>();

    String dir = "D:\\50-Development\\environments\\schrott\\slizaa-master\\ws\\TestReferenceProject\\libs";

    try {
      Files.newDirectoryStream(Paths.get(dir), "*.jar").forEach(p -> result.add(new Object[] { p.toFile() }));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // return result
    return result;
  }
}
