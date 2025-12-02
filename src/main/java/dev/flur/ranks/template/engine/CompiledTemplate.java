package dev.flur.ranks.template.engine;

import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

/**
 * Wrapper for a compiled Pebble template with metadata.
 */
public record CompiledTemplate(
        @NotNull String sourcePath,
        @NotNull PebbleTemplate pebbleTemplate,
        @NotNull Instant compiledAt,
        @Nullable Instant sourceModifiedAt
) {

    /**
     * Checks if this compiled template is newer than the given source modification time.
     *
     * @param sourceModified The source file modification time
     * @return true if this template is still valid
     */
    public boolean isNewerThan(@Nullable Instant sourceModified) {
        if (sourceModified == null) {
            return true;
        }
        if (sourceModifiedAt == null) {
            return false;
        }
        return !sourceModifiedAt.isBefore(sourceModified);
    }

    /**
     * Checks if this template has expired based on TTL.
     *
     * @param ttlMillis Time-to-live in milliseconds, or -1 for infinite
     * @return true if the template has expired
     */
    public boolean hasExpired(long ttlMillis) {
        if (ttlMillis < 0) {
            return false;
        }
        long ageMillis = Instant.now().toEpochMilli() - compiledAt.toEpochMilli();
        return ageMillis > ttlMillis;
    }
}
