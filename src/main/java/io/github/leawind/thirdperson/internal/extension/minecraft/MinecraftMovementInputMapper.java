package io.github.leawind.thirdperson.internal.extension.minecraft;

import io.github.leawind.perspectiveapi.api.PerspectiveMath;
import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import io.github.leawind.thirdperson.internal.core.base.rotation.MovementIntent;
import io.github.leawind.thirdperson.internal.extension.input.MinecraftMovementInputMapping;
import io.github.leawind.thirdperson.internal.extension.input.MovementInput;
import io.github.leawind.thirdperson.internal.extension.input.MovementInputMapper;
import net.minecraft.client.player.LocalPlayer;
import org.joml.Quaternionf;

/// Maps camera-relative movement into the standard Minecraft player heading.
final class MinecraftMovementInputMapper implements MovementInputMapper {
  static final MinecraftMovementInputMapper INSTANCE = new MinecraftMovementInputMapper();

  private MinecraftMovementInputMapper() {}

  @Override
  public ExtensionResult<MovementInput> map(LocalPlayer player, MovementIntent intent) {
    var worldFromInput =
        PerspectiveMath.eulerDegToQuat(0.0f, player.getYRot(), 0.0f, new Quaternionf());
    return MinecraftMovementInputMapping.mapToBasis(intent, worldFromInput);
  }
}
