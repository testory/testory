package org.testory;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.onReturn;
import static org.testory.proxy.Invocation.invocation;
import static org.testory.test.Testilities.newObject;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class Describe_captors {
  private Mockable mock, otherMock;
  private Captor captor;
  private Object argument, otherArgument;
  private Method method, otherMethod, otherTypeMethod;
  private List<Object> arguments, otherArguments;
  private Class<?> type;

  @Before
  public void before() throws NoSuchMethodException {
    type = Returnable.class;
    mock = mock(Mockable.class);
    otherMock = mock(Mockable.class);
    argument = newObject("argument");
    otherArgument = newObject("otherArgument");
    arguments = asList(argument);
    otherArguments = asList(otherArgument);
    method = Mockable.class.getMethod("returnReturnable", Object.class);
    otherMethod = Mockable.class.getMethod("returnOtherReturnable", Object.class);
    otherTypeMethod = Mockable.class.getMethod("returnObject", Object.class);
  }

  @Test
  public void on_instance_matches_invocation_on_same_instance() {
    captor = onInstance(mock);
    assertTrue(captor.matches(invocation(method, mock, arguments)));
    assertTrue(captor.matches(invocation(otherMethod, mock, arguments)));
    assertFalse(captor.matches(invocation(method, otherMock, arguments)));
    assertTrue(captor.matches(invocation(method, mock, otherArguments)));
  }

  @Test
  public void on_return_matches_invocation_of_method_returning_same_type() {
    captor = onReturn(type);
    assertTrue(captor.matches(invocation(method, mock, arguments)));
    assertTrue(captor.matches(invocation(otherMethod, mock, arguments)));
    assertFalse(captor.matches(invocation(otherTypeMethod, mock, arguments)));
    assertTrue(captor.matches(invocation(method, otherMock, arguments)));
    assertTrue(captor.matches(invocation(method, mock, otherArguments)));
  }

  @Test
  public void captors_are_printable() {
    assertEquals("onInstance(" + mock + ")", onInstance(mock).toString());
    assertEquals("onReturn(" + type.getName() + ")", onReturn(type).toString());
  }

  @Test
  public void captors_checks_arguments() {
    try {
      onInstance(null);
      fail();
    } catch (TestoryException e) {}
    try {
      onReturn(null);
      fail();
    } catch (TestoryException e) {}
  }

  private static class Returnable {}

  @SuppressWarnings("unused")
  private static class Mockable {
    public Returnable returnReturnable(Object o) {
      return null;
    }

    public Returnable returnOtherReturnable(Object o) {
      return null;
    }

    public Object returnObject(Object o) {
      return null;
    }
  }
}
