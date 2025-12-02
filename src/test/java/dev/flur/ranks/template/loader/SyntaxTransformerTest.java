package dev.flur.ranks.template.loader;

import dev.flur.ranks.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SyntaxTransformer.
 */
class SyntaxTransformerTest {

    private TemplateLoader mockDelegate;
    private SyntaxTransformer transformer;

    @BeforeEach
    void setUp() {
        mockDelegate = mock(TemplateLoader.class);
        transformer = new SyntaxTransformer(mockDelegate);
    }

    @Nested
    @DisplayName("Transform Variable Syntax")
    class VariableSyntax {

        @Test
        @DisplayName("transforms << var >> to {{ var }}")
        void transform_SimpleVariable() {
            String input = "Hello << name >>!";
            String expected = "Hello {{ name }}!";

            assertEquals(expected, transformer.transform(input));
        }

        @Test
        @DisplayName("transforms variable with extra whitespace")
        void transform_VariableWithWhitespace() {
            String input = "Hello <<   name   >>!";
            String expected = "Hello {{ name }}!";

            assertEquals(expected, transformer.transform(input));
        }

        @Test
        @DisplayName("transforms multiple variables")
        void transform_MultipleVariables() {
            String input = "Hello << firstName >> << lastName >>!";
            String expected = "Hello {{ firstName }} {{ lastName }}!";

            assertEquals(expected, transformer.transform(input));
        }

        @Test
        @DisplayName("transforms variable with filter")
        void transform_VariableWithFilter() {
            String input = "<< amount | currency >>";
            String expected = "{{ amount | currency }}";

            assertEquals(expected, transformer.transform(input));
        }
    }

    @Nested
    @DisplayName("Transform Tag Syntax")
    class TagSyntax {

        @Test
        @DisplayName("transforms <[ if ]> to {% if %}")
        void transform_IfTag() {
            String input = "<[ if condition ]>content<[ endif ]>";
            String expected = "{% if condition %}content{% endif %}";

            assertEquals(expected, transformer.transform(input));
        }

        @Test
        @DisplayName("transforms for loop")
        void transform_ForLoop() {
            String input = "<[ for item in items ]><< item >><[ endfor ]>";
            String expected = "{% for item in items %}{{ item }}{% endfor %}";

            assertEquals(expected, transformer.transform(input));
        }

        @Test
        @DisplayName("transforms tag with extra whitespace")
        void transform_TagWithWhitespace() {
            String input = "<[   if x   ]>";
            String expected = "{% if x %}";

            assertEquals(expected, transformer.transform(input));
        }
    }

    @Nested
    @DisplayName("Transform Comment Syntax")
    class CommentSyntax {

        @Test
        @DisplayName("transforms <# comment #> to {# comment #}")
        void transform_Comment() {
            String input = "<# This is a comment #>";
            String expected = "{# This is a comment #}";

            assertEquals(expected, transformer.transform(input));
        }

        @Test
        @DisplayName("transforms comment with extra whitespace")
        void transform_CommentWithWhitespace() {
            String input = "<#   comment   #>";
            String expected = "{# comment #}";

            assertEquals(expected, transformer.transform(input));
        }
    }

    @Nested
    @DisplayName("Mixed Syntax")
    class MixedSyntax {

        @Test
        @DisplayName("transforms mixed variable, tag and comment")
        void transform_MixedSyntax() {
            String input = "<# Header #><[ if show ]>Hello << name >>!<[ endif ]>";
            String expected = "{# Header #}{% if show %}Hello {{ name }}!{% endif %}";

            assertEquals(expected, transformer.transform(input));
        }

        @Test
        @DisplayName("preserves MiniMessage tags")
        void transform_PreservesMiniMessage() {
            String input = "<gold>Hello << name >>!</gold>";
            String expected = "<gold>Hello {{ name }}!</gold>";

            assertEquals(expected, transformer.transform(input));
        }

        @Test
        @DisplayName("preserves complex MiniMessage formatting")
        void transform_PreservesComplexMiniMessage() {
            String input = "<hover:show_text:'<gray>Click!</gray>'><click:run_command:'/rank'><gold>Rank up!</gold></click></hover>";
            String expected = "<hover:show_text:'<gray>Click!</gray>'><click:run_command:'/rank'><gold>Rank up!</gold></click></hover>";

            assertEquals(expected, transformer.transform(input));
        }

        @Test
        @DisplayName("handles empty input")
        void transform_EmptyInput() {
            assertEquals("", transformer.transform(""));
        }

        @Test
        @DisplayName("handles input with no special syntax")
        void transform_NoSpecialSyntax() {
            String input = "Plain text message";
            assertEquals(input, transformer.transform(input));
        }
    }

    @Nested
    @DisplayName("Delegate Methods")
    class DelegateMethods {

        @Test
        @DisplayName("getContent delegates and transforms")
        void getContent_DelegatesAndTransforms() {
            String templateName = "test.peb";
            String rawContent = "Hello << name >>!";
            when(mockDelegate.getContent(templateName)).thenReturn(Result.success(rawContent));

            Result<String> result = transformer.getContent(templateName);

            assertTrue(result.isSuccess());
            assertEquals("Hello {{ name }}!", result.getValue());
            verify(mockDelegate).getContent(templateName);
        }

        @Test
        @DisplayName("getContent propagates failure")
        void getContent_PropagatesFailure() {
            String templateName = "missing.peb";
            when(mockDelegate.getContent(templateName)).thenReturn(Result.failure("Not found"));

            Result<String> result = transformer.getContent(templateName);

            assertTrue(result.isFailure());
            assertEquals("Not found", result.getErrorMessage());
        }

        @Test
        @DisplayName("exists delegates to underlying loader")
        void exists_Delegates() {
            String templateName = "test.peb";
            when(mockDelegate.exists(templateName)).thenReturn(true);

            assertTrue(transformer.exists(templateName));
            verify(mockDelegate).exists(templateName);
        }

        @Test
        @DisplayName("getLastModified delegates to underlying loader")
        void getLastModified_Delegates() {
            String templateName = "test.peb";
            Instant expected = Instant.now();
            when(mockDelegate.getLastModified(templateName)).thenReturn(expected);

            assertEquals(expected, transformer.getLastModified(templateName));
            verify(mockDelegate).getLastModified(templateName);
        }

        @Test
        @DisplayName("createCacheKey delegates to underlying loader")
        void createCacheKey_Delegates() {
            String templateName = "test.peb";
            String expectedKey = "file://test.peb";
            when(mockDelegate.createCacheKey(templateName)).thenReturn(expectedKey);

            assertEquals(expectedKey, transformer.createCacheKey(templateName));
            verify(mockDelegate).createCacheKey(templateName);
        }
    }
}
