package io.github.leawind.thirdperson.gametest;

import io.github.leawind.perspectiveapi.api.PerspectiveAPI;
import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.logic.base.BaseRuntime;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;

/// Verifies that the normal perspective-toggle input activates and deactivates this perspective.
public final class PerspectiveLifecycleClientGameTest implements FabricClientGameTest {
  private static final String FIRST_PERSON_ID = "perspective_api.first_person";
  private static final int MAX_PERSPECTIVE_CYCLES = 4;
  private static final int SWITCH_TIMEOUT_TICKS = 20;

  @Override
  public void runTest(ClientGameTestContext context) {
    try (var world = context.worldBuilder().create()) {
      selectPerspective(context, FIRST_PERSON_ID);
      selectPerspective(context, ThirdPerson.PERSPECTIVE_ID);
      assertThirdPersonActive(context);

      selectPerspective(context, FIRST_PERSON_ID);
      assertFirstPersonRestored(context);
    }
  }

  private static void selectPerspective(ClientGameTestContext context, String perspectiveId) {
    for (int cycle = 0; cycle < MAX_PERSPECTIVE_CYCLES; cycle++) {
      if (context.computeOnClient(ignored -> PerspectiveAPI.isCurrent(perspectiveId))) {
        return;
      }
      tapPerspectiveToggle(context);
    }
    context.waitFor(ignored -> PerspectiveAPI.isCurrent(perspectiveId), SWITCH_TIMEOUT_TICKS);
  }

  private static void tapPerspectiveToggle(ClientGameTestContext context) {
    context.getInput().holdKeyFor(options -> options.keyTogglePerspective, 1);
    context.waitTick();
  }

  private static void assertThirdPersonActive(ClientGameTestContext context) {
    context.runOnClient(
        minecraft -> {
          assertTrue(
              PerspectiveAPI.isCurrent(ThirdPerson.PERSPECTIVE_ID),
              "perspective toggle did not select Leawind's Third Person");
          assertTrue(
              !minecraft.options.getCameraType().isFirstPerson(),
              "active third-person perspective kept the first-person camera type");
          assertTrue(
              BaseRuntime.getInstance().isCameraControlEnabled(),
              "third-person perspective did not enable camera control");
        });
  }

  private static void assertFirstPersonRestored(ClientGameTestContext context) {
    context.runOnClient(
        minecraft -> {
          assertTrue(
              PerspectiveAPI.isCurrent(FIRST_PERSON_ID),
              "perspective toggle did not return to first person");
          assertTrue(
              minecraft.options.getCameraType().isFirstPerson(),
              "first-person perspective did not restore the first-person camera type");
          assertTrue(
              !BaseRuntime.getInstance().isCameraControlEnabled(),
              "first-person perspective left third-person camera control enabled");
        });
  }

  private static void assertTrue(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
  }
}
