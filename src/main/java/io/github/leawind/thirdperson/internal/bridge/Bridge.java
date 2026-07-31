package io.github.leawind.thirdperson.internal.bridge;

import io.github.leawind.thirdperson.ThirdPerson;
import java.util.function.Consumer;
import java.util.Optional;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
/*? if >=1.20.5 {*/
import net.minecraft.core.component.DataComponents;
/*? }*/
/*? if >=1.21.11 {*/
import net.minecraft.resources.Identifier;
/*? }*/
import net.minecraft.server.packs.resources.PreparableReloadListener;
/*? if !neoforge {*/
import net.minecraft.server.packs.resources.ReloadableResourceManager;
/*? }*/
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// Version-sensitive Minecraft operations used by version-neutral logic.
public final class Bridge {
  /*? if >=1.21.11 {*/
  private static final KeyMapping.Category KEY_MAPPING_CATEGORY =
      KeyMapping.Category.register(
          Identifier.fromNamespaceAndPath(ThirdPerson.MOD_ID, "keybinds"));
  /*? } else {*/
  /*private static final String KEY_MAPPING_CATEGORY =
      "key.categories." + ThirdPerson.MOD_ID;
  *//*? }*/

  /*? if !neoforge {*/
  private static ReloadableResourceManager registeredResourceManager;
  /*? }*/

  private Bridge() {}

  public static KeyMapping createKeyMapping(String translationKey, int keyCode) {
    return new KeyMapping(translationKey, keyCode, KEY_MAPPING_CATEGORY);
  }

  public static boolean isScreenOpen(Minecraft minecraft) {
    /*? if >=26.2 {*/
    return minecraft.gui.screen() != null;
    /*? } else {*/
    /*return minecraft.screen != null;
    *//*? }*/
  }

  public static boolean isEating(LocalPlayer player) {
    if (!player.isUsingItem()) {
      return false;
    }
    /*? if >=1.20.5 {*/
    return player.getUseItem().get(DataComponents.FOOD) != null;
    /*? } else {*/
    /*return player.getUseItem().isEdible();
    *//*? }*/
  }

  public static double interactionRange(Minecraft minecraft) {
    /*? if >=1.20.5 {*/
    return Math.max(
        minecraft.player.blockInteractionRange(), minecraft.player.entityInteractionRange());
    /*? } else {*/
    /*return minecraft.gameMode.getPickRange();
    *//*? }*/
  }

  public static void registerReloadListener(
      Minecraft minecraft,
      PreparableReloadListener listener,
      Consumer<ResourceManager> initialLoad) {
    /*? if !neoforge {*/
    var resourceManager = minecraft.getResourceManager();
    if (resourceManager instanceof ReloadableResourceManager reloadable
        && reloadable != registeredResourceManager) {
      registeredResourceManager = reloadable;
      reloadable.registerReloadListener(listener);
      initialLoad.accept(reloadable);
    }
    /*? }*/
  }

  public static Optional<Vector3d> clipVisualBlocks(
      Entity entity, Vector3dc from, Vector3dc to) {
    HitResult hit =
        entity
            .level()
            .clip(
                new ClipContext(
                    toVec3(from),
                    toVec3(to),
                    ClipContext.Block.VISUAL,
                    ClipContext.Fluid.NONE,
                    entity));
    return hit.getType() == HitResult.Type.MISS
        ? Optional.empty()
        : Optional.of(toVector3d(hit.getLocation()));
  }

  public static BlockHit clipBlocks(
      Entity entity, Vec3 from, Vec3 to, boolean useColliderBlocks) {
    HitResult hit =
        entity
            .level()
            .clip(
                new ClipContext(
                    from,
                    to,
                    useColliderBlocks
                        ? ClipContext.Block.COLLIDER
                        : ClipContext.Block.OUTLINE,
                    ClipContext.Fluid.NONE,
                    entity));
    return new BlockHit(
        hit.getLocation(),
        hit.getType() == HitResult.Type.BLOCK,
        hit.getType() == HitResult.Type.MISS);
  }

  public static Optional<Vec3> pickEntity(
      Entity source, Vec3 from, Vec3 to, double maxDistanceSquared) {
    EntityHitResult hit =
        ProjectileUtil.getEntityHitResult(
            source,
            from,
            to,
            new AABB(from, to),
            entity -> !entity.isSpectator() && entity.isPickable(),
            maxDistanceSquared);
    return hit == null ? Optional.empty() : Optional.of(hit.getLocation());
  }

  public static CameraSubjectMeasurements measureCameraSubject(Entity cameraEntity) {
    Entity rootVehicle = cameraEntity.getRootVehicle();
    AABB vehicleBounds =
        rootVehicle
            .getPassengersAndSelf()
            .map(Entity::getBoundingBox)
            .reduce(AABB::minmax)
            .orElseGet(rootVehicle::getBoundingBox);
    double vehicleTotalSize =
        Math.hypot(
            Math.hypot(vehicleBounds.getXsize(), vehicleBounds.getYsize()),
            vehicleBounds.getZsize());
    double bodyRadius = cameraEntity.getBbWidth() * 0.5 * Math.sqrt(3.0);
    return new CameraSubjectMeasurements(bodyRadius, vehicleTotalSize);
  }

  private static Vec3 toVec3(Vector3dc value) {
    return new Vec3(value.x(), value.y(), value.z());
  }

  private static Vector3d toVector3d(Vec3 value) {
    return new Vector3d(value.x, value.y, value.z);
  }

  public record BlockHit(Vec3 location, boolean blocked, boolean missed) {}

  public record CameraSubjectMeasurements(double bodyRadius, double vehicleTotalSize) {}
}
