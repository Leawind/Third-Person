package io.github.leawind.thirdperson.internal.bridge.entity;

import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import io.github.leawind.thirdperson.internal.core.base.pivot.PivotPose;

/// Resolves an entity's current rendered pivot pose when the implementation applies.
public interface EntityPoseSampler {
  ExtensionResult<PivotPose> sample(EntityPoseContext context);
}
