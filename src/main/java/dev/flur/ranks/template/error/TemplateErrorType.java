package dev.flur.ranks.template.error;

/**
 * Types of template errors that can occur during processing.
 */
public enum TemplateErrorType {

    /**
     * Pebble syntax error (unclosed tags, invalid syntax).
     */
    SYNTAX_ERROR("Pebble syntax error"),

    /**
     * Undefined variable referenced in template.
     */
    UNDEFINED_VARIABLE("Undefined variable referenced"),

    /**
     * Unknown filter used in template.
     */
    UNDEFINED_FILTER("Unknown filter"),

    /**
     * Unknown function used in template.
     */
    UNDEFINED_FUNCTION("Unknown function"),

    /**
     * MiniMessage parsing error.
     */
    MINIMESSAGE_ERROR("MiniMessage parsing error"),

    /**
     * Template file not found.
     */
    FILE_NOT_FOUND("Template file not found"),

    /**
     * Circular template inheritance detected.
     */
    CIRCULAR_INHERITANCE("Circular template inheritance detected"),

    /**
     * Referenced macro not found.
     */
    MACRO_NOT_FOUND("Referenced macro not found"),

    /**
     * IO error reading template.
     */
    IO_ERROR("Error reading template"),

    /**
     * General evaluation error.
     */
    EVALUATION_ERROR("Template evaluation error");

    private final String description;

    TemplateErrorType(String description) {
        this.description = description;
    }

    /**
     * Gets a human-readable description of this error type.
     *
     * @return Error description
     */
    public String getDescription() {
        return description;
    }
}
