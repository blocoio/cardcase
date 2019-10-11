package io.bloco.cardcase.common;

import androidx.annotation.Nullable;

// Based on http://google-collections.googlecode.com/svn-history/r78/trunk/javadoc/com/google/common/base/Preconditions.html

public class Preconditions {

  @SuppressWarnings("unused")
  public static void checkArgument(boolean expression, @Nullable Object errorMessage) {
    if (!expression) {
      throw new IllegalArgumentException(String.valueOf(errorMessage));
    }
  }

  public static void checkState(boolean expression, @Nullable Object errorMessage) {
    if (!expression) {
      throw new IllegalStateException(String.valueOf(errorMessage));
    }
  }

  @SuppressWarnings("UnusedReturnValue")
  public static <T> T checkNotNull(T reference, @Nullable Object errorMessage) {
    if (reference == null) {
      throw new NullPointerException(String.valueOf(errorMessage));
    }
    return reference;
  }
}