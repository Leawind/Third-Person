package net.leawind.mc.thirdperson.forge;


import net.leawind.mc.thirdperson.ThirdPersonResources;
import net.leawind.mc.thirdperson.resources.ItemPatternManager;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * @see ThirdPersonResources
 */
@SuppressWarnings("unused")
public class ThirdPersonResourcesImpl {
	public static void register () {
		ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		if (resourceManager instanceof ReloadableResourceManager reloadableResourceManager) {
			ThirdPersonResources.itemPatternManager = new ItemPatternManager();
			reloadableResourceManager.registerReloadListener(ThirdPersonResources.itemPatternManager);
		}
	}
}
