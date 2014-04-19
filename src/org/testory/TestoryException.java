package org.testory;

import org.testory.proxy.Proxies;

public class TestoryException extends RuntimeException {
  private static final long serialVersionUID = -5222839249836069701L;

  public TestoryException() {}

  public TestoryException(String message) {
    super(message);
  }

  public TestoryException(Throwable cause) {
    super(cause);
  }

  public TestoryException(String message, Throwable cause) {
    super(message, cause);
  }

  public static void check(boolean condition) {
    if (!condition) {
      throw decorate(new TestoryException());
    }
  }

  private static TestoryException decorate(TestoryException original) {
    TestoryException check = new TestoryException("failed check", original);
    check.setStackTrace(failedCheckTrace(original.getStackTrace()));
    TestoryException caller = new TestoryException("incorrect usage", check);
    caller.setStackTrace(callerTrace(original.getStackTrace()));
    return caller;
  }

  private static StackTraceElement[] callerTrace(StackTraceElement[] stackTrace) {
    for (int i = stackTrace.length - 1; i >= 0; i--) {
      String name = stackTrace[i].getClassName();
      if (name.equals(Testory.class.getName()) || name.startsWith(Proxies.class.getName())) {
        for (int j = i + 1; j < stackTrace.length; j++) {
          if (stackTrace[j].getLineNumber() >= 0) {
            return new StackTraceElement[] { stackTrace[j] };
          }
        }
        throw new Error();
      }
    }
    throw new Error();
  }

  private static StackTraceElement[] failedCheckTrace(StackTraceElement[] stackTrace) {
    for (int i = stackTrace.length - 1; i >= 0; i--) {
      if (stackTrace[i].getClassName().equals(TestoryException.class.getName())
          && stackTrace[i].getMethodName().equals("check")) {
        return new StackTraceElement[] { stackTrace[i + 1] };
      }
    }
    throw new Error();
  }
}
