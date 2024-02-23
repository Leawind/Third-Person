package net.leawind.mc.thirdperson.fabric.resources;


import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.leawind.mc.thirdperson.ThirdPersonConstants;
import net.leawind.mc.thirdperson.resources.ItemPatternManager;
import net.minecraft.resources.ResourceLocation;

public class IdentifiableItemPatternManager extends ItemPatternManager implements IdentifiableResourceReloadListener {
	@Override
	public ResourceLocation getFabricId () {
		return new ResourceLocation(String.format("%s:reload_%s", ThirdPersonConstants.MOD_ID, ItemPatternManager.ID));
	}
}
