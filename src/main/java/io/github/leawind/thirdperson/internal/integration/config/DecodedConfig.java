package io.github.leawind.thirdperson.internal.integration.config;

import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;

record DecodedConfig(ThirdPersonConfig config, boolean migrated) {}
