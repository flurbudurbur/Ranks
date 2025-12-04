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
 * Pebble filter that formats numbers as percentages.
 * <p>
 * Usage:
 * <ul>
 *   <li>{@code {{ 0.755 | pct }}} - "75.5%" (values 0-1 treated as ratios)</li>
 *   <li>{@code {{ 75.5 | pct }}} - "75.5%" (values > 1 treated as percentages)</li>
 *   <li>{@code {{ 100 | pct }}} - "100%"</li>
 * </ul>
 */
public class PercentFilter implements Filter {

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

        // If value is between 0 and 1 (exclusive), treat as a ratio
        if (value > 0 && value < 1) {
            value = value * 100;
        }

        return formatPercent(value);
    }

    @Override
    public List<String> getArgumentNames() {
        return List.of();
    }

    /**
     * Formats a number as a percentage.
     */
    private String formatPercent(double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat formatter;

        // Use one decimal place if not a whole number
        if (value == Math.floor(value)) {
            formatter = new DecimalFormat("#", symbols);
        } else {
            formatter = new DecimalFormat("#.#", symbols);
        }

        return formatter.format(value) + "%";
    }
}
