package io.github.leawind.thirdperson.internal.logic.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InteractionRaycastGeometryTest {
  @Test
  void candidateRangeIncludesCameraToEyeDistance() {
    assertEquals(8.5, InteractionRaycastGeometry.candidateRange(4.5, 3.0, 4.0));
    assertEquals(10.0, InteractionRaycastGeometry.candidateRange(4.5, 6.0, 4.0));
  }

  @Test
  void playerEyeOriginDoesNotExtendCandidateRange() {
    assertEquals(4.5, InteractionRaycastGeometry.candidateRange(4.5, 3.0, 0.0));
    assertEquals(6.0, InteractionRaycastGeometry.candidateRange(4.5, 6.0, 0.0));
  }

  @Test
  void candidateRangeRejectsInvalidInputs() {
    assertTrue(Double.isNaN(InteractionRaycastGeometry.candidateRange(-1.0, 3.0, 4.0)));
    assertTrue(
        Double.isNaN(
            InteractionRaycastGeometry.candidateRange(
                4.5, Double.POSITIVE_INFINITY, 4.0)));
  }

  @Test
  void rangeCheckMatchesVanillaStrictBoundary() {
    assertTrue(InteractionRaycastGeometry.isWithinRange(15.999, 4.0));
    assertFalse(InteractionRaycastGeometry.isWithinRange(16.0, 4.0));
    assertFalse(InteractionRaycastGeometry.isWithinRange(16.001, 4.0));
  }

  @Test
  void attackCandidateRangeIncludesMovementAndDisplacedOrigin() {
    assertEquals(8.75, InteractionRaycastGeometry.attackCandidateRange(4.5, 0.25, 4.0));
    assertEquals(4.5, InteractionRaycastGeometry.attackCandidateRange(4.5, 0.0, 0.0));
  }

  @Test
  void attackCandidateRangeRejectsInvalidInputs() {
    assertTrue(
        Double.isNaN(InteractionRaycastGeometry.attackCandidateRange(4.5, -0.1, 0.0)));
    assertTrue(
        Double.isNaN(
            InteractionRaycastGeometry.attackCandidateRange(
                Double.POSITIVE_INFINITY, 0.0, 0.0)));
  }

  @Test
  void attackRangeIncludesHitboxMarginAndForwardMovement() {
    assertTrue(
        InteractionRaycastGeometry.isWithinAttackRange(
            1.75 * 1.75, 2.0, 4.5, 0.25, 0.5));
    assertTrue(
        InteractionRaycastGeometry.isWithinAttackRange(
            5.25 * 5.25, 2.0, 4.5, 0.25, 0.5));
    assertFalse(
        InteractionRaycastGeometry.isWithinAttackRange(
            1.749 * 1.749, 2.0, 4.5, 0.25, 0.5));
    assertFalse(
        InteractionRaycastGeometry.isWithinAttackRange(
            5.251 * 5.251, 2.0, 4.5, 0.25, 0.5));
  }

  @Test
  void displacedOriginExtendsDiscoveryButNotAttackEligibility() {
    assertEquals(8.5, InteractionRaycastGeometry.attackCandidateRange(4.5, 0.0, 4.0));
    assertFalse(
        InteractionRaycastGeometry.isWithinAttackRange(
            8.0 * 8.0, 0.0, 4.5, 0.25, 0.0));
  }

  @Test
  void attackRangeRejectsInvalidConfiguration() {
    assertFalse(InteractionRaycastGeometry.isWithinAttackRange(4.0, 5.0, 4.0, 0.0, 0.0));
    assertFalse(InteractionRaycastGeometry.isWithinAttackRange(4.0, 0.0, 4.0, -0.1, 0.0));
    assertFalse(
        InteractionRaycastGeometry.isWithinAttackRange(
            Double.POSITIVE_INFINITY, 0.0, 4.0, 0.0, 0.0));
  }
}
