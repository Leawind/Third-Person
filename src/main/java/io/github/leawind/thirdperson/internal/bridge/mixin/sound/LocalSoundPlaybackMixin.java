package io.github.leawind.thirdperson.internal.bridge.mixin.sound;

import io.github.leawind.thirdperson.internal.bridge.events.SoundSourcePositionEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(net.minecraft.client.multiplayer.ClientLevel.class)
abstract class LocalSoundPlaybackMixin {
  @ModifyArgs(
      method =
          "playSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZJ)V",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;<init>(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFLnet/minecraft/util/RandomSource;DDD)V"))
  private void adjustCameraEntitySound(Args args) {
    Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
    if (cameraEntity != null
        && (thirdPerson$isCurrentPosition(args, cameraEntity)
            || thirdPerson$isEncodedPreviousPosition(args, cameraEntity))) {
      thirdPerson$adjustArguments(args, cameraEntity);
    }
  }

  @Unique
  private static boolean thirdPerson$isCurrentPosition(Args args, Entity cameraEntity) {
    return cameraEntity.getX() == thirdPerson$x(args)
        && cameraEntity.getY() == thirdPerson$y(args)
        && cameraEntity.getZ() == thirdPerson$z(args);
  }

  @Unique
  private static boolean thirdPerson$isEncodedPreviousPosition(Args args, Entity cameraEntity) {
    return thirdPerson$encodedCoordinate(cameraEntity.xo) == thirdPerson$x(args)
        && thirdPerson$encodedCoordinate(cameraEntity.yo) == thirdPerson$y(args)
        && thirdPerson$encodedCoordinate(cameraEntity.zo) == thirdPerson$z(args);
  }

  @Unique
  private static void thirdPerson$adjustArguments(Args args, Entity cameraEntity) {
    Vec3 adjusted =
        SoundSourcePositionEvent.emit(
            cameraEntity, new Vec3(thirdPerson$x(args), thirdPerson$y(args), thirdPerson$z(args)));
    args.set(5, adjusted.x);
    args.set(6, adjusted.y);
    args.set(7, adjusted.z);
  }

  @Unique
  private static double thirdPerson$encodedCoordinate(double coordinate) {
    return (int) (coordinate * 8.0) / 8.0;
  }

  @Unique
  private static double thirdPerson$x(Args args) {
    return args.get(5);
  }

  @Unique
  private static double thirdPerson$y(Args args) {
    return args.get(6);
  }

  @Unique
  private static double thirdPerson$z(Args args) {
    return args.get(7);
  }
}
