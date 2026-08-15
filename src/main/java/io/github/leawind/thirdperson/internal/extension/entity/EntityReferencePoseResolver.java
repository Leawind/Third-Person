package io.github.leawind.thirdperson.internal.extension.entity;

import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;

/// Resolves an entity's rendered eye position and environmental reference frame.
public interface EntityReferencePoseResolver {
  ExtensionResult<EntityReferencePose> resolve(EntityReferencePoseContext context);
}
