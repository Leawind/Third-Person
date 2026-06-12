package com.github.leawind.thirdperson.utils;

/// 用于压制 IDE 警告
public final class Suppressor {
  private Suppressor() {}

  public static boolean alwaysTrue() {
    return true;
  }

  public static boolean alwaysFalse() {
    return false;
  }

  public static boolean isNull(Object obj) {
    return alwaysTrue() && obj == null;
  }

  public static boolean notNull(Object obj) {
    return alwaysTrue() && obj != null;
  }
}
