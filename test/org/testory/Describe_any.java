package org.testory;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.test.Testilities.newObject;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Describe_any {
  private Foo mock, otherMock;
  private Object object, otherObject;

  public interface Foo {
    boolean foo(Object a);

    boolean bar(Object a);

    boolean foo(Object a, Object b);

    boolean foo(int a);

    boolean foo(int a, Object b, int c);
  }

  @Before
  public void before() {
    purge();

    mock = mock(Foo.class);
    otherMock = mock(Foo.class);
    object = newObject("object");
    otherObject = newObject("otherObject");
  }

  @After
  public void after() {
    purge();
  }

  private void purge() {
    when("");
    when("");
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void compiles_with_various_types() {
    class Eater<E> {
      void eat(E food) {}

      void eatBool(boolean food) {}

      void eatChar(char food) {}

      void eatByte(byte food) {}

      void eatShort(short food) {}

      void eatInt(int food) {}

      void eatLong(long food) {}

      void eatFloat(float food) {}

      void eatDouble(double food) {}
    }
    new Eater<Object>().eat(any(Object.class));
    new Eater<String>().eat(any(String.class));
    new Eater<Runnable>().eat(any(Runnable.class));
    new Eater<List>().eat(any(List.class));
    new Eater<List<?>>().eat(any(List.class));
    new Eater<List<Object>>().eat(any(List.class));
    new Eater<List<String>>().eat(any(List.class));

    new Eater<Void>().eat(any(Void.class));
    new Eater<Boolean>().eat(any(Boolean.class));
    new Eater<Character>().eat(any(Character.class));
    new Eater<Byte>().eat(any(Byte.class));
    new Eater<Short>().eat(any(Short.class));
    new Eater<Integer>().eat(any(Integer.class));
    new Eater<Long>().eat(any(Long.class));
    new Eater<Float>().eat(any(Float.class));
    new Eater<Double>().eat(any(Double.class));

    new Eater().eatBool(any(Boolean.class));
    new Eater().eatChar(any(Character.class));
    new Eater().eatByte(any(Byte.class));
    new Eater().eatShort(any(Short.class));
    new Eater().eatInt(any(Integer.class));
    new Eater().eatLong(any(Long.class));
    new Eater().eatFloat(any(Float.class));
    new Eater().eatDouble(any(Double.class));
  }

  @Test
  public void matches_any_object() {
    given(willReturn(true), mock).foo(any(Object.class));
    assertTrue(mock.foo(object));
    thenCalled(mock).foo(any(Object.class));
  }

  @Test
  public void matches_any_object_accepts_null() {
    given(willReturn(true), mock).foo(any(Object.class));
    assertTrue(mock.foo(null));
    thenCalled(mock).foo(any(Object.class));
  }

  @Test
  public void matches_any_int() {
    given(willReturn(true), mock).foo((int) any(Integer.class));
    assertTrue(mock.foo(5));
    thenCalled(mock).foo((int) any(Integer.class));
  }

  @Test
  public void matches_any_two_objects() {
    given(willReturn(true), mock).foo(any(Object.class), any(Object.class));
    assertTrue(mock.foo(object, otherObject));
    thenCalled(mock).foo(any(Object.class), any(Object.class));
  }

  @Test
  public void matches_any_type() {
    given(willReturn(true), mock).foo(any(Foo.class));
    assertTrue(mock.foo(object));
    thenCalled(mock).foo(any(Foo.class));
  }

  @Test
  public void matches_matching_argument() {
    given(willReturn(true), mock).foo(any(Object.class, same(object)));
    assertTrue(mock.foo(object));
    thenCalled(mock).foo(any(Object.class, same(object)));
  }

  @Test
  public void not_matches_mismatching_argument() {
    given(willReturn(true), mock).foo(any(Object.class, same(object)));
    assertFalse(mock.foo(otherObject));

    try {
      thenCalled(mock).foo(any(Object.class, same(object)));
      fail();
    } catch (TestoryAssertionError e) {
      thenEqual(format("\n" //
          + "  expected called times 1\n" //
          + "    %s.foo(any(%s, %s))\n", //
          mock, Object.class.getName(), same(object)), //
          e.getMessage());
    }
  }

  @Test
  public void not_matches_other_mock() {
    given(willReturn(true), mock).foo(any(Object.class));
    assertFalse(otherMock.foo(object));
    try {
      thenCalled(mock).foo(any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(format("\n" //
          + "  expected called times 1\n" //
          + "    %s.foo(any(%s))\n", //
          mock, Object.class.getName()), //
          e.getMessage());
    }
  }

  @Test
  public void not_matches_other_method() {
    given(willReturn(true), mock).foo(any(Object.class));
    assertFalse(mock.bar(object));
    try {
      thenCalled(mock).foo(any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(format("\n" //
          + "  expected called times 1\n" //
          + "    %s.foo(any(%s))\n", //
          mock, Object.class.getName()), //
          e.getMessage());
    }
  }

  @Test
  public void solves_mixing_proxiable_types() {
    mock = mock(Foo.class);
    given(willReturn(true), mock).foo(object, any(Object.class));
    assertTrue(mock.foo(object, object));
    thenCalled(mock).foo(object, any(Object.class));

    mock = mock(Foo.class);
    given(willReturn(true), mock).foo(object, any(Object.class));
    assertTrue(mock.foo(object, otherObject));
    thenCalled(mock).foo(object, any(Object.class));

    mock = mock(Foo.class);
    given(willReturn(true), mock).foo(object, any(Object.class));
    assertTrue(mock.foo(object, null));
    thenCalled(mock).foo(object, any(Object.class));

    mock = mock(Foo.class);
    given(willReturn(true), mock).foo(object, any(Object.class));
    assertFalse(mock.foo(otherObject, object));
    try {
      thenCalled(mock).foo(object, any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(format("\n" //
          + "  expected called times 1\n" //
          + "    %s.foo(%s, any(%s))\n", //
          mock, object, Object.class.getName()), //
          e.getMessage());
    }

    mock = mock(Foo.class);
    given(willReturn(true), mock).foo(object, any(Object.class));
    assertFalse(mock.foo(null, object));
    try {
      thenCalled(mock).foo(object, any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(format("\n" //
          + "  expected called times 1\n" //
          + "    %s.foo(%s, any(%s))\n", //
          mock, object, Object.class.getName()), //
          e.getMessage());
    }

    mock = mock(Foo.class);
    given(willReturn(true), mock).foo(object, any(Object.class));
    assertFalse(mock.foo(null, null));
    try {
      thenCalled(mock).foo(object, any(Object.class));
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(format("\n" //
          + "  expected called times 1\n" //
          + "    %s.foo(%s, any(%s))\n", //
          mock, object, Object.class.getName()), //
          e.getMessage());
    }
  }

  @Test
  public void solves_mixing_with_separator() {
    mock = mock(Foo.class);
    given(willReturn(true), mock).foo(any(Integer.class), any(Object.class), 0);
    assertTrue(mock.foo(-1, object, 0));
    thenCalled(mock).foo(any(Integer.class), any(Object.class), 0);

    mock = mock(Foo.class);
    given(willReturn(true), mock).foo(any(Integer.class), any(Object.class), 0);
    assertFalse(mock.foo(0, object, -1));
    try {
      thenCalled(mock).foo(any(Integer.class), any(Object.class), 0);
      fail();
    } catch (TestoryAssertionError e) {
      assertEquals(format("\n" //
          + "  expected called times 1\n" //
          + "    %s.foo(any(%s), any(%s), %s)\n", //
          mock, Integer.class.getName(), Object.class.getName(), 0), //
          e.getMessage());
    }
  }

  @Test
  public void not_solves_more_anys_than_parameters() {
    try {
      any(Object.class);
      given(willReturn(true), mock).foo(any(Object.class));
      fail();
    } catch (TestoryException e) {}

    try {
      any(Object.class);
      thenCalled(mock).foo(any(Object.class));
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void recovers_after_misuse() {
    try {
      any(Object.class);
      given(willReturn(true), mock).foo(any(Object.class));
      fail();
    } catch (TestoryException e) {}

    given(willReturn(true), mock).foo(any(Object.class));
    assertTrue(mock.foo(object));
    thenCalled(mock).foo(any(Object.class));
  }

  @Test
  public void type_cannot_be_null() {
    try {
      any(null);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void type_cannot_be_primitive() {
    try {
      any(int.class);
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void matcher_cannot_be_any_object() {
    try {
      any(Object.class, new Object());
      fail();
    } catch (TestoryException e) {}
  }

  @Test
  public void matcher_cannot_be_null() {
    try {
      any(Object.class, null);
      fail();
    } catch (TestoryException e) {}
  }

  private static Object same(final Object object) {
    return new Object() {
      @SuppressWarnings("unused")
      public boolean matches(Object item) {
        return object == item;
      }

      public String toString() {
        return "same(" + object + ")";
      }
    };
  }
}
