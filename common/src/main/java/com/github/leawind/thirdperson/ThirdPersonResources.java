package com.github.leawind.thirdperson;


import com.github.leawind.thirdperson.resources.ItemPredicateManager;
import dev.architectury.registry.ReloadListenerRegistry;
import net.minecraft.server.packs.PackType;

/**
 * 自定义资源包
 */
public final class ThirdPersonResources {
	public final static ItemPredicateManager itemPredicateManager = new ItemPredicateManager();

	public static void register () {
		ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, ThirdPersonResources.itemPredicateManager);
	}
}
