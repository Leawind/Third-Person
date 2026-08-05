package io.github.leawind.thirdperson.internal.bridge.mixin.sound;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.leawind.thirdperson.internal.bridge.events.SoundSourcePositionEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(net.minecraft.client.multiplayer.ClientLevel.class)
abstract class LocalSoundPlaybackMixin {
  @WrapOperation(
      method =
          "playSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZJ)V",
      at =
          @At(
              value = "NEW",
              target = "Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"))
  private SimpleSoundInstance wrapCameraEntitySound(
      SoundEvent sound,
      SoundSource source,
      float volume,
      float pitch,
      RandomSource random,
      double x,
      double y,
      double z,
      Operation<SimpleSoundInstance> original) {
    Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
    if (cameraEntity != null
        && (thirdPerson$isCurrentPosition(x, y, z, cameraEntity)
            || thirdPerson$isEncodedPreviousPosition(x, y, z, cameraEntity))) {
      Vec3 adjusted = SoundSourcePositionEvent.emit(cameraEntity, new Vec3(x, y, z));
      x = adjusted.x;
      y = adjusted.y;
      z = adjusted.z;
    }
    return original.call(sound, source, volume, pitch, random, x, y, z);
  }

  @Unique
  private static boolean thirdPerson$isCurrentPosition(
      double x, double y, double z, Entity cameraEntity) {
    return cameraEntity.getX() == x && cameraEntity.getY() == y && cameraEntity.getZ() == z;
  }

  @Unique
  private static boolean thirdPerson$isEncodedPreviousPosition(
      double x, double y, double z, Entity cameraEntity) {
    return thirdPerson$encodedCoordinate(cameraEntity.xo) == x
        && thirdPerson$encodedCoordinate(cameraEntity.yo) == y
        && thirdPerson$encodedCoordinate(cameraEntity.zo) == z;
  }

  @Unique
  private static double thirdPerson$encodedCoordinate(double coordinate) {
    return (int) (coordinate * 8.0) / 8.0;
  }
}
