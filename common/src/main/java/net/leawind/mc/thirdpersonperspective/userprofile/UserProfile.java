package net.leawind.mc.thirdpersonperspective.userprofile;


import com.mojang.logging.LogUtils;
import net.leawind.mc.thirdpersonperspective.ExpectPlatform;
import net.leawind.mc.thirdpersonperspective.ThirdPersonPerspectiveMod;
import net.leawind.mc.thirdpersonperspective.core.CameraOffsetProfile;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class UserProfile {
	public static final  Logger                           LOGGER               = LogUtils.getLogger();
	private static final Map<Object, CameraOffsetProfile> cameraOffsetProfiles = new HashMap<>();

	private static Path getProfilePath () {
		return ExpectPlatform.getConfigDirectory().resolve(String.format("%s.profile.json5",
																		 ThirdPersonPerspectiveMod.MOD_ID));
	}

	private static CameraType getProfileKey () {
		return Minecraft.getInstance().options.getCameraType();
	}

	@NotNull
	public static CameraOffsetProfile getCameraOffsetProfile () {
		return cameraOffsetProfiles.get(getProfileKey());
	}

	public static void loadDefault () {
		cameraOffsetProfiles.clear();
		cameraOffsetProfiles.put(CameraType.THIRD_PERSON_BACK, CameraOffsetProfile.DEFAULT_CLOSER);
		cameraOffsetProfiles.put(CameraType.THIRD_PERSON_FRONT, CameraOffsetProfile.DEFAULT_FARTHER);
	}

	public static void load () {
		cameraOffsetProfiles.clear();
		// TODO
		LOGGER.info("User Profile is loaded");
	}

	public static void save () {
		// TODO
		LOGGER.info("User Profile is saved");
	}
}
