package org.testory.common;

import static org.junit.Assert.assertEquals;
import static org.testory.common.Formatter.formatter;
import static org.testory.testing.Fakes.newObject;

import org.junit.Before;
import org.junit.Test;

public class TestFormatter {
  private Object a, b, c, d;
  private Formatter formatter;

  @Before
  public void before() {
    formatter = formatter();
    a = newObject("a");
    b = newObject("b");
    c = newObject("c");
    d = newObject("d");
  }

  @Test
  public void formats_object_using_to_string_method() {
    assertEquals(
        a.toString(),
        formatter.format(a));
  }

  @Test
  public void formats_empty_array() {
    assertEquals(
        "[]",
        formatter.format(new Object[] {}));
  }

  @Test
  public void formats_single_object_array() {
    assertEquals(
        "[" + a + "]",
        formatter.format(new Object[] { a }));
  }

  @Test
  public void formats_primitive() {
    assertEquals(
        "[" + 1 + "]",
        formatter.format(new int[] { 1 }));
  }

  @Test
  public void formats_object_array() {
    assertEquals(
        "[" + a + ", " + b + "]",
        formatter.format(new Object[] { a, b }));
  }

  @Test
  public void formats_deep_array() {
    assertEquals(
        "[[" + a + ", " + b + "], [" + c + ", " + d + "]]",
        formatter.format(new Object[] { new Object[] { a, b }, new Object[] { c, d } }));
  }

  @Test
  public void formats_primitive_array() {
    assertEquals(
        "[[" + 1 + ", " + 2 + "], [" + 3 + ", " + 4 + "]]",
        formatter.format(new int[][] { { 1, 2 }, { 3, 4 } }));
  }

  @Test
  public void formats_null() {
    assertEquals(
        "null",
        formatter.format(null));
  }
}
