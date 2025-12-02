package dev.flur.ranks.template.error;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Exception thrown when template processing fails.
 */
public class TemplateException extends RuntimeException {

    private final TemplateErrorType errorType;
    private final String templatePath;
    private final int lineNumber;
    private final int columnNumber;

    public TemplateException(@NotNull TemplateErrorType errorType,
                             @NotNull String message) {
        this(errorType, message, null, -1, -1, null);
    }

    public TemplateException(@NotNull TemplateErrorType errorType,
                             @NotNull String message,
                             @Nullable Throwable cause) {
        this(errorType, message, null, -1, -1, cause);
    }

    public TemplateException(@NotNull TemplateErrorType errorType,
                             @NotNull String message,
                             @Nullable String templatePath,
                             int lineNumber,
                             int columnNumber,
                             @Nullable Throwable cause) {
        super(formatMessage(errorType, message, templatePath, lineNumber, columnNumber), cause);
        this.errorType = errorType;
        this.templatePath = templatePath;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    private static String formatMessage(TemplateErrorType errorType,
                                         String message,
                                         String templatePath,
                                         int lineNumber,
                                         int columnNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(errorType.name()).append("] ");
        sb.append(message);

        if (templatePath != null) {
            sb.append(" (in ").append(templatePath);
            if (lineNumber > 0) {
                sb.append(" at line ").append(lineNumber);
                if (columnNumber > 0) {
                    sb.append(", column ").append(columnNumber);
                }
            }
            sb.append(")");
        }

        return sb.toString();
    }

    /**
     * Gets the type of template error.
     *
     * @return The error type
     */
    @NotNull
    public TemplateErrorType getErrorType() {
        return errorType;
    }

    /**
     * Gets the path of the template that caused the error.
     *
     * @return Template path or null if inline template
     */
    @Nullable
    public String getTemplatePath() {
        return templatePath;
    }

    /**
     * Gets the line number where the error occurred.
     *
     * @return Line number or -1 if unknown
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Gets the column number where the error occurred.
     *
     * @return Column number or -1 if unknown
     */
    public int getColumnNumber() {
        return columnNumber;
    }
}
