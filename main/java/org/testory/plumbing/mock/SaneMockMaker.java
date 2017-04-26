package org.testory.plumbing.mock;

import static java.util.Objects.deepEquals;
import static org.testory.plumbing.PlumbingException.check;
import static org.testory.plumbing.Stubbing.stubbing;

import org.testory.plumbing.Maker;
import org.testory.plumbing.Stubbing;
import org.testory.plumbing.history.History;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.InvocationMatcher;

public class SaneMockMaker implements Maker {
  private final Maker mockMaker;
  private final History history;

  private SaneMockMaker(Maker mockMaker, History history) {
    this.mockMaker = mockMaker;
    this.history = history;
  }

  public static Maker sane(Maker mockMaker, History history) {
    check(mockMaker != null);
    check(history != null);
    return new SaneMockMaker(mockMaker, history);
  }

  public <T> T make(Class<T> type, String name) {
    check(type != null);
    check(name != null);
    T mock = mockMaker.make(type, name);
    history
        .add(stubbingEquals(mock, name))
        .add(stubbingHashCode(mock, name))
        .add(stubbingToString(mock, name));
    return mock;
  }

  private static Stubbing stubbingEquals(final Object mock, String name) {
    return stubbing(onInvocation(mock, "equals", Object.class), new Handler() {
      public Object handle(Invocation invocation) {
        return mock == invocation.arguments.get(0);
      }
    });
  }

  private static Stubbing stubbingHashCode(final Object mock, final String name) {
    return stubbing(onInvocation(mock, "hashCode"), new Handler() {
      public Object handle(Invocation invocation) {
        return name.hashCode();
      }
    });
  }

  private static Stubbing stubbingToString(final Object mock, final String name) {
    return stubbing(onInvocation(mock, "toString"), new Handler() {
      public Object handle(Invocation invocation) {
        return name;
      }
    });
  }

  private static InvocationMatcher onInvocation(
      final Object instance, final String methodName, final Class<?>... parameters) {
    return new InvocationMatcher() {
      public boolean matches(Invocation invocation) {
        return invocation.instance == instance
            && deepEquals(invocation.method.getName(), methodName)
            && deepEquals(invocation.method.getParameterTypes(), parameters);
      }
    };
  }
}
