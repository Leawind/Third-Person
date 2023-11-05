package net.leawind.mc.thirdpersonperspective.userprofile;


import com.mojang.logging.LogUtils;
import net.leawind.mc.thirdpersonperspective.ExpectPlatform;
import net.leawind.mc.thirdpersonperspective.ThirdPersonPerspectiveMod;
import net.leawind.mc.thirdpersonperspective.core.CameraOffsetProfile;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;

public class UserProfile {
	public static final  Logger                LOGGER                      = LogUtils.getLogger();
	private static final CameraOffsetProfile[] cameraOffsetProfilesDefault = new CameraOffsetProfile[]{
		CameraOffsetProfile.DEFAULT_CLOSER, CameraOffsetProfile.DEFAULT_FARTHER};
	@NotNull
	private static       CameraOffsetProfile[] cameraOffsetProfiles;

	private static Path getProfilePath () {
		return ExpectPlatform.getConfigDirectory().resolve(String.format("%s.profile.json5",
																		 ThirdPersonPerspectiveMod.MOD_ID));
	}

	private static int getProfileIndex () {
		return Minecraft.getInstance().options.getCameraType().isMirrored() ? 1: 0;
	}

	@NotNull
	public static CameraOffsetProfile getCameraOffsetProfile () {
		return cameraOffsetProfiles[getProfileIndex()];
	}

	public static void loadDefault () {
		cameraOffsetProfiles = new CameraOffsetProfile[]{cameraOffsetProfilesDefault[0].clone(),
														 cameraOffsetProfilesDefault[1].clone()};
	}

	public static void load () {
		// TODO
		LOGGER.info("User Profile is loaded");
	}

	public static void save () {
		// TODO
		LOGGER.info("User Profile is saved");
	}
}
