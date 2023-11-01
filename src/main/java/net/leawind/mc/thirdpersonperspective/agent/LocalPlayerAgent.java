package net.leawind.mc.thirdpersonperspective.agent;


import net.leawind.mc.thirdpersonperspective.ThirdPersonPerspective;
import net.leawind.mc.util.Vectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.PerformanceSensitive;

/**
 * 第三人称自由视角玩家代理
 */
public class LocalPlayerAgent {
	private CameraAgent cameraAgent;
	private LocalPlayer player;
	/**
	 * 玩家此时应有的朝向
	 */
	private Vec2        vRot;

	public LocalPlayerAgent (LocalPlayer player) {
		this.player = player;
		this.vRot   = player.getRotationVector();
	}

	/**
	 * 玩家移动
	 * <p>
	 * 通过修改玩家的 xxa 和 zza 属性来控制玩家的移动方向和速度
	 * TODO yya!
	 */
	@PerformanceSensitive
	public void onServerAiStep () {
		if (player.isSwimming()) {
			return;
		}
		CameraAgent cameraAgent = CameraAgent.getInstance();
		float       left        = player.xxa;
		float       forward     = player.isFallFlying() ? 0 : player.zza;
		float       speed       = (float)Math.sqrt(left * left + forward * forward);// 记录此时的速度
		if (left != 0 || forward != 0) {
			float absoluteRot = (float)(cameraAgent.getRealRotY() + (-Math.atan2(left, forward) * 180 / Math.PI));
			if (!cameraAgent.cameraOffsetType.isAim()) {
				// 奔跑时立即转向移动方向
				// 否则缓慢转向移动方向
				vRot = new Vec2(0, absoluteRot);
				if (player.isSprinting()) {
					applyRotationInstantly();
				} else {
					applyRotationSmoothly();
				}
			}
			float relativeRot       = absoluteRot - player.getYRot();
			float relativeRotRadian = (float)(relativeRot * Math.PI / 180);
			player.xxa = (float)-Math.sin(relativeRotRadian) * speed;
			player.zza = (float)Math.cos(relativeRotRadian) * speed;
		}
	}

	@PerformanceSensitive
	public void onRenderTick (float lerpK) {
		cameraAgent = CameraAgent.getInstance();
		this.player = cameraAgent.player;
		if (shouldTurnToCameraHitResult()) {
			turnToCameraHitResultInstanly(lerpK);
		} else if (shouldTurnWithCamera()) {
			turnToCameraInstantly();
		}
	}

	/**
	 * 是否应当保持注视着相机的 hitResult
	 */
	public boolean shouldTurnToCameraHitResult () {
		return CameraAgent.getInstance().isAiming;
	}

	public void turnToCameraHitResultInstanly (float lerpK) {
		Vec2 relativeRotation = cameraAgent.getRelativeRotation();
		// 计算相机视线的 hitResult 坐标
		Vec3 cameraHitPosition = cameraAgent.getPickPosition();
		if (cameraHitPosition == null) {
			// 让玩家朝向相机的朝向
			LocalPlayerAgent.getInstance().turnToInstantly(relativeRotation.y + 180, -relativeRotation.x);
		} else {
			// 让玩家朝向该坐标
			Vec3 playerViewVector = player.getEyePosition(lerpK).vectorTo(cameraHitPosition);
			Vec2 playerViewRot    = Vectors.rotationAngleFromDirection(playerViewVector);
			LocalPlayerAgent.getInstance().turnToInstantly(playerViewRot);
		}
	}

	/**
	 * 是否应该保持和相机相同的朝向
	 * <p>
	 * 使用鞘翅飞行
	 * <p>
	 * 游泳
	 */
	public boolean shouldTurnWithCamera () {
		return player.isSwimming() || player.isFallFlying();
	}

	public void turnToCameraInstantly () {
		Vec2 relativeRotation = cameraAgent.getRelativeRotation();
		LocalPlayerAgent.getInstance().turnToInstantly(relativeRotation.y + 180, -relativeRotation.x);
	}

	/**
	 * 立即将朝向与玩家同步
	 */
	public void syncRotation () {
		vRot = player.getRotationVector();
	}

	/**
	 * 立即将朝向应用到玩家实体
	 */
	public void applyRotationInstantly () {
		player.setYRot(vRot.y);
		player.setXRot(vRot.x);
	}

	/**
	 * 平滑地转动玩家
	 */
	public void applyRotationSmoothly () {
		float playerY = player.getYRot();
		float dy      = ((vRot.y - playerY) % 360 + 360) % 360;
		if (dy > 180) {
			dy -= 360;
		}
		player.turn(dy, vRot.x - player.getXRot());
	}

	public void turnToInstantly (float y, float x) {
		vRot = new Vec2(x, y);
		applyRotationInstantly();
	}

	public void turnToInstantly (Vec2 rot) {
		vRot = rot;
		applyRotationInstantly();
	}

	/**
	 * 实例
	 */
	private static LocalPlayerAgent instance;

	public static boolean isAvailable () {
		return getInstance() != null && getInstance().player != null;
	}

	public static LocalPlayerAgent getInstance () {
		if (instance == null) {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player != null) {
				ThirdPersonPerspective.LOGGER.info(String.format("Creating LocalPlayerAgent for player at %s",
																 player.position()));
				instance = new LocalPlayerAgent(player);
			}
		}
		return instance;
	}
}
