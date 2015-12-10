package org.testory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.testing.Closures.returning;
import static org.testory.testing.Closures.throwing;
import static org.testory.testing.Fakes.newObject;
import static org.testory.testing.Fakes.newThrowable;

import org.junit.Before;
import org.junit.Test;

public class test_asserting_thrown {
  private Throwable throwable;
  private Object object;

  @Before
  public void before() {
    throwable = newThrowable("throwable");
    object = newObject("object");
  }

  @Test
  public void asserts_throwing() {
    when(throwing(throwable));
    thenThrown();
  }

  @Test
  public void fails_returning_object() {
    when(returning(object));
    try {
      thenThrown();
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_returning_void() {
    // TODO replace by VoidClosure
    when(new Runnable() {
      public void run() {}
    }).run();
    try {
      thenThrown();
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void failure_prints_expectation() {
    when(returning(object));
    try {
      thenThrown();
      fail();
    } catch (TestoryAssertionError e) {
      assertTrue(e.getMessage(), e.getMessage().contains(""
          + "  expected thrown\n"
          + "    \n"));
    }
  }
}
