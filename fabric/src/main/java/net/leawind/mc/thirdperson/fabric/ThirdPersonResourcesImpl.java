package net.leawind.mc.thirdperson.fabric;


import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.leawind.mc.thirdperson.ThirdPersonResources;
import net.leawind.mc.thirdperson.fabric.resources.IdentifiableItemPatternManager;
import net.minecraft.server.packs.PackType;

@SuppressWarnings("unused")
public class ThirdPersonResourcesImpl {
	public static void register () {
		ThirdPersonResources.itemPatternManager = new IdentifiableItemPatternManager();
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener((IdentifiableResourceReloadListener)ThirdPersonResources.itemPatternManager);
	}
}
