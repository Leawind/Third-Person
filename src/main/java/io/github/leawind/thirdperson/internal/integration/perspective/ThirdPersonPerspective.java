package io.github.leawind.thirdperson.internal.integration.perspective;

import com.google.auto.service.AutoService;
import io.github.leawind.perspectiveapi.api.PerspectiveBehavior;
import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;

/// The single manually selectable perspective provided by this mod.
@AutoService(PerspectiveBehavior.class)
@PerspectiveBehavior.Info(
    id = ThirdPerson.PERSPECTIVE_ID,
    nameKey = "perspective.leawind_third_person.third_person.name",
    descriptionKey = "perspective.leawind_third_person.third_person.description",
    priority = 10,
    baseType = PerspectiveBehavior.BaseType.THIRD_PERSON_BACK,
    switchable = true)
@SuppressWarnings("unused")
public final class ThirdPersonPerspective implements PerspectiveBehavior {
  private final ThirdPersonRuntime runtime = ThirdPersonRuntime.getInstance();

  @Override
  public void onActivate() {
    runtime.onPerspectiveActivated();
  }

  @Override
  public void onDeactivate() {
    runtime.onPerspectiveDeactivated();
  }
}
