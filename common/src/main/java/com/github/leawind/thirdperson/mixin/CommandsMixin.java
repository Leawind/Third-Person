package com.github.leawind.thirdperson.mixin;

import com.github.leawind.thirdperson.util.ItemPredicateUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.Commands.CommandSelection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public class CommandsMixin {

  @Inject(
      at = @At("TAIL"),
      method =
          "<init>(Lnet/minecraft/commands/Commands$CommandSelection;Lnet/minecraft/commands/CommandBuildContext;)V")
  private void init(CommandSelection selection, CommandBuildContext context, CallbackInfo ci) {
    ItemPredicateUtil.init(context);
  }
}