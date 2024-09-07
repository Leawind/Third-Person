package net.leawind.mc.thirdperson;


import dev.architectury.registry.ReloadListenerRegistry;
import net.leawind.mc.thirdperson.resources.ItemPredicateManager;
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
