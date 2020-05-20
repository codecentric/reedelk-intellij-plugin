package com.reedelk.plugin.completion;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TokenFinderTest {

    TokenFinder tokenFinderNew = new TokenFinder();

    @Test
    void booh() {

        // Given
        String text = "message.payload().each { entry.attributes().each { entry.";

        // When
        List<String> actual = tokenFinderNew.find(text, text.length());

        //  Then
        assertThat(actual).contains("payload", "each", "{", "entry", "attributes", "each", "{", "entry", "");
    }

    @Test
    void shouldDoSomethingEmpty() {

        // Given
        String text = "";

        // When
        List<String> actual = tokenFinderNew.find(text, text.length());

        //  Then
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldDoSomething() {

        // Given
        String text = "  messa";

        // When
        List<String> actual = tokenFinderNew.find(text, 7);

        //  Then
        assertThat(actual).containsExactly("messa");
    }

    @Test
    void shouldCorrectlyFindLastTokenStartingWithWhiteNewLineAndTab() {
        // Given
        String text = "\n\tmessa";

        // When
        List<String> actual = tokenFinderNew.find(text, 7);

        //  Then
        assertThat(actual).containsExactly("messa");
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
        List<String> actual = tokenFinderNew.find(text, offset);

        // Then
        assertThat(actual)
                .containsExactly("HttpPartBuilder", "create", "text", "");
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
        List<String> actual = tokenFinderNew.find(text, offset);

        // Then
        assertThat(actual)
                .containsExactly("HttpPartBuilder", "create", "text", "");
    }

    @Test
    void shouldReturnEmptyWhenTextWithNotWellBalancedRoundParenthesis() {
        // Given
        String text = "Util.'My text').";

        int offset = 16;

        // When
        List<String> actual = tokenFinderNew.find(text, offset);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldReturnCorrectlyWhenTextInsideRoundParenthesis() {
        // Given
        String text = "Util.text('My text').";

        int offset = 21;

        // When
        List<String> actual = tokenFinderNew.find(text, offset);

        // Then
        assertThat(actual).containsExactly("Util", "text", "");
    }

    @Test
    void shouldReturnCorrectlyWhenTextInsideSquareBrackets() {
        // Given
        String text = "Util.text(['one text', 'two text']).";

        int offset = 36;

        // When
        List<String> actual = tokenFinderNew.find(text, offset);

        // Then
        assertThat(actual).containsExactly("Util","text", "");
    }

    @Test
    void shouldReturnCorrectlyWhenTextInsideSquareBracketsAndBoh() {
        // Given
        String text = "Util.text([message.";

        // When
        List<String> actual = tokenFinderNew.find(text, text.length());

        // Then
        assertThat(actual).containsExactly("message", "");
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

        List<String> actual = tokenFinderNew.find(given, offset);

        assertThat(actual)
                .containsExactly("HttpPartBuilder","create","name", "");
    }

    @Test
    void shouldReturnCorrectTokensWhenNewLineFollowedBySpaces() {
        // Given
        String input = "def myFunction(context, message) {\n" +
                "    HttpPartBuilder.create()\n" +
                "        .IntellijIdeaRulezzz \n" +
                "}";

        // When
        List<String> actual = tokenFinderNew.find(input, 73);

        // Then
        assertThat(actual).containsExactly("HttpPartBuilder", "create", "");
    }

    @Test
    void shouldCorrectlyTokenizeRootVariable() {
        // Given
        String input = "HttpPartBuilder.IntellijIdeaRulezzz ";

        // When
        List<String> actual = tokenFinderNew.find(input, 16);

        // Then
        assertThat(actual).containsExactly("HttpPartBuilder", "");
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
        List<String> actual = tokenFinderNew.find(input, 147);

        // Then
        assertThat(actual)
                .containsExactly("HttpPartBuilder","create","attribute", "binary", "");
    }

    @Test
    void shouldCorrectlyTokenizeNestedBuilderString() {
        // Given
        String input = "message.payload(Util.";

        // When
        List<String> actual = tokenFinderNew.find(input, input.length());

        // Then
        assertThat(actual)
                .containsExactly("Util", "");
    }

    @Test
    void shouldNested() {
        String input = "def myFunction(context, message) {\n" +
                "    HttpPartBuilder.create()\n" +
                "        .attribute('myFile', 'myName')\n" +
                "        .binary(message.)" +
                "}";

        // When
        List<String> actual = tokenFinderNew.find(input, input.length() - 2);

        // Then
        assertThat(actual)
                .containsExactly("message", "");
    }

    @Test
    void shouldCorrectlyFindTokenWhenListIteratorClosure() {
        // Given
        String input = "message.payload().collect { it, each -> it.something(message.payload()) each.";

        // When
        List<String> actual = tokenFinderNew.find(input, input.length());

        // Then
        assertThat(actual).containsExactly("message", "payload", "collect", "{", "each", "");
    }

    @Test
    void shouldCorrectlyFindTokenWhenListIteratorClosureWithMessage() {
        // Given
        String input = "message.payload().collect { it, each -> it.something(Util.";

        // When
        List<String> actual = tokenFinderNew.find(input, input.length());

        // Then
        assertThat(actual).containsExactly("Util", "");
    }

    @Test
    void shouldCorrectlyFindTokenWhenListIteratorClosureWithMessageNestedClosure() {
        // Given
        String input = "message.payload().collect { it, each -> each.collect { it.super(message.";

        // When
        List<String> actual = tokenFinderNew.find(input, input.length());

        // Then
        assertThat(actual).containsExactly("message", "");
    }

    @Test
    void shouldCorrectlyFindTokenWhenEndOfClosure() {
        // Given
        String input = "message.payload().collect { it.IntellijIdeaRulezzz  }";

        // When
        List<String> actual = tokenFinderNew.find(input, input.length());

        // Then
        assertThat(actual).containsExactly("message", "payload", "collect");
    }
}
