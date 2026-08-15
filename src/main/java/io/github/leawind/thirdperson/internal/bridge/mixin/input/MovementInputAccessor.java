package io.github.leawind.thirdperson.internal.bridge.mixin.input;

/*? if >1.21 {*/
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.phys.Vec2;
/*? } else {*/
/*import net.minecraft.client.player.ClientInput;
*//*? }*/
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/*? if >1.21 {*/
@Mixin(ClientInput.class)
/*? } else {*/
/*@Mixin(Input.class)
*//*? }*/
interface MovementInputAccessor {
  /*? if >1.21 {*/
  @Accessor
  void setMoveVector(Vec2 movement);
  /*? } else {*/
  /*@Accessor
  void setLeftImpulse(float value);

  @Accessor
  void setForwardImpulse(float value);
  *//*? }*/
}
