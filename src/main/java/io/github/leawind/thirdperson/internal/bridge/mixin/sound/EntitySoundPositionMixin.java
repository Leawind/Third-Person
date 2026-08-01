package io.github.leawind.thirdperson.internal.bridge.mixin.sound;

import io.github.leawind.thirdperson.internal.bridge.events.SoundSourcePositionEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityBoundSoundInstance.class)
abstract class EntitySoundPositionMixin extends AbstractTickableSoundInstance {
  @Shadow private @Final Entity entity;

  private EntitySoundPositionMixin(
      SoundEvent soundEvent, SoundSource soundSource, RandomSource randomSource) {
    super(soundEvent, soundSource, randomSource);
  }

  @Inject(method = "<init>", at = @At("TAIL"))
  private void afterInitialization(CallbackInfo ci) {
    thirdPerson$adjustPosition();
  }

  @Inject(method = "tick", at = @At("TAIL"))
  private void afterTick(CallbackInfo ci) {
    if (!entity.isRemoved()) {
      thirdPerson$adjustPosition();
    }
  }

  @Unique
  private void thirdPerson$adjustPosition() {
    if (entity != Minecraft.getInstance().getCameraEntity()) {
      return;
    }
    Vec3 adjusted = SoundSourcePositionEvent.emit(entity, new Vec3(x, y, z));
    x = adjusted.x;
    y = adjusted.y;
    z = adjusted.z;
  }
}
