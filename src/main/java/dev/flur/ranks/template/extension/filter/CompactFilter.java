package dev.flur.ranks.template.extension.filter;

import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Pebble filter that formats numbers in compact/abbreviated form.
 * <p>
 * Usage:
 * <ul>
 *   <li>{@code {{ 1500 | compact }}} - "1.5K"</li>
 *   <li>{@code {{ 2300000 | compact }}} - "2.3M"</li>
 *   <li>{@code {{ 1000000000 | compact }}} - "1B"</li>
 *   <li>{@code {{ 500 | compact }}} - "500" (no suffix for small numbers)</li>
 * </ul>
 */
public class CompactFilter implements Filter {

    private static final long BILLION = 1_000_000_000L;
    private static final long MILLION = 1_000_000L;
    private static final long THOUSAND = 1_000L;

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

        return formatCompact(value);
    }

    @Override
    public List<String> getArgumentNames() {
        return List.of();
    }

    /**
     * Formats a number in compact form.
     */
    private String formatCompact(double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat oneDecimal = new DecimalFormat("#.#", symbols);
        DecimalFormat noDecimal = new DecimalFormat("#", symbols);

        double absValue = Math.abs(value);
        String prefix = value < 0 ? "-" : "";

        if (absValue >= BILLION) {
            double compact = absValue / BILLION;
            return prefix + (compact == Math.floor(compact)
                    ? noDecimal.format(compact)
                    : oneDecimal.format(compact)) + "B";
        } else if (absValue >= MILLION) {
            double compact = absValue / MILLION;
            return prefix + (compact == Math.floor(compact)
                    ? noDecimal.format(compact)
                    : oneDecimal.format(compact)) + "M";
        } else if (absValue >= THOUSAND) {
            double compact = absValue / THOUSAND;
            return prefix + (compact == Math.floor(compact)
                    ? noDecimal.format(compact)
                    : oneDecimal.format(compact)) + "K";
        } else {
            // For small numbers, show as-is with no decimals if whole
            if (absValue == Math.floor(absValue)) {
                return noDecimal.format(value);
            }
            return oneDecimal.format(value);
        }
    }
}
