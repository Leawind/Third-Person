package net.leawind.mc.thirdperson.fabric;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "SameReturnValue"})
public class ExpectPlatformExampleImpl {
	@Contract(pure=true)
	public static @NotNull String getMessage () {
		return "This is Fabric";
	}
}
