/**
 *
 */
package org.slizaa.jtype.scanner.itest.examplecode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 *
 */
public class FieldInstance {

  public Object testFieldInstance = Collections.addAll(new ArrayList<>(), Semaphore.class);
}
