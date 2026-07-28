package io.github.leawind.thirdperson.internal.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

final class JsonStateStore {
  ThirdPersonPersistentState load(Path path) throws IOException {
    return JsonStateCodec.decode(Files.readString(path, StandardCharsets.UTF_8));
  }

  void save(Path path, ThirdPersonPersistentState state) throws IOException {
    Path parent = path.toAbsolutePath().getParent();
    if (parent == null) {
      throw new IOException("State path has no parent: " + path);
    }
    Files.createDirectories(parent);
    Path temporary = Files.createTempFile(parent, path.getFileName().toString(), ".tmp");
    try {
      Files.writeString(temporary, JsonStateCodec.encode(state), StandardCharsets.UTF_8);
      try {
        Files.move(
            temporary,
            path,
            StandardCopyOption.ATOMIC_MOVE,
            StandardCopyOption.REPLACE_EXISTING);
      } catch (AtomicMoveNotSupportedException ignored) {
        Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
      }
    } finally {
      Files.deleteIfExists(temporary);
    }
  }
}
