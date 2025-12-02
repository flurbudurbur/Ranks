package dev.flur.ranks.template.error;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of template validation with detailed error information.
 */
public final class TemplateValidationResult {

    private final boolean valid;
    private final String templatePath;
    private final List<TemplateIssue> issues;

    private TemplateValidationResult(boolean valid,
                                     @Nullable String templatePath,
                                     @NotNull List<TemplateIssue> issues) {
        this.valid = valid;
        this.templatePath = templatePath;
        this.issues = Collections.unmodifiableList(new ArrayList<>(issues));
    }

    /**
     * Creates a successful validation result.
     *
     * @param templatePath Path to the validated template
     * @return Successful validation result
     */
    public static TemplateValidationResult success(@Nullable String templatePath) {
        return new TemplateValidationResult(true, templatePath, Collections.emptyList());
    }

    /**
     * Creates a failed validation result with issues.
     *
     * @param templatePath Path to the validated template
     * @param issues       List of validation issues
     * @return Failed validation result
     */
    public static TemplateValidationResult failure(@Nullable String templatePath,
                                                   @NotNull List<TemplateIssue> issues) {
        return new TemplateValidationResult(false, templatePath, issues);
    }

    /**
     * Creates a failed validation result with a single issue.
     *
     * @param templatePath Path to the validated template
     * @param issue        The validation issue
     * @return Failed validation result
     */
    public static TemplateValidationResult failure(@Nullable String templatePath,
                                                   @NotNull TemplateIssue issue) {
        return new TemplateValidationResult(false, templatePath, Collections.singletonList(issue));
    }

    /**
     * Checks if the template is valid.
     *
     * @return true if valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Gets the path of the validated template.
     *
     * @return Template path or null for inline templates
     */
    @Nullable
    public String getTemplatePath() {
        return templatePath;
    }

    /**
     * Gets the list of validation issues.
     *
     * @return Unmodifiable list of issues
     */
    @NotNull
    public List<TemplateIssue> getIssues() {
        return issues;
    }

    /**
     * Gets a formatted error message summarizing all issues.
     *
     * @return Formatted error message
     */
    @NotNull
    public String getFormattedErrors() {
        if (valid) {
            return "No errors";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Template validation failed");
        if (templatePath != null) {
            sb.append(" for '").append(templatePath).append("'");
        }
        sb.append(":\n");

        for (int i = 0; i < issues.size(); i++) {
            TemplateIssue issue = issues.get(i);
            sb.append("  ").append(i + 1).append(". ");
            sb.append(issue.getFormattedMessage());
            if (i < issues.size() - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Represents a single validation issue.
     */
    public static final class TemplateIssue {

        private final TemplateErrorType type;
        private final String message;
        private final int lineNumber;
        private final int columnNumber;
        private final String lineContent;

        public TemplateIssue(@NotNull TemplateErrorType type,
                             @NotNull String message,
                             int lineNumber,
                             int columnNumber,
                             @Nullable String lineContent) {
            this.type = type;
            this.message = message;
            this.lineNumber = lineNumber;
            this.columnNumber = columnNumber;
            this.lineContent = lineContent;
        }

        public TemplateIssue(@NotNull TemplateErrorType type, @NotNull String message) {
            this(type, message, -1, -1, null);
        }

        @NotNull
        public TemplateErrorType getType() {
            return type;
        }

        @NotNull
        public String getMessage() {
            return message;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public int getColumnNumber() {
            return columnNumber;
        }

        @Nullable
        public String getLineContent() {
            return lineContent;
        }

        /**
         * Gets a formatted message for this issue.
         *
         * @return Formatted message
         */
        @NotNull
        public String getFormattedMessage() {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(type.name()).append("] ").append(message);

            if (lineNumber > 0) {
                sb.append(" (line ").append(lineNumber);
                if (columnNumber > 0) {
                    sb.append(", column ").append(columnNumber);
                }
                sb.append(")");
            }

            if (lineContent != null && !lineContent.isEmpty()) {
                sb.append("\n    > ").append(lineContent.trim());
                if (columnNumber > 0) {
                    sb.append("\n    > ");
                    sb.append(" ".repeat(Math.max(0, columnNumber - 1)));
                    sb.append("^");
                }
            }

            return sb.toString();
        }
    }
}
