package io.github.leawind.thirdperson.internal.bridge.input;

import io.github.leawind.thirdperson.internal.bridge.events.LocalPlayerMovementInputEvent.MovementInput;
import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import io.github.leawind.thirdperson.internal.core.base.rotation.MovementIntent;
import net.minecraft.client.player.LocalPlayer;

/// Maps a world-space movement intent into the input basis used by the active Minecraft context.
public interface MovementInputMapper {
  ExtensionResult<MovementInput> map(LocalPlayer player, MovementIntent intent);
}
