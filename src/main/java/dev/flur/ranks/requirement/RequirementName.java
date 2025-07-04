
package dev.flur.ranks.requirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify the name/identifier for a requirement class.
 * <p>
 * This annotation should be applied to classes that extend {@link AnnotatedRequirement}
 * to automatically register them in the {@link RequirementRegistry} with the specified name.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * @RequirementName("money")
 * @RequirementParams(minimum = 1, maximum = 1, description = "Format: amount")
 * public class MoneyRequirement extends AnnotatedRequirement {
 *     // Implementation
 * }
 * }</pre>
 *
 * @see RequirementRegistry
 * @see AnnotatedRequirement
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequirementName {

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
    String value();
}