package com.github.leawind.thirdperson.minecraft.logic;

import com.github.leawind.thirdperson.api.ThirdPerson;
import com.github.leawind.thirdperson.minecraft.bridge.events.GameClientEvents;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.CameraSetupContext;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.EventContext;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.ExtractVisibleEntitiesContext;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.ModifyModelPartOpacityContext;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.MouseTurnPlayerStartContext;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.MoveImpulseContext;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.TurnPlayerContext;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public final class ThirdPersonEventHandler {
  public static void register() {
    ClientTickEvent.CLIENT_PRE.register(ThirdPersonEventHandler::onClientTickPre);
    ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(
        ThirdPersonEventHandler::onClientPlayerRespawn);
    ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(ThirdPersonEventHandler::onClientPlayerJoin);
    ClientRawInputEvent.MOUSE_SCROLLED.register(ThirdPersonEventHandler::onMouseScrolled);

    ClientLifecycleEvent.CLIENT_STOPPING.register(
        minecraft -> ThirdPerson.getConfigManager().lazySave());

    GameClientEvents.RENDER_TICK_START.on(ThirdPersonEventHandler::onRenderTickStart);
    GameClientEvents.SETUP_CAMERA.on(ThirdPersonEventHandler::onCameraSetup);
    GameClientEvents.MODIFY_FOV.on(ThirdPersonEventHandler::onModifyFov);
    GameClientEvents.MODIFY_MOVE_IMPULSE.on(ThirdPersonEventHandler::onCalculateMoveImpulse);
    GameClientEvents.TURN_PLAYER.on(ThirdPersonEventHandler::onEntityTurnStart);
    GameClientEvents.MOUSE_TURN_PLAYER_START.on(ThirdPersonEventHandler::onMouseTurnPlayerStart);
    GameClientEvents.ENABLE_BOB_VIEW.on(ThirdPersonEventHandler::onEnableBobView);
    GameClientEvents.ENABLE_THIRD_PERSON_CROSSHAIR.on(ThirdPersonEventHandler::onCrosshair);
    GameClientEvents.HANDLE_KEYBINDS_START.on(ThirdPersonEventHandler::onHandleKeybindsStart);
    GameClientEvents.DISABLE_DOUBLE_TAP_SPRINT.on(ThirdPersonEventHandler::onDisableSprint);
    GameClientEvents.EXTRACT_VISIBLE_ENTITIES.on(ThirdPersonEventHandler::onRenderEntity);
    GameClientEvents.MODIFY_MODEL_PART_OPACITY.on(ThirdPersonEventHandler::onModelPartOpacity);
  }

  /// Client tick 前
  private static void onClientTickPre(@NonNull Minecraft minecraft) {
    if (minecraft.isPaused()) {
      return;
    }
  }

  /// 当玩家死亡后重生或加入新的维度时触发
  private static void onClientPlayerRespawn(
      @NonNull LocalPlayer oldPlayer, @NonNull LocalPlayer newPlayer) {}

  private static void onClientPlayerJoin(@NonNull LocalPlayer player) {}

  private static void onCameraSetup(CameraSetupContext ctx) {}

  private static void onRenderTickStart(Float partialTick) {}

  private static void onModifyFov(EventContext<Float> ctx) {}

  private static void onEnableBobView(EventContext<Boolean> ctx) {}

  private static void onCrosshair(EventContext<Boolean> ctx) {}

  private static void onDisableSprint(EventContext<Boolean> ctx) {}

  private static void onCalculateMoveImpulse(MoveImpulseContext event) {}

  private static void onRenderEntity(ExtractVisibleEntitiesContext ctx) {}

  private static void onHandleKeybindsStart() {}

  private static void onMouseTurnPlayerStart(MouseTurnPlayerStartContext event) {}

  private static void onEntityTurnStart(TurnPlayerContext event) {}

  private static void onModelPartOpacity(ModifyModelPartOpacityContext ctx) {}

  private static @NonNull EventResult onMouseScrolled(
      @NonNull Minecraft minecraft, double amountX, double amountY) {
    int offset = (int) -Math.signum(amountY);

    return EventResult.pass();
  }
}
