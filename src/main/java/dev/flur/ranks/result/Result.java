package dev.flur.ranks.result;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A generic result class that can represent either a success or a failure.
 * This is used to avoid throwing exceptions and to provide more context about failures.
 *
 * @param <T> The type of the success value
 */
public class Result<T> {

    private final T value;
    private final String errorMessage;
    private final boolean success;

    private Result(T value, String errorMessage, boolean success) {
        this.value = value;
        this.errorMessage = errorMessage;
        this.success = success;
    }

    /**
     * Creates a successful result with the given value.
     *
     * @param value The success value
     * @param <T>   The type of the success value
     * @return A successful result
     */
    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull Result<T> success(@NotNull T value) {
        return new Result<>(value, null, true);
    }

    /**
     * Creates a failure result with the given error message.
     *
     * @param errorMessage The error message
     * @param <T>          The type of the success value
     * @return A failure result
     */
    @Contract(value = "_ -> new", pure = true)
    public static <T> @NotNull Result<T> failure(@NotNull String errorMessage) {
        return new Result<>(null, errorMessage, false);
    }

    /**
     * Checks if this result is a success.
     *
     * @return True if this result is a success, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Checks if this result is a failure.
     *
     * @return True if this result is a failure, false otherwise
     */
    public boolean isFailure() {
        return !success;
    }

    /**
     * Gets the success value.
     *
     * @return The success value, or null if this result is a failure
     */
    @Nullable
    public T getValue() {
        return value;
    }

    /**
     * Gets the error message.
     *
     * @return The error message, or null if this result is a success
     */
    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Executes the given action if this result is a success.
     *
     * @param action The action to execute
     * @return This result
     */
    public Result<T> onSuccess(Consumer<T> action) {
        if (success) {
            action.accept(value);
        }
        return this;
    }

    /**
     * Executes the given action if this result is a failure.
     *
     * @param action The action to execute
     * @return This result
     */
    public Result<T> onFailure(Consumer<String> action) {
        if (!success) {
            action.accept(errorMessage);
        }
        return this;
    }

    /**
     * Maps the success value to a new value.
     *
     * @param mapper The mapper function
     * @param <R>    The type of the new success value
     * @return A new result with the mapped value, or a failure if this result is a failure
     */
    public <R> Result<R> map(Function<T, R> mapper) {
        if (success) {
            return Result.success(mapper.apply(value));
        } else {
            return Result.failure(errorMessage);
        }
    }

    /**
     * Flat maps the success value to a new result.
     *
     * @param mapper The mapper function
     * @param <R>    The type of the new success value
     * @return The new result, or a failure if this result is a failure
     */
    public <R> Result<R> flatMap(Function<T, Result<R>> mapper) {
        if (success) {
            return mapper.apply(value);
        } else {
            return Result.failure(errorMessage);
        }
    }
}