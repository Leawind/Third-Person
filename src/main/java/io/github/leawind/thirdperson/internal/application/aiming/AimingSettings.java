package io.github.leawind.thirdperson.internal.application.aiming;

import java.util.List;
import java.util.Objects;

/// Owns aiming preferences and user-supplied item predicates.
public final class AimingSettings {
  private static final int MAX_PATTERNS_PER_LIST = 1024;

  private boolean smartAiming = true;
  private List<String> holdToAimItemPatterns = List.of();
  private List<String> useToAimItemPatterns = List.of();
  private long revision;

  public boolean smartAiming() {
    return smartAiming;
  }

  public void setSmartAiming(boolean smartAiming) {
    if (this.smartAiming != smartAiming) {
      this.smartAiming = smartAiming;
      revision++;
    }
  }

  public List<String> holdToAimItemPatterns() {
    return holdToAimItemPatterns;
  }

  public void setHoldToAimItemPatterns(List<String> patterns) {
    List<String> copy = copyPatterns(patterns, "holdToAimItemPatterns");
    if (!holdToAimItemPatterns.equals(copy)) {
      holdToAimItemPatterns = copy;
      revision++;
    }
  }

  public List<String> useToAimItemPatterns() {
    return useToAimItemPatterns;
  }

  public void setUseToAimItemPatterns(List<String> patterns) {
    List<String> copy = copyPatterns(patterns, "useToAimItemPatterns");
    if (!useToAimItemPatterns.equals(copy)) {
      useToAimItemPatterns = copy;
      revision++;
    }
  }

  public long revision() {
    return revision;
  }

  public void restore(
      boolean smartAiming,
      List<String> holdToAimItemPatterns,
      List<String> useToAimItemPatterns) {
    this.smartAiming = smartAiming;
    this.holdToAimItemPatterns =
        copyPatterns(holdToAimItemPatterns, "holdToAimItemPatterns");
    this.useToAimItemPatterns = copyPatterns(useToAimItemPatterns, "useToAimItemPatterns");
    revision++;
  }

  private static List<String> copyPatterns(List<String> patterns, String name) {
    Objects.requireNonNull(patterns, name);
    if (patterns.size() > MAX_PATTERNS_PER_LIST || patterns.stream().anyMatch(Objects::isNull)) {
      throw new IllegalArgumentException(name + " must contain at most 1024 non-null values");
    }
    return List.copyOf(patterns);
  }
}
