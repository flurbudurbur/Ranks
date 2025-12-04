package dev.flur.ranks.template.extension.filter;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.util.List;
import java.util.Map;

/**
 * Pebble filter that formats numbers as whole numbers (no decimals).
 * <p>
 * Usage:
 * <ul>
 *   <li>{@code {{ 1234.56 | whole }}} - "1235" (rounded)</li>
 *   <li>{@code {{ 999.4 | whole }}} - "999"</li>
 *   <li>{@code {{ 999.5 | whole }}} - "1000"</li>
 * </ul>
 */
public class WholeFilter implements Filter {

    @Override
    public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
                        EvaluationContext context, int lineNumber) {
        if (input == null) {
            return "";
        }

        double value;
        if (input instanceof Number number) {
            value = number.doubleValue();
        } else {
            try {
                value = Double.parseDouble(input.toString());
            } catch (NumberFormatException e) {
                return input.toString();
            }
        }

        return String.valueOf(Math.round(value));
    }

    @Override
    public List<String> getArgumentNames() {
        return List.of();
    }
}
