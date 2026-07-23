package io.github.leawind.thirdperson.internal.logic;

import io.github.leawind.thirdperson.ThirdPerson;

public final class ModEvents {
  public static void register() {
    ThirdPerson.LOGGER.info("{} initialized", ThirdPerson.MOD_NAME);
  }
}
