package org.slizaa.scanner.jtype.itest;

import java.io.File;
import java.io.IOException;

import org.ops4j.pax.url.mvn.MavenResolvers;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class Main {

  /**
   * <p>
   * </p>
   *
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
    
    //
    File resolvedFile = MavenResolvers.createMavenResolver(null, null).resolve("org.mapstruct",
        "mapstruct", null, "jar", "1.2.0.Final");
    
    System.out.println(resolvedFile);
  }
}
