package com.github.leawind.thirdperson.neoforge;

import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonConstants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@SuppressWarnings("unused")
@Mod(value = ThirdPersonConstants.MOD_ID, dist = net.neoforged.api.distmarker.Dist.CLIENT)
public final class ThirdPersonNeoForgeClient {
  public ThirdPersonNeoForgeClient(IEventBus modBus) {
    ThirdPerson.init();
  }
}
