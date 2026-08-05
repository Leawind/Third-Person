package io.github.leawind.thirdperson.gametest;

import io.github.leawind.perspectiveapi.api.PerspectiveAPI;
import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.logic.base.BaseRuntime;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;

/// Exercises the camera at a very large valid world coordinate without manual input.
public final class LargeCoordinateCameraClientGameTest implements FabricClientGameTest {
  private static final int MAX_PERSPECTIVE_CYCLES = 4;
  private static final int STARTUP_SETTLE_TICKS = 20;
  private static final int SWITCH_CONFIRMATION_TICKS = 5;
  private static final int CLIENT_SYNC_TIMEOUT_TICKS = 80;
  private static final double REMOTE_COORDINATE_THRESHOLD = 10_000_000;

  @Override
  public void runTest(ClientGameTestContext context) {
    try (var world = context.worldBuilder().create()) {
      world.getServer().runCommand("effect give @a minecraft:resistance infinite 255 true");
      world.getServer().runCommand("tp @a 0 256 0");
      waitForPlayer(context);
      waitTicks(context, STARTUP_SETTLE_TICKS);

      selectPerspective(context, ThirdPerson.PERSPECTIVE_ID);
      assertThirdPersonActive(context, "after selection");
      waitTicks(context, 40);
      assertThirdPersonActive(context, "after fixed-position wait");

      world
          .getServer()
          .runCommand("tp @a -25000000 256 25000000");
      waitTicks(context, 20);

      context.runOnClient(
          minecraft -> {
            assertTrue(
                PerspectiveAPI.isCurrent(ThirdPerson.PERSPECTIVE_ID),
                "large-coordinate teleport changed the selected perspective");
            assertTrue(
                BaseRuntime.getInstance().isCameraControlEnabled(),
                "large-coordinate teleport disabled third-person camera control");
            assertTrue(minecraft.player != null, "client player disappeared after large-coordinate teleport");
            assertFinite(minecraft.player.getY(), "client player y position");

            var cameraState = PerspectiveAPI.getPreviousCameraState();
            assertTrue(cameraState != null, "camera state was not produced at the large coordinate");
            var cameraPosition = cameraState.position();
            assertFinite(cameraPosition.x(), "camera x position");
            assertFinite(cameraPosition.y(), "camera y position");
            assertFinite(cameraPosition.z(), "camera z position");
            assertTrue(
                Math.abs(cameraPosition.x()) > REMOTE_COORDINATE_THRESHOLD
                    && Math.abs(cameraPosition.z()) > REMOTE_COORDINATE_THRESHOLD,
                "camera did not reach the remote coordinate region");
          });
    }
  }

  private static void selectPerspective(ClientGameTestContext context, String perspectiveId) {
    for (int cycle = 0; cycle <= MAX_PERSPECTIVE_CYCLES; cycle++) {
      if (isPerspectiveStable(context, perspectiveId)) {
        return;
      }
      if (cycle < MAX_PERSPECTIVE_CYCLES) {
        context.getInput().holdKeyFor(options -> options.keyTogglePerspective, 1);
        context.waitTick();
      }
    }
    throw new AssertionError("perspective switcher did not stably select " + perspectiveId);
  }

  private static boolean isPerspectiveStable(ClientGameTestContext context, String perspectiveId) {
    for (int tick = 0; tick < SWITCH_CONFIRMATION_TICKS; tick++) {
      context.waitTick();
      if (!context.computeOnClient(ignored -> PerspectiveAPI.isCurrent(perspectiveId))) {
        return false;
      }
    }
    return true;
  }

  private static void assertThirdPersonActive(ClientGameTestContext context, String phase) {
    context.runOnClient(
        ignored -> {
          assertTrue(
              PerspectiveAPI.isCurrent(ThirdPerson.PERSPECTIVE_ID),
              phase + " did not keep Leawind's Third Person selected");
          assertTrue(
              BaseRuntime.getInstance().isCameraControlEnabled(),
              phase + " did not keep third-person camera control enabled");
        });
  }

  private static void waitForPlayer(ClientGameTestContext context) {
    context.waitFor(minecraft -> minecraft.player != null, CLIENT_SYNC_TIMEOUT_TICKS);
  }

  private static void waitTicks(ClientGameTestContext context, int ticks) {
    for (int tick = 0; tick < ticks; tick++) {
      context.waitTick();
    }
  }

  private static void assertFinite(double value, String description) {
    assertTrue(Double.isFinite(value), description + " must be finite but was " + value);
  }

  private static void assertTrue(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
  }
}
