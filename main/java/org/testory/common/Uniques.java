package org.testory.common;

import static org.testory.common.Checks.checkArgument;
import static org.testory.common.Checks.checkNotNull;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.List;

import net.sf.cglib.core.CodeGenerationException;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.NoOp;

import org.objenesis.ObjenesisStd;

public class Uniques {
  public static boolean hasUniques(Class<?> type) {
    checkNotNull(type);
    return !type.isPrimitive();
  }

  public static <T> T unique(Class<T> type) {
    checkNotNull(type);
    checkArgument(hasUniques(type));
    return type.isArray()
        ? (T) Array.newInstance(type.getComponentType(), 0)
        : !Modifier.isAbstract(type.getModifiers())
            ? new ObjenesisStd().newInstance(type)
            : newCglibProxy(type);
  }

  private static <T> T newCglibProxy(Class<T> type) {
    Enhancer enhancer = new Enhancer() {
      /** includes all constructors */
      protected void filterConstructors(Class sc, List constructors) {}
    };
    enhancer.setClassLoader(Thread.currentThread().getContextClassLoader());

    class CglibBugWorkaround {}
    if (type == Object.class) {
      enhancer.setSuperclass(CglibBugWorkaround.class);
      enhancer.setInterfaces(new Class[0]);
    } else if (type.isInterface()) {
      enhancer.setSuperclass(CglibBugWorkaround.class);
      enhancer.setInterfaces(new Class[] { type });
    } else {
      enhancer.setSuperclass(type);
      enhancer.setInterfaces(new Class[0]);
    }

    enhancer.setCallbackTypes(new Class[] { NoOp.class });

    Class<?> proxyClass;
    try {
      proxyClass = enhancer.createClass();
    } catch (CodeGenerationException e) {
      throw new IllegalArgumentException(e);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(e);
    }

    Factory proxy = (Factory) new ObjenesisStd().newInstance(proxyClass);
    proxy.setCallback(0, NoOp.INSTANCE);
    return (T) proxy;
  }
}
