package net.leawind.mc.thirdperson;


import dev.architectury.registry.ReloadListenerRegistry;
import net.leawind.mc.thirdperson.resources.ItemPatternManager;
import net.minecraft.server.packs.PackType;

/**
 * 自定义资源包
 */
public final class ThirdPersonResources {
	public final static ItemPatternManager itemPatternManager = new ItemPatternManager();

	public static void register () {
		ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, ThirdPersonResources.itemPatternManager);
	}
}
