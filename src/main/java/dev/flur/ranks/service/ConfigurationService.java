package dev.flur.ranks.service;

import dev.flur.ranks.service.config.TomlConfiguration;
import org.jetbrains.annotations.NotNull;

public interface ConfigurationService {
    @NotNull
    TomlConfiguration getConfiguration(@NotNull String fileName);

    void reloadConfigurations();

    void saveConfiguration(@NotNull String fileName, @NotNull TomlConfiguration config);

    boolean configExists(@NotNull String fileName);
}