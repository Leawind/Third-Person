package net.leawind.mc.thirdpersonperspective.userprofile;


import net.leawind.mc.thirdpersonperspective.ExpectPlatform;

import java.nio.file.Path;

public class UserProfile {
	public static Path getPath () {
		return ExpectPlatform.getConfigDirectory();
	}

	public static void load () {
	}

	public static void save () {
	}
}
