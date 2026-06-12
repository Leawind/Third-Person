package com.github.leawind.thirdperson.minecraft.logic.resources;

import com.github.leawind.thirdperson.api.ThirdPerson;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

/** 自定义资源包 */
public final class ThirdPersonResources {
  public static final ItemPredicateManager ITEM_PREDICATE_MANAGER = new ItemPredicateManager();

  public static void register() {
    ReloadListenerRegistry.register(
        PackType.CLIENT_RESOURCES,
        ThirdPersonResources.ITEM_PREDICATE_MANAGER,
        Identifier.fromNamespaceAndPath(ThirdPerson.MOD_ID, ItemPredicateManager.ID));
  }
}
