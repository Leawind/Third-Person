package io.github.leawind.thirdperson.internal.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonStateStoreTest {
  @TempDir Path temporaryDirectory;

  @Test
  void savesAtomicallyAndLoadsTheState() throws Exception {
    Path path = temporaryDirectory.resolve("nested/leawind_third_person.json");
    var store = new JsonStateStore();

    store.save(path, ThirdPersonPersistentState.defaults());

    assertEquals(ThirdPersonPersistentState.defaults(), store.load(path));
    try (var children = Files.list(path.getParent())) {
      assertFalse(children.anyMatch(child -> child.getFileName().toString().endsWith(".tmp")));
    }
  }
}
