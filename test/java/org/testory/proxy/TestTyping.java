package org.testory.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testory.proxy.Typing.extending;
import static org.testory.proxy.Typing.implementing;
import static org.testory.proxy.Typing.subclassing;
import static org.testory.proxy.Typing.typing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class TestTyping {
  private Class<?> superclass;
  private Set<? extends Class<?>> interfaces;
  private Typing typing;

  @Before
  public void before() {
    superclass = Object.class;
    interfaces = classes(InterfaceA.class, InterfaceB.class);
  }

  @Test
  public void creates_legal_typing() {
    assertFactory(ConcreteClass.class, interfaces);
    assertFactory(AbstractClass.class, interfaces);
    assertFactory(ConcreteClass.class, classes());
    assertFactory(Object.class, interfaces);
  }

  private static void assertFactory(Class<?> superclass, Set<? extends Class<?>> interfaces) {
    Typing typing = typing(superclass, interfaces);
    assertEquals(superclass, typing.superclass);
    assertEquals(interfaces, typing.interfaces);
  }

  @Test
  public void fails_for_illegal_typing() {
    assertFactoryFails(Interface.class, interfaces);
    assertFactoryFails(AnnotationClass.class, interfaces);
    assertFactoryFails(int.class, interfaces);
    assertFactoryFails(Object[].class, interfaces);
    assertFactoryFails(superclass, classes(ConcreteClass.class));
    assertFactoryFails(superclass, classes(AbstractClass.class));
    assertFactoryFails(superclass, classes(AnnotationClass.class));
    assertFactoryFails(superclass, classes(int.class));
    assertFactoryFails(superclass, classes(Object[].class));
    assertFactoryFails(null, interfaces);
    assertFactoryFails(superclass, null);
  }

  private static void assertFactoryFails(Class<?> superclass, Set<? extends Class<?>> interfaces) {
    try {
      typing(superclass, interfaces);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void extends_type() {
    typing = extending(ConcreteClass.class);
    assertEquals(ConcreteClass.class, typing.superclass);
    assertEquals(classes(), typing.interfaces);
  }

  @Test
  public void extending_fails_for_concrete_class() {
    try {
      extending(Interface.class);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void implements_type() {
    typing = implementing(Interface.class);
    assertEquals(Object.class, typing.superclass);
    assertEquals(classes(Interface.class), typing.interfaces);
  }

  @Test
  public void implements_types() {
    typing = implementing(InterfaceA.class, InterfaceB.class);
    assertEquals(Object.class, typing.superclass);
    assertEquals(classes(InterfaceA.class, InterfaceB.class), typing.interfaces);
  }

  @Test
  public void implementing_fails_for_concrete_class() {
    try {
      implementing(ConcreteClass.class);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void subclasses_concrete_class() {
    typing = subclassing(ConcreteClass.class);
    assertEquals(ConcreteClass.class, typing.superclass);
    assertEquals(classes(), typing.interfaces);
  }

  @Test
  public void subclasses_abstract_class() {
    typing = subclassing(AbstractClass.class);
    assertEquals(AbstractClass.class, typing.superclass);
    assertEquals(classes(), typing.interfaces);
  }

  @Test
  public void subclasses_interface() {
    typing = subclassing(Interface.class);
    assertEquals(Object.class, typing.superclass);
    assertEquals(classes(Interface.class), typing.interfaces);
  }

  @Test
  public void subclasses_class_and_interfaces() {
    typing = subclassing(InterfaceA.class, ConcreteClass.class, InterfaceB.class);
    assertEquals(ConcreteClass.class, typing.superclass);
    assertEquals(classes(InterfaceA.class, InterfaceB.class), typing.interfaces);
  }

  @Test
  public void subclasses_nothing() {
    typing = subclassing();
    assertEquals(Object.class, typing.superclass);
    assertEquals(classes(), typing.interfaces);
  }

  @Test
  public void peels_class() {
    class Subclass extends ConcreteClass implements Interface {}
    typing = typing(Subclass.class, classes(InterfaceA.class, InterfaceB.class)).peel();
    assertEquals(ConcreteClass.class, typing.superclass);
    assertEquals(classes(Interface.class, InterfaceA.class, InterfaceB.class), typing.interfaces);
  }

  @Test
  public void peel_fails_for_object_superclass() {
    typing = typing(Object.class, classes());
    try {
      typing.peel();
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void sublcassing_fails_for_more_than_one_concrete_class() {
    try {
      subclassing(ConcreteClassA.class, ConcreteClassB.class);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void sublcassing_fails_for_null_type() {
    try {
      subclassing((Class<?>) null);
      fail();
    } catch (ProxyException e) {}
  }

  @Test
  public void sublcassing_fails_for_null_array() {
    try {
      subclassing((Class<?>[]) null);
      fail();
    } catch (ProxyException e) {}
  }

  private static Set<Class<?>> classes(Class<?>... classes) {
    return new HashSet<>(Arrays.asList(classes));
  }

  private static class ConcreteClass {}

  private static class ConcreteClassA {}

  private static class ConcreteClassB {}

  private static abstract class AbstractClass {}

  private static interface Interface {}

  private static interface InterfaceA {}

  private static interface InterfaceB {}

  private static @interface AnnotationClass {}
}
