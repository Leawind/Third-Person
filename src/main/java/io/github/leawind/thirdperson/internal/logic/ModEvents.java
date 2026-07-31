package io.github.leawind.thirdperson.internal.logic;

import io.github.leawind.thirdperson.internal.bridge.events.BeforeInteractionEvent;
import io.github.leawind.thirdperson.internal.bridge.events.ClientTickEvent;
import io.github.leawind.thirdperson.internal.bridge.events.LocalPlayerMovementYawEvent;
import io.github.leawind.thirdperson.internal.bridge.events.LocalPlayerSprintImpulseEvent;
import io.github.leawind.thirdperson.internal.bridge.events.LocalPlayerTurnEvent;
import io.github.leawind.thirdperson.internal.bridge.events.MouseScrollEvent;
import io.github.leawind.thirdperson.internal.bridge.events.RenderFrameEvent;
import io.github.leawind.thirdperson.internal.bridge.events.ReticleGateEvent;
import io.github.leawind.thirdperson.internal.logic.base.MinecraftClientIntegration;
import io.github.leawind.thirdperson.internal.logic.base.MinecraftInputIntegration;
import io.github.leawind.thirdperson.internal.logic.base.MinecraftInteractionIntegration;
import io.github.leawind.thirdperson.internal.logic.scheduler.MinecraftCameraAdjustmentIntegration;
import io.github.leawind.thirdperson.internal.logic.scheduler.MinecraftHudIntegration;
import io.github.leawind.thirdperson.internal.logic.scheduler.MinecraftSchedulingIntegration;
import io.github.leawind.thirdperson.internal.logic.scheduler.SchedulerRuntime;
import io.github.leawind.thirdperson.internal.logic.scheduler.aiming.MinecraftItemPredicateIntegration;
import io.github.leawind.thirdperson.internal.logic.scheduler.input.MinecraftKeyIntegration;
import io.github.leawind.thirdperson.internal.logic.scheduler.state.MinecraftStatePersistence;
import net.minecraft.client.player.LocalPlayer;

/// Registers one listener per bridge event and makes cross-layer execution order explicit.
public final class ModEvents {
  private static boolean registered;

  private ModEvents() {}

  public static void register() {
    if (registered) {
      return;
    }
    registered = true;
    ClientTickEvent.register(ModEvents::onClientTick);
    RenderFrameEvent.register(ModEvents::beforeRenderFrame);
    LocalPlayerTurnEvent.register(ModEvents::onLocalPlayerTurn);
    LocalPlayerMovementYawEvent.register(MinecraftInputIntegration::modifyMovementYaw);
    LocalPlayerSprintImpulseEvent.register(
        MinecraftInputIntegration::modifySprintImpulseCondition);
    MouseScrollEvent.register(MinecraftCameraAdjustmentIntegration::onScroll);
    ReticleGateEvent.register(MinecraftHudIntegration::shouldRenderReticle);
    BeforeInteractionEvent.register(MinecraftInteractionIntegration::alignPlayerToCameraIntent);
  }

  private static void onClientTick() {
    MinecraftStatePersistence.onClientTick();
    MinecraftItemPredicateIntegration.onClientTick(
        SchedulerRuntime.getInstance().aimingSettings());
    MinecraftKeyIntegration.onClientTick();
    MinecraftSchedulingIntegration.onClientTick();
    MinecraftClientIntegration.onClientTick();
  }

  private static void beforeRenderFrame(float partialTick) {
    MinecraftSchedulingIntegration.beforeRenderFrame(partialTick);
    MinecraftClientIntegration.beforeRenderFrame(partialTick);
  }

  private static boolean onLocalPlayerTurn(
      LocalPlayer player, double rawYaw, double rawPitch) {
    return MinecraftCameraAdjustmentIntegration.onTurn(player, rawYaw, rawPitch)
        || MinecraftInputIntegration.onTurn(player, rawYaw, rawPitch);
  }
}
