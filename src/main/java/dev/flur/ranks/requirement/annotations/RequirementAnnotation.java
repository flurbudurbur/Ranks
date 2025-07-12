package dev.flur.ranks.requirement.annotations;

import dev.flur.ranks.requirement.AnnotatedRequirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to define parameter validation rules for requirement classes.
 * <p>
 * This annotation should be applied to classes that extend {@link AnnotatedRequirement}
 * to automatically validate the minimum and maximum number of parameters required
 * for the requirement to function correctly.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * @RequirementParams(minimum = 1, maximum = 1, description = "Format: amount")
 * public class MoneyRequirement extends AnnotatedRequirement {
 *     // Implementation
 * }
 *
 * @RequirementParams(minimum = 2, description = "Format: block1 [block2 ...] amount")
 * public class BlockBreakRequirement extends AnnotatedRequirement {
 *     // Implementation
 * }
 * }</pre>
 *
 * <p>
 * The validation is performed automatically in the {@link AnnotatedRequirement} constructor,
 * throwing an {@link IllegalArgumentException} if the parameter count constraints are violated.
 * </p>
 *
 * @see AnnotatedRequirement
 * @see dev.flur.ranks.requirement.Requirement
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequirementAnnotation {

    /**
     * The minimum number of parameters required for this requirement.
     * <p>
     * If fewer parameters are provided than this minimum, an
     * {@link IllegalArgumentException} will be thrown during requirement creation.
     * </p>
     *
     * @return the minimum number of parameters required
     * @see #maximum()
     */
    int minimum() default 1;

    /**
     * The maximum number of parameters allowed for this requirement.
     * <p>
     * If more parameters are provided than this maximum, an
     * {@link IllegalArgumentException} will be thrown during requirement creation.
     * </p>
     * <p>
     * Defaults to {@link Integer#MAX_VALUE} to allow unlimited parameters.
     * </p>
     *
     * @return the maximum number of parameters allowed
     * @see #minimum()
     */
    int maximum() default Integer.MAX_VALUE;

    /**
     * A human-readable description of the expected parameter format.
     * <p>
     * This description is included in error messages when parameter validation fails,
     * helping users understand the correct format for the requirement.
     * </p>
     *
     * <h3>Recommended Format:</h3>
     * <ul>
     *   <li>Start with "Format: " for consistency</li>
     *   <li>Use parameter names that describe their purpose</li>
     *   <li>Use square brackets {@code []} for optional parameters</li>
     *   <li>Use ellipsis {@code ...} for repeatable parameters</li>
     * </ul>
     *
     * <h3>Examples:</h3>
     * <ul>
     *   <li>{@code "Format: amount"} - Single required parameter</li>
     *   <li>{@code "Format: block1 [block2 ...] amount"} - Multiple blocks with required amount</li>
     *   <li>{@code "Format: item1 [item2 ...] quantity"} - Multiple items with required quantity</li>
     * </ul>
     *
     * @return a description of the expected parameter format
     */
    String usage() default "";

    /**
     * The name/identifier for this requirement type.
     * <p>
     * This name will be used to identify the requirement in configuration files,
     * commands, and other user-facing interfaces.
     * </p>
     * <p>
     * The name should be:
     * <ul>
     *   <li>Lowercase with hyphens for multi-word names (e.g., "money", "xp-level")</li>
     *   <li>Unique across all requirements</li>
     *   <li>Descriptive and easy to understand</li>
     * </ul>
     * </p>
     *
     * @return the name/identifier for this requirement type
     */
    String name();
}