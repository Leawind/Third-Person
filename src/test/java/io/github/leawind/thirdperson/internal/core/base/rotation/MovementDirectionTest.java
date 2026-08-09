package io.github.leawind.thirdperson.internal.core.base.rotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

class MovementDirectionTest {
  @Test
  void sprintingImpulseAcceptsEveryDirectionAtTheSameMagnitude() {
    assertTrue(MovementDirection.hasDirectionalImpulse(0.0, 1.0, 0.8));
    assertTrue(MovementDirection.hasDirectionalImpulse(1.0, 0.0, 0.8));
    assertFalse(MovementDirection.hasDirectionalImpulse(0.5, 0.5, 0.8));
    assertFalse(MovementDirection.hasDirectionalImpulse(0.0, 0.0, 0.0));
  }

  @Test
  void pivotPlaneFollowsPitchAndRollWithoutUsingCameraPitch() {
    Quaternionf worldFromPivot = new Quaternionf().rotationX((float) Math.toRadians(90.0));
    Vector3f direction =
        MovementDirection.pivotPlaneWorld(0.0f, 1.0f, 0.0f, worldFromPivot).orElseThrow();

    assertEquals(0.0f, direction.x, 1.0e-6f);
    assertEquals(-1.0f, direction.y, 1.0e-6f);
    assertEquals(0.0f, direction.z, 1.0e-6f);
  }

  @Test
  void cameraSpaceMovementUsesTheFullCameraRotation() {
    Quaternionf worldFromCamera =
        new Quaternionf().rotationX((float) Math.toRadians(30.0));
    var rotation =
        MovementDirection.cameraSpaceWorld(0.0f, 1.0f, worldFromCamera)
            .flatMap(MovementDirection::facingRotation)
            .orElseThrow();

    assertEquals(0.0f, rotation.yawDegrees(), 1.0e-5f);
    assertEquals(30.0f, rotation.pitchDegrees(), 1.0e-5f);
  }
}
