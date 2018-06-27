/*******************************************************************************
 * Copyright (c) 2011-2015 Slizaa project team. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Slizaa project team - initial API and implementation
 ******************************************************************************/
package org.slizaa.jtype.scanner.itest;

import java.io.File;
import java.util.Collections;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.StatementResult;
import org.slizaa.core.boltclient.testfwk.BoltClientConnectionRule;
import org.slizaa.jtype.scanner.JTypeSlizaaTestServerRule;
import org.slizaa.scanner.core.contentdefinition.FileBasedContentDefinitionProvider;
import org.slizaa.scanner.core.spi.contentdefinition.AnalyzeMode;
import org.slizaa.scanner.core.spi.contentdefinition.IContentDefinitionProvider;

/**
 */
public class SimpleJTypeJDKTest {

  @ClassRule
  public static JTypeSlizaaTestServerRule _server = new JTypeSlizaaTestServerRule(getSystemDefinition());

  @Rule
  public BoltClientConnectionRule         _client = new BoltClientConnectionRule();

  /**
   * <p>
   * </p>
   */
  @Test
  @Ignore
  public void test() {

    //
    StatementResult statementResult = this._client.getBoltClient()
        .syncExecCypherQuery("Match (t:TYPE) return count(t)");
    System.out.println(statementResult.single().get(0).asInt());

    //
    this._client.getBoltClient().syncExecCypherQuery("CALL slizaa.exportDatabase({fileName})",
        Collections.singletonMap("fileName", "C:\\tmp\\exportDatabase.txt")).summary();
  }

  /**
   * <p>
   * </p>
   *
   * @return
   */
  private static IContentDefinitionProvider getSystemDefinition() {

    // create property string
    String version = System.getProperty("java.version");
    String classpath = System.getProperty("sun.boot.class.path");

    //
    FileBasedContentDefinitionProvider provider = new FileBasedContentDefinitionProvider();

    //
    for (String path : classpath.split(File.pathSeparator)) {

      if (new File(path).exists()) {

        // name
        String name = path.substring(path.lastIndexOf(File.separator) + 1);
        int indexOfDot = name.lastIndexOf('.');
        if (indexOfDot != -1) {
          name = name.substring(0, indexOfDot);
        }

        // add the JARs
        provider.createFileBasedContentDefinition("jdk-" + name, version, new File[] {}, null,
            AnalyzeMode.BINARIES_ONLY);
      }
    }

    //
    return provider;
  }
}
