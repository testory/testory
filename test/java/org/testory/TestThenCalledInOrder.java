package org.testory;

import static java.lang.String.format;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.testory.Testory.mock;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledInOrder;
import static org.testory.Testory.when;
import static org.testory.testing.HamcrestMatchers.hasMessage;
import static org.testory.testing.HamcrestMatchers.hasMessageContaining;

import org.junit.Before;
import org.junit.Test;
import org.testory.proxy.InvocationMatcher;

public class TestThenCalledInOrder {
  private Mockable first, second, third;

  @Before
  public void before() {
    triggerPurge();
    first = mock(Mockable.class);
    second = mock(Mockable.class);
    third = mock(Mockable.class);
  }

  private static void triggerPurge() {
    when("");
    when("");
  }

  @Test
  public void asserts_invocations_in_order() {
    first.invoke();
    second.invoke();
    thenCalledInOrder(onInstance(first));
    thenCalledInOrder(onInstance(second));
  }

  @Test
  public void fails_if_invocations_not_in_order() {
    second.invoke();
    first.invoke();
    thenCalledInOrder(onInstance(first));
    try {
      thenCalledInOrder(onInstance(second));
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_if_invocation_is_missing() {
    try {
      thenCalledInOrder(onInstance(first));
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void fails_if_verifying_same_invocation_again() {
    first.invoke();
    thenCalledInOrder(onInstance(first));
    try {
      thenCalledInOrder(onInstance(first));
      fail();
    } catch (TestoryAssertionError e) {}
  }

  @Test
  public void ignores_invocations_before() {
    first.invoke();
    second.invoke();
    thenCalledInOrder(onInstance(second));
  }

  @Test
  public void ignores_invocations_after() {
    first.invoke();
    second.invoke();
    thenCalledInOrder(onInstance(first));
  }

  @Test
  public void ignores_invocations_between() {
    first.invoke();
    second.invoke();
    third.invoke();
    thenCalledInOrder(onInstance(first));
    thenCalledInOrder(onInstance(third));
  }

  @Test
  public void ordered_verifying_does_not_affect_unordered_verifying() {
    first.invoke();
    second.invoke();
    thenCalledInOrder(onInstance(second));
    thenCalled(onInstance(first));
  }

  @Test
  public void unordered_verifying_does_not_affect_ordered_verifying() {
    first.invoke();
    second.invoke();
    thenCalled(onInstance(second));
    thenCalledInOrder(onInstance(first));
  }

  @Test
  public void failure_prints_expected_call() {
    try {
      thenCalledInOrder(onInstance(first));
      fail();
    } catch (TestoryAssertionError e) {
      assertThat(e, hasMessageContaining(
          format(""
              + "  expected called in order\n"
              + "    %s\n"
              + "  but not called\n",
              onInstance(first))));
    }
  }

  @Test
  public void checks_that_invocation_matcher_is_not_null() {
    try {
      thenCalledInOrder((InvocationMatcher) null);
      fail();
    } catch (TestoryException e) {
      assertThat(e, hasMessage("expected not null"));
    }
  }

  @Test
  public void checks_that_mock_is_not_null() {
    try {
      thenCalledInOrder((Object) null);
      fail();
    } catch (TestoryException e) {
      assertThat(e, hasMessage("expected not null"));
    }
  }

  @Test
  public void checks_that_mock_is_mock() {
    try {
      thenCalledInOrder(new Object());
      fail();
    } catch (TestoryException e) {
      assertThat(e, hasMessage("expected mock"));
    }
  }

  private static class Mockable {
    void invoke() {}
  }
}
