package io.github.leawind.thirdperson.internal.bridge.mixin.cameraentityopacity;

import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/// Selects Mixins for Minecraft's direct-render or extracted-render-state pipeline.
public final class CameraEntityOpacityMixinPlugin implements IMixinConfigPlugin {
  private static final String SUBMIT_NODE_COLLECTION_RESOURCE =
      "net/minecraft/client/renderer/SubmitNodeCollection.class";

  private boolean extractedRenderStatePipeline;

  @Override
  public void onLoad(String mixinPackage) {
    extractedRenderStatePipeline =
        CameraEntityOpacityMixinPlugin.class
                .getClassLoader()
                .getResource(SUBMIT_NODE_COLLECTION_RESOURCE)
            != null;
  }

  @Override
  public String getRefMapperConfig() {
    return null;
  }

  @Override
  public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
    String simpleName = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
    return switch (simpleName) {
      case "LevelRendererMixin", "ModelPartCubeMixin", "ModelPartMixin", "RenderTypeMixin" ->
          !extractedRenderStatePipeline;
      case "EntityRenderDispatcherMixin",
              "EntityRendererMixin",
              "RenderTypesMixin",
              "SubmitNodeCollectionMixin" -> extractedRenderStatePipeline;
      default -> true;
    };
  }

  @Override
  public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

  @Override
  public List<String> getMixins() {
    return null;
  }

  @Override
  public void preApply(
      String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

  @Override
  public void postApply(
      String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
