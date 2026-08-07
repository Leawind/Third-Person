package io.github.leawind.thirdperson.internal.bridge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

class MinecraftSpatialQueryingTest {
  @Test
  void fallbackRayLimitPreservesShortRaysAndBoundsLongRays() {
    Vec3 from = new Vec3(1.0, 2.0, 3.0);

    assertEquals(
        new Vec3(5.0, 2.0, 3.0),
        MinecraftSpatialQuerying.limitRayEnd(from, new Vec3(5.0, 2.0, 3.0)));
    assertEquals(
        new Vec3(769.0, 2.0, 3.0),
        MinecraftSpatialQuerying.limitRayEnd(from, new Vec3(8193.0, 2.0, 3.0)));
    assertEquals(
        from,
        MinecraftSpatialQuerying.limitRayEnd(from, new Vec3(Double.POSITIVE_INFINITY, 2.0, 3.0)));
  }

  @Test
  void fallbackInflationCapsUnboundedValues() {
    assertEquals(32.0, MinecraftSpatialQuerying.limitInflation(32.0));
    assertEquals(768.0, MinecraftSpatialQuerying.limitInflation(1.0e9));
    assertEquals(768.0, MinecraftSpatialQuerying.limitInflation(Double.POSITIVE_INFINITY));
    assertEquals(768.0, MinecraftSpatialQuerying.limitInflation(Double.NaN));
  }
}
