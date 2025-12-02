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
 * Pebble filter that formats numbers as currency.
 * <p>
 * Usage:
 * <ul>
 *   <li>{@code {{ amount | currency }}} - Format with default symbol: "$1,234.56"</li>
 *   <li>{@code {{ amount | currency('€') }}} - Format with custom symbol: "€1,234.56"</li>
 *   <li>{@code {{ amount | currency('$', 0) }}} - No decimals: "$1,235"</li>
 * </ul>
 */
public class CurrencyFilter implements Filter {

    private static final String DEFAULT_SYMBOL = "$";
    private static final int DEFAULT_DECIMALS = 2;

    @Override
    public Object apply(Object input, Map<String, Object> args, PebbleTemplate self,
                        EvaluationContext context, int lineNumber) {
        if (input == null) {
            return "";
        }

        double amount;
        if (input instanceof Number number) {
            amount = number.doubleValue();
        } else {
            try {
                amount = Double.parseDouble(input.toString());
            } catch (NumberFormatException e) {
                return input.toString();
            }
        }

        // Get symbol
        String symbol = DEFAULT_SYMBOL;
        if (args.containsKey("symbol")) {
            symbol = String.valueOf(args.get("symbol"));
        }

        // Get decimals
        int decimals = DEFAULT_DECIMALS;
        if (args.containsKey("decimals")) {
            Object decimalsArg = args.get("decimals");
            if (decimalsArg instanceof Number) {
                decimals = ((Number) decimalsArg).intValue();
            }
        }

        return formatCurrency(amount, symbol, decimals);
    }

    @Override
    public List<String> getArgumentNames() {
        return List.of("symbol", "decimals");
    }

    /**
     * Formats an amount as currency.
     */
    private String formatCurrency(double amount, String symbol, int decimals) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        StringBuilder pattern = new StringBuilder("#,##0");
        if (decimals > 0) {
            pattern.append(".");
            pattern.append("0".repeat(decimals));
        }

        DecimalFormat formatter = new DecimalFormat(pattern.toString(), symbols);
        return symbol + formatter.format(amount);
    }
}
