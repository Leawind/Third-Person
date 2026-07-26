package io.github.leawind.thirdperson.internal.integration.config;

import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

final class JsonConfigStore {
  ThirdPersonConfig load(Path path) throws IOException {
    return JsonConfigCodec.decode(Files.readString(path, StandardCharsets.UTF_8));
  }

  void save(Path path, ThirdPersonConfig config) throws IOException {
    Path parent = path.toAbsolutePath().getParent();
    if (parent == null) {
      throw new IOException("Config path has no parent: " + path);
    }
    Files.createDirectories(parent);
    Path temporary = Files.createTempFile(parent, path.getFileName().toString(), ".tmp");
    try {
      Files.writeString(temporary, JsonConfigCodec.encode(config), StandardCharsets.UTF_8);
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
