package dev.flur.ranks.template.extension.filter;

import io.pebbletemplates.pebble.template.EvaluationContext;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CurrencyFilter.
 */
class CurrencyFilterTest {

    private CurrencyFilter filter;
    private PebbleTemplate mockTemplate;
    private EvaluationContext mockContext;

    @BeforeEach
    void setUp() {
        filter = new CurrencyFilter();
        mockTemplate = mock(PebbleTemplate.class);
        mockContext = mock(EvaluationContext.class);
    }

    private Object applyFilter(Object input, Map<String, Object> args) {
        return filter.apply(input, args, mockTemplate, mockContext, 1);
    }

    private Object applyFilter(Object input) {
        return applyFilter(input, new HashMap<>());
    }

    @Nested
    @DisplayName("Default Formatting")
    class DefaultFormatting {

        @Test
        @DisplayName("formats integer with default symbol")
        void apply_Integer_FormatsWithDefaultSymbol() {
            assertEquals("$1,234.00", applyFilter(1234));
        }

        @Test
        @DisplayName("formats double with two decimals by default")
        void apply_Double_FormatsWithDecimals() {
            assertEquals("$1,234.56", applyFilter(1234.56));
        }

        @Test
        @DisplayName("formats large numbers with grouping")
        void apply_LargeNumber_FormatsWithGrouping() {
            assertEquals("$1,234,567.89", applyFilter(1234567.89));
        }

        @Test
        @DisplayName("formats zero correctly")
        void apply_Zero_FormatsCorrectly() {
            assertEquals("$0.00", applyFilter(0));
        }

        @Test
        @DisplayName("formats negative numbers")
        void apply_Negative_FormatsCorrectly() {
            assertEquals("$-1,234.56", applyFilter(-1234.56));
        }
    }

    @Nested
    @DisplayName("Custom Symbol")
    class CustomSymbol {

        @Test
        @DisplayName("uses custom symbol from args")
        void apply_CustomSymbol_UsesSymbol() {
            Map<String, Object> args = Map.of("symbol", "€");
            assertEquals("€1,234.56", applyFilter(1234.56, args));
        }

        @Test
        @DisplayName("uses multi-character symbol")
        void apply_MultiCharSymbol_UsesSymbol() {
            Map<String, Object> args = Map.of("symbol", "EUR ");
            assertEquals("EUR 1,234.56", applyFilter(1234.56, args));
        }
    }

    @Nested
    @DisplayName("Custom Decimals")
    class CustomDecimals {

        @Test
        @DisplayName("formats with zero decimals")
        void apply_ZeroDecimals_RoundsNumber() {
            Map<String, Object> args = Map.of("decimals", 0);
            assertEquals("$1,235", applyFilter(1234.56, args));
        }

        @Test
        @DisplayName("formats with one decimal")
        void apply_OneDecimal_FormatsCorrectly() {
            Map<String, Object> args = Map.of("decimals", 1);
            assertEquals("$1,234.6", applyFilter(1234.56, args));
        }

        @Test
        @DisplayName("formats with three decimals")
        void apply_ThreeDecimals_FormatsCorrectly() {
            Map<String, Object> args = Map.of("decimals", 3);
            assertEquals("$1,234.560", applyFilter(1234.56, args));
        }

        @Test
        @DisplayName("combines custom symbol and decimals")
        void apply_CustomSymbolAndDecimals_FormatsCorrectly() {
            Map<String, Object> args = new HashMap<>();
            args.put("symbol", "€");
            args.put("decimals", 0);
            assertEquals("€1,235", applyFilter(1234.56, args));
        }
    }

    @Nested
    @DisplayName("Input Types")
    class InputTypes {

        @Test
        @DisplayName("handles null input")
        void apply_NullInput_ReturnsEmpty() {
            assertEquals("", applyFilter(null));
        }

        @Test
        @DisplayName("handles Integer input")
        void apply_IntegerInput_Formats() {
            assertEquals("$100.00", applyFilter(Integer.valueOf(100)));
        }

        @Test
        @DisplayName("handles Long input")
        void apply_LongInput_Formats() {
            assertEquals("$1,000,000.00", applyFilter(Long.valueOf(1000000)));
        }

        @Test
        @DisplayName("handles Float input")
        void apply_FloatInput_Formats() {
            assertEquals("$99.99", applyFilter(Float.valueOf(99.99f)));
        }

        @Test
        @DisplayName("parses String number")
        void apply_StringNumber_Parses() {
            assertEquals("$1,234.56", applyFilter("1234.56"));
        }

        @Test
        @DisplayName("returns original for invalid string")
        void apply_InvalidString_ReturnsOriginal() {
            assertEquals("not a number", applyFilter("not a number"));
        }
    }

    @Nested
    @DisplayName("Argument Names")
    class ArgumentNames {

        @Test
        @DisplayName("returns expected argument names")
        void getArgumentNames_ReturnsExpected() {
            List<String> args = filter.getArgumentNames();

            assertEquals(2, args.size());
            assertTrue(args.contains("symbol"));
            assertTrue(args.contains("decimals"));
        }
    }
}
