package net.leawind.mc.fabric.thirdperson;


import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.leawind.mc.thirdperson.ThirdPerson;

/**
 * Modmenu 入口
 */
@SuppressWarnings("unused")
public class ModMenuEntry implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory () {
		return ThirdPerson.CONFIG_MANAGER::getConfigScreen;
	}
}
