package io.github.leawind.thirdperson.internal.integration.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonConfigStoreTest {
  @TempDir Path temporaryDirectory;

  @Test
  void savesAtomicallyAndLoadsTheSnapshot() throws Exception {
    Path path = temporaryDirectory.resolve("nested/leawind_third_person.json");
    var store = new JsonConfigStore();

    store.save(path, ThirdPersonConfig.defaults());

    assertEquals(ThirdPersonConfig.defaults(), store.load(path));
    try (var children = Files.list(path.getParent())) {
      assertFalse(children.anyMatch(child -> child.getFileName().toString().endsWith(".tmp")));
    }
  }
}
