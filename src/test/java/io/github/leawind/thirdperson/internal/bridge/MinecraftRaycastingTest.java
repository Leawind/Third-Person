package io.github.leawind.thirdperson.internal.bridge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

class MinecraftRaycastingTest {
  @Test
  void fallbackRayLimitPreservesShortRaysAndBoundsLongRays() {
    Vec3 from = new Vec3(1.0, 2.0, 3.0);

    assertEquals(
        new Vec3(5.0, 2.0, 3.0), MinecraftRaycasting.limitRayEnd(from, new Vec3(5.0, 2.0, 3.0)));
    assertEquals(
        new Vec3(4097.0, 2.0, 3.0),
        MinecraftRaycasting.limitRayEnd(from, new Vec3(8193.0, 2.0, 3.0)));
    assertEquals(
        from,
        MinecraftRaycasting.limitRayEnd(from, new Vec3(Double.POSITIVE_INFINITY, 2.0, 3.0)));
  }
}
