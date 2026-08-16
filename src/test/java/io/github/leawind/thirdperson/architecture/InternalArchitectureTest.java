package io.github.leawind.thirdperson.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.Set;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

class InternalArchitectureTest {
  private static final String PROJECT = "io.github.leawind.thirdperson";
  private static final String INTERNAL = PROJECT + ".internal";
  private static final String API = PROJECT + ".api";
  private static final String PLATFORM = PROJECT + ".platform";
  private static final String PLATFORM_API = PLATFORM + ".api";
  private static final String CORE = INTERNAL + ".core";
  private static final String CORE_BASE = CORE + ".base";
  private static final String CORE_SCHEDULE = CORE + ".schedule";
  private static final String BRIDGE = INTERNAL + ".bridge";
  private static final String MIXIN = BRIDGE + ".mixin";
  private static final String EXTENSION = INTERNAL + ".extension";
  private static final String MINECRAFT_EXTENSION = EXTENSION + ".minecraft";
  private static final String SABLE_EXTENSION = EXTENSION + ".sable";
  private static final String LOGIC = INTERNAL + ".logic";
  private static final String LOGIC_BASE = LOGIC + ".base";
  private static final String LOGIC_BASE_CAMERA = LOGIC_BASE + ".camera";
  private static final String LOGIC_BASE_PIVOT = LOGIC_BASE + ".pivot";
  private static final String LOGIC_SCHEDULER = LOGIC + ".scheduler";
  private static final String UTILS = INTERNAL + ".utils";
  private static final String MOD_ENTRYPOINT = LOGIC + ".ModEntrypoint";
  private static final String MINECRAFT_CAMERA_COLLISION =
      LOGIC_BASE_CAMERA + ".MinecraftCameraCollision";

  private static final Set<String> BASE_CATEGORIES =
      Set.of("camera", "math", "pivot", "rotation");
  private static final Set<String> SCHEDULER_CATEGORIES =
      Set.of("aiming", "camera", "config", "hud", "input", "rotation", "sound", "state");
  private static final Set<String> MIXIN_CATEGORIES =
      Set.of("hud", "input", "interaction", "lifecycle", "render", "sound");
  private static final Set<String> LEGACY_PACKAGES =
      Set.of(
          INTERNAL + ".base",
          INTERNAL + ".scheduler",
          INTERNAL + ".application",
          INTERNAL + ".integration",
          INTERNAL + ".persistence",
          INTERNAL + ".configscreen",
          INTERNAL + ".bootstrap");
  private static final Set<String> ALLOWED_SCHEDULER_BASE_TYPES =
      Set.of(
          CORE_BASE + ".BaseParameters",
          CORE_BASE + ".RaycastOrigin",
          CORE_BASE + ".ThirdPersonBase",
          CORE_BASE + ".camera.CameraProfile",
          CORE_BASE + ".camera.CameraSmoothingParameters",
          CORE_BASE + ".pivot.CameraPivotSmoothing",
          CORE_BASE + ".rotation.LookRotation",
          CORE_BASE + ".rotation.PlayerRotationMode",
          CORE_BASE + ".rotation.PlayerRotationParameters",
          CORE_BASE + ".rotation.PlayerRotationSmoothing");

  private static final JavaClasses INTERNAL_CLASSES =
      new ClassFileImporter()
          .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
          .importPackages(INTERNAL);

  @Test
  void packagesStayWithinApprovedBoundaries() {
    assertNoClassesInPackageTrees("legacy business-layer packages", LEGACY_PACKAGES);
    assertOnlyDirectCategories(LOGIC_BASE, BASE_CATEGORIES, true);
    assertOnlyDirectCategories(CORE_BASE, BASE_CATEGORIES, true);
    assertOnlyDirectCategories(CORE_SCHEDULE, SCHEDULER_CATEGORIES, true);
    assertOnlyDirectCategories(LOGIC_SCHEDULER, SCHEDULER_CATEGORIES, true);
    assertOnlyDirectCategories(MIXIN, MIXIN_CATEGORIES, false);
  }

  @Test
  void coreDoesNotDependOnMinecraftOrImplementationLayers() {
    assertNoDependencies(
        "core",
        source -> isInPackageTree(source, CORE),
        "Minecraft, bridge, extension, logic, or platform",
        target ->
            isInPackageTree(target, "net.minecraft")
                || isInPackageTree(target, BRIDGE)
                || isInPackageTree(target, EXTENSION)
                || isInPackageTree(target, LOGIC)
                || isInPackageTree(target, PLATFORM));
  }

  @Test
  void baseAndSchedulerFollowTheirDependencyDirection() {
    assertNoDependencies(
        "base logic",
        source -> isInPackageTree(source, LOGIC_BASE),
        "scheduling logic",
        target -> isInPackageTree(target, LOGIC_SCHEDULER));
    assertNoDependencies(
        "pivot strategies",
        source -> isInPackageTree(source, LOGIC_BASE_PIVOT),
        "camera implementations other than MinecraftCameraCollision",
        target ->
            isInPackageTree(target, LOGIC_BASE_CAMERA)
                && !target.getName().equals(MINECRAFT_CAMERA_COLLISION));
    assertNoDependencies(
        "scheduling logic",
        source -> isInPackageTree(source, LOGIC_SCHEDULER),
        "core base implementation types outside the approved boundary",
        target ->
            isInPackageTree(target, CORE_BASE)
                && !ALLOWED_SCHEDULER_BASE_TYPES.contains(target.getName()));
  }

  @Test
  void bridgeDependsOnlyOnAllowedProjectLayers() {
    assertNoDependencies(
        "bridge",
        source -> isInPackageTree(source, BRIDGE),
        "the public API or logic",
        target -> isInPackageTree(target, API) || isInPackageTree(target, LOGIC));
    assertNoDependencies(
        "bridge",
        source -> isInPackageTree(source, BRIDGE),
        "platform implementation",
        target ->
            isInPackageTree(target, PLATFORM) && !isInPackageTree(target, PLATFORM_API));
  }

  @Test
  void extensionsStayBehindTheirBoundaries() {
    assertNoDependencies(
        "extension points",
        source ->
            isInPackageTree(source, EXTENSION)
                && !isInPackageTree(source, MINECRAFT_EXTENSION)
                && !isInPackageTree(source, SABLE_EXTENSION),
        "bridge, logic, or platform implementation",
        target ->
            isInPackageTree(target, BRIDGE)
                || isInPackageTree(target, LOGIC)
                || isInPackageTree(target, PLATFORM));
    assertNoDependencies(
        "concrete extension implementations",
        source ->
            isInPackageTree(source, MINECRAFT_EXTENSION)
                || isInPackageTree(source, SABLE_EXTENSION),
        "bridge or logic",
        target -> isInPackageTree(target, BRIDGE) || isInPackageTree(target, LOGIC));
    assertConcreteExtensionAccess(MINECRAFT_EXTENSION);
    assertConcreteExtensionAccess(SABLE_EXTENSION);
    assertNoDependencies(
        "classes outside Sable extensions",
        source -> !isInPackageTree(source, SABLE_EXTENSION),
        "the Sable API",
        target -> isInPackageTree(target, "dev.ryanhcode.sable"));
  }

  @Test
  void utilitiesStayBusinessNeutral() {
    assertNoDependencies(
        "utilities",
        source -> isInPackageTree(source, UTILS),
        "project code outside utilities",
        target ->
            isInPackageTree(target, PROJECT) && !isInPackageTree(target, UTILS));
  }

  private static void assertNoClassesInPackageTrees(
      String description, Set<String> forbiddenPackages) {
    classes()
        .should(
            new ArchCondition<>("not reside in " + description) {
              @Override
              public void check(JavaClass item, ConditionEvents events) {
                if (forbiddenPackages.stream().anyMatch(root -> isInPackageTree(item, root))) {
                  events.add(
                      SimpleConditionEvent.violated(
                          item, item.getName() + " resides in " + item.getPackageName()));
                }
              }
            })
        .check(INTERNAL_CLASSES);
  }

  private static void assertOnlyDirectCategories(
      String rootPackage, Set<String> categories, boolean allowRoot) {
    classes()
        .should(
            new ArchCondition<>("stay in approved packages below " + rootPackage) {
              @Override
              public void check(JavaClass item, ConditionEvents events) {
                if (isInPackageTree(item, rootPackage)
                    && !isApprovedDirectPackage(
                        item.getPackageName(), rootPackage, categories, allowRoot)) {
                  events.add(
                      SimpleConditionEvent.violated(
                          item,
                          item.getName()
                              + " resides outside the approved direct packages below "
                              + rootPackage));
                }
              }
            })
        .check(INTERNAL_CLASSES);
  }

  private static boolean isApprovedDirectPackage(
      String packageName, String rootPackage, Set<String> categories, boolean allowRoot) {
    return (allowRoot && packageName.equals(rootPackage))
        || categories.stream()
            .anyMatch(category -> packageName.equals(rootPackage + "." + category));
  }

  private static void assertConcreteExtensionAccess(String extensionPackage) {
    assertNoDependencies(
        "classes outside " + extensionPackage + " except ModEntrypoint",
        source ->
            !isInPackageTree(source, extensionPackage)
                && !source.getName().equals(MOD_ENTRYPOINT),
        extensionPackage,
        target -> isInPackageTree(target, extensionPackage));
  }

  private static void assertNoDependencies(
      String sourceDescription,
      Predicate<JavaClass> sourcePredicate,
      String targetDescription,
      Predicate<JavaClass> targetPredicate) {
    classes()
        .should(
            new ArchCondition<>(
                sourceDescription + " not depend on " + targetDescription) {
              @Override
              public void check(JavaClass item, ConditionEvents events) {
                if (!sourcePredicate.test(item)) {
                  return;
                }
                item.getDirectDependenciesFromSelf().stream()
                    .filter(dependency -> targetPredicate.test(dependency.getTargetClass()))
                    .forEach(
                        dependency ->
                            events.add(
                                SimpleConditionEvent.violated(
                                    item, dependency.getDescription())));
              }
            })
        .check(INTERNAL_CLASSES);
  }

  private static boolean isInPackageTree(JavaClass javaClass, String rootPackage) {
    return isInPackageTree(javaClass.getPackageName(), rootPackage);
  }

  private static boolean isInPackageTree(String packageName, String rootPackage) {
    return packageName.equals(rootPackage) || packageName.startsWith(rootPackage + ".");
  }
}
