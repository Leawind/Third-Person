package io.github.leawind.thirdperson.internal.bridge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.bridge.events.CameraEntityOpacityEvent;
import org.junit.jupiter.api.Test;

class CameraEntityRenderStateTest {
  @Test
  void appliesTheCameraOpacityOnlyInsideItsRenderScope() {
    CameraEntityOpacityEvent.register(partialTick -> 0.5f);

    assertFalse(CameraEntityRenderState.begin(1.0f));
    assertTrue(CameraEntityRenderState.isApplyingTransparency());
    assertEquals(0.5f, CameraEntityRenderState.applyOpacity(0.75f));
    assertEquals(0x7f336699, CameraEntityRenderState.applyOpacity(0xff336699));

    CameraEntityRenderState.end();
    assertFalse(CameraEntityRenderState.isApplyingTransparency());
    assertEquals(0xff336699, CameraEntityRenderState.applyOpacity(0xff336699));

    CameraEntityOpacityEvent.register(partialTick -> 0.0f);
    assertTrue(CameraEntityRenderState.begin(1.0f));
    CameraEntityRenderState.end();
  }
}
