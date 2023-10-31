package net.leawind.mc.thirdpersonperspective.mixininterface;

import net.minecraft.world.phys.Vec3;

public interface CameraMixinInterface {
	void third_Person_View$setRealPosition(Vec3 pos);

	void third_Person_View$setRealRotation(float yRot, float xRot);
}
