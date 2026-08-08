package io.github.leawind.thirdperson.internal.bridge;

import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.bridge.compat.sable.SableCompatibility;
import java.util.function.Consumer;
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
import net.minecraft.world.phys.AABB;

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

  public static double blockInteractionRange(Minecraft minecraft) {
    /*? if >=1.20.5 {*/
    return minecraft.player.blockInteractionRange();
    /*? } else if fabric {*/
    /*return minecraft.gameMode.getPickRange();
    *//*? } else {*/
    /*return minecraft.player.getBlockReach();
    *//*? }*/
  }

  public static double entityInteractionRange(Minecraft minecraft) {
    /*? if >=1.20.5 {*/
    return minecraft.player.entityInteractionRange();
    /*? } else if fabric {*/
    /*return minecraft.gameMode.hasFarPickRange() ? 6.0 : 3.0;
    *//*? } else {*/
    /*return minecraft.player.getEntityReach();
    *//*? }*/
  }

  /// Whether the current version runs vanilla picking after applying the current frame's camera
  /// state.
  public static boolean vanillaPickFollowsCameraUpdate() {
    /*? if >=26.2 {*/
    return true;
    /*? } else {*/
    /*return false;
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

  public static double getBbSize(Entity entity){
    return Math.hypot(entity.getBbWidth() * Math.sqrt(2), entity.getBbHeight());
  }

  @Deprecated
  public static CameraSubjectMeasurements measureCameraSubject(Entity cameraEntity) {
    Entity rootVehicle = cameraEntity.getRootVehicle();
    AABB vehicleBounds =
      rootVehicle
        .getPassengersAndSelf()
        .map(SableCompatibility::getWorldBoundingBox)
        .reduce(AABB::minmax)
        .orElseGet(() -> SableCompatibility.getWorldBoundingBox(rootVehicle));
    double vehicleTotalSize =
      Math.hypot(
        Math.hypot(vehicleBounds.getXsize(), vehicleBounds.getYsize()),
        vehicleBounds.getZsize());
    double bodyRadius = cameraEntity.getBbWidth() * 0.5 * Math.sqrt(3.0);
    return new CameraSubjectMeasurements(bodyRadius, vehicleTotalSize);
  }

  @Deprecated
  public record CameraSubjectMeasurements(double bodyRadius, double vehicleTotalSize) {}
}
