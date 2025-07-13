package dev.flur.ranks.service.config;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * TOML configuration wrapper that provides similar functionality to Bukkit's FileConfiguration
 */
public class TomlConfiguration {
    private Toml toml;
    private final File file;
    private final Map<String, Object> data;

    public TomlConfiguration(@NotNull File file) {
        this.file = file;
        if (file.exists()) {
            this.toml = new Toml().read(file);
            this.data = toml.toMap();
        } else {
            this.data = new HashMap<>();
        }
    }

    public void save() throws IOException {
        TomlWriter writer = new TomlWriter();
        try (FileWriter fileWriter = new FileWriter(file)) {
            writer.write(data, fileWriter);
        }
    }

    public void save(@NotNull File file) throws IOException {
        TomlWriter writer = new TomlWriter();
        try (FileWriter fileWriter = new FileWriter(file)) {
            writer.write(data, fileWriter);
        }
    }

    // Configuration value getters
    @Nullable
    public Object get(@NotNull String path) {
        return getValueByPath(data, path);
    }

    @Nullable
    public String getString(@NotNull String path) {
        Object value = get(path);
        return value != null ? value.toString() : null;
    }

    @Nullable
    public String getString(@NotNull String path, @Nullable String defaultValue) {
        String value = getString(path);
        return value != null ? value : defaultValue;
    }

    public int getInt(@NotNull String path) {
        Object value = get(path);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }

    public int getInt(@NotNull String path, int defaultValue) {
        Object value = get(path);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    public boolean getBoolean(@NotNull String path) {
        Object value = get(path);
        return value instanceof Boolean ? (Boolean) value : false;
    }

    public boolean getBoolean(@NotNull String path, boolean defaultValue) {
        Object value = get(path);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }

    public double getDouble(@NotNull String path) {
        Object value = get(path);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }

    public double getDouble(@NotNull String path, double defaultValue) {
        Object value = get(path);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public List<String> getStringList(@NotNull String path) {
        Object value = get(path);
        return value instanceof List ? (List<String>) value : null;
    }

    // Configuration value setters
    public void set(@NotNull String path, @Nullable Object value) {
        setValueByPath(data, path, value);
    }

    // Utility methods
    public boolean contains(@NotNull String path) {
        return get(path) != null;
    }

    @NotNull
    public Set<String> getKeys(boolean deep) {
        return deep ? getAllKeys(data, "") : data.keySet();
    }

    // Helper methods for nested path access
    @SuppressWarnings("unchecked")
    private @Nullable Object getValueByPath(Map<String, Object> map, @NotNull String path) {
        String[] keys = path.split("\\.");
        Object current = map;

        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(key);
            } else {
                return null;
            }
        }

        return current;
    }

    @SuppressWarnings("unchecked")
    private void setValueByPath(Map<String, Object> map, @NotNull String path, Object value) {
        String[] keys = path.split("\\.");
        Map<String, Object> current = map;

        for (int i = 0; i < keys.length - 1; i++) {
            String key = keys[i];
            if (!current.containsKey(key) || !(current.get(key) instanceof Map)) {
                current.put(key, new HashMap<String, Object>());
            }
            current = (Map<String, Object>) current.get(key);
        }

        current.put(keys[keys.length - 1], value);
    }

    private @NotNull Set<String> getAllKeys(@NotNull Map<String, Object> map, String prefix) {
        Set<String> keys = new HashSet<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            keys.add(key);

            if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) entry.getValue();
                keys.addAll(getAllKeys(nestedMap, key));
            }
        }
        return keys;
    }
}