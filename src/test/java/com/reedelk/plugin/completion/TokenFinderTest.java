package com.reedelk.plugin.completion;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TokenFinderTest {
    
    @Test
    void shouldCorrectlyFindLastTokenStartingWithTab() {
        // Given
        String text = "\tmess";

        // When
        Optional<String[]> actual = TokenFinder.findLastToken(text, 5);

        //  Then
        assertThat(actual).contains(new String[] {"mess"});
    }

    @Test
    void shouldCorrectlyFindLastTokenStartingWithWhiteSpaces() {
        // Given
        String text = "  messa";

        // When
        Optional<String[]> actual = TokenFinder.findLastToken(text, 7);

        //  Then
        assertThat(actual).contains(new String[] {"messa"});
    }

    @Test
    void shouldCorrectlyFindLastTokenStartingWithWhiteNewLine() {
        // Given
        String text = "\nmessa";

        // When
        Optional<String[]> actual = TokenFinder.findLastToken(text, 6);

        //  Then
        assertThat(actual).contains(new String[]{"messa"});
    }

    @Test
    void shouldCorrectlyFindLastTokenStartingWithWhiteNewLineAndTab() {
        // Given
        String text = "\n\tmessa";

        // When
        Optional<String[]> actual = TokenFinder.findLastToken(text, 7);

        //  Then
        assertThat(actual).contains(new String[]{"messa"});
    }

    @Test
    void shouldCorrectlyFindLastToken() {
        // Given
        String text = "def test(context,message) {\n" +
                "\tdef text = 'test function';\n" +
                "\tHttpPartBuilder.create().text('Mytext').\n" +
                "\treturn text + ' result';\n" +
                "}";

        int offset = 98;

        // When
        Optional<String[]> actual = TokenFinder.findLastToken(text, offset);

        // Then
        assertThat(actual)
                .contains(new String[]{"HttpPartBuilder", "create", "text", ""});
    }

    @Test
    void shouldCorrectlyFindLastTokenWhenArgumentTextWithEmptySpace() {
        // Given
        String text = "def test(context,message) {\n" +
                "\tdef text = 'test function';\n" +
                "\tHttpPartBuilder.create().text('My text').\n" +
                "\treturn text + ' result';\n" +
                "}";

        int offset = 99;

        // When
        Optional<String[]> actual = TokenFinder.findLastToken(text, offset);

        // Then
        assertThat(actual)
                .contains(new String[]{"HttpPartBuilder", "create", "text", ""});
    }

    @Test
    void shouldReturnEmptyWhenTextWithNotWellBalancedRoundParenthesis() {
        // Given
        String text = "Util.'My text').";

        int offset = 16;

        // When
        Optional<String[]> actual = TokenFinder.findLastToken(text, offset);

        // Then
        assertThat(actual).isNotPresent();
    }

    @Test
    void shouldReturnCorrectlyWhenTextInsideRoundParenthesis() {
        // Given
        String text = "Util.text('My text').";

        int offset = 21;

        // When
        Optional<String[]> actual = TokenFinder.findLastToken(text, offset);

        // Then
        assertThat(actual).contains(new String[]{"Util", "text", ""});
    }

    @Test
    void shouldReturnCorrectlyWhenTextInsideSquareBrackets() {
        // Given
        String text = "Util.text(['one text', 'two text']).";

        int offset = 36;

        // When
        Optional<String[]> actual = TokenFinder.findLastToken(text, offset);

        // Then
        assertThat(actual).contains(new String[]{"Util","text",""});
    }

    @Test
    void shouldReturnCorrectTokenWhenNewLineAndBuilderPattern() {
        String given = "def helloWorld(context, message) {\n" +
                "\n" +
                "    HttpPartBuilder.create()\n" +
                "        .name('he llo')\n" +
                "        .IntellijIdeaRulezzz \n" +
                "    return result;\n" +
                "}\n";

        int offset = 98;

        Optional<String[]> lastToken = TokenFinder.findLastToken(given, offset);

        assertThat(lastToken)
                .contains(new String[]{"HttpPartBuilder","create","name", ""});
    }



    @Test
    void shouldReturnCorrectTokensWhenNewLineFollowedBySpaces() {
        // Given
        String input = "def myFunction(context, message) {\n" +
                "    HttpPartBuilder.create()\n" +
                "        .IntellijIdeaRulezzz \n" +
                "}";

        // When
        Optional<String[]> actual = TokenFinder.findLastToken(input, 73);

        // Then
        assertThat(actual).contains(new String[]{"HttpPartBuilder", "create", ""});
    }

    @Test
    void shouldCorrectlyTokenizeRootVariable() {
        // Given
        String input = "HttpPartBuilder.IntellijIdeaRulezzz ";

        // When
        Optional<String[]> actual = TokenFinder.findLastToken(input, 16);

        // Then
        assertThat(actual).contains(new String[]{"HttpPartBuilder", ""});
    }

    @Test
    void shouldCorrectlyTokenizeEmptyString() {
        // Given
        String input = "";

        // When
        Optional<String[]> actual = TokenFinder.findLastToken(input, 0);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldCorrectlyTokenizeBuilderString() {
        // Given
        String input = "def myFunction(context, message) {\n" +
                "    HttpPartBuilder.create()\n" +
                "        .attribute('myFile', 'myName')\n" +
                "        .binary(message.payload())\n" +
                "        .IntellijIdeaRulezzz \n" +
                "}";

        // When
        Optional<String[]> actual = TokenFinder.findLastToken(input, 147);

        // Then
        assertThat(actual)
                .contains(new String[]{"HttpPartBuilder","create","attribute", "binary", ""});
    }
}
