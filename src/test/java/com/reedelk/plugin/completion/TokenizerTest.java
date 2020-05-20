package com.reedelk.plugin.completion;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenizerTest {

    @Test
    void shouldReturnEmptyWhenTextIsEmpty() {

        // Given
        String text = "";

        // When
        String[] actual = Tokenizer.tokenize(text, text.length());

        //  Then
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldReturnCorrectlyWhenLeadingSpaces() {

        // Given
        String text = "  messa";

        // When
        String[] actual = Tokenizer.tokenize(text, 7);

        //  Then
        assertThat(actual).containsExactly("messa");
    }

    @Test
    void shouldReturnCorrectlyWhenLeadingNewLineAndTab() {
        // Given
        String text = "\n\tmessa";

        // When
        String[] actual = Tokenizer.tokenize(text, 7);

        //  Then
        assertThat(actual).containsExactly("messa");
    }

    @Test
    void shouldReturnCorrectlyInlineBuilderPatternWithNotEmptyLiteralInputMethod() {
        // Given
        String text = "def test(context,message) {\n" +
                "\tdef text = 'test function';\n" +
                "\tHttpPartBuilder.create().text('Mytext').";

        // When
        String[] actual = Tokenizer.tokenize(text, text.length());

        // Then
        assertThat(actual).containsExactly("HttpPartBuilder", "create", "text", "");
    }

    @Test
    void shouldReturnCorrectlyInlineBuilderPatternWithNotEmptyLiteralInputMethodWithSpaceInIt() {
        // Given
        String text = "def test(context,message) {\n" +
                "\tdef text = 'test function';\n" +
                "\tHttpPartBuilder.create().text('My text').";

        // When
        String[] actual = Tokenizer.tokenize(text, text.length());

        // Then
        assertThat(actual).containsExactly("HttpPartBuilder", "create", "text", "");
    }

    @Test
    void shouldReturnEmptyWhenTextWithNotWellBalancedRoundParenthesis() {
        // Given
        String text = "Util.'My text').";

        // When
        String[] actual = Tokenizer.tokenize(text, text.length());

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldReturnCorrectlyWhenTextInsideRoundParenthesis() {
        // Given
        String text = "Util.text('My text').";

        // When
        String[] actual = Tokenizer.tokenize(text, text.length());

        // Then
        assertThat(actual).containsExactly("Util", "text", "");
    }

    @Test
    void shouldReturnCorrectlyWhenTextInsideSquareBrackets() {
        // Given
        String text = "Util.text(['one text', 'two text']).";

        // When
        String[] actual = Tokenizer.tokenize(text, text.length());

        // Then
        assertThat(actual).containsExactly("Util","text", "");
    }

    @Test
    void shouldReturnCorrectlyWhenTextInsideSquareBracketsAndNotBalancedBrackets() {
        // Given
        String text = "Util.text([message.";

        // When
        String[] actual = Tokenizer.tokenize(text, text.length());

        // Then
        assertThat(actual).containsExactly("message", "");
    }

    @Test
    void shouldReturnCorrectTokensWhenNewLineAndBuilderPattern() {
        String text = "def helloWorld(context, message) {\n" +
                "\n" +
                "    HttpPartBuilder.create()\n" +
                "        .name('he llo')\n" +
                "        .";

        String[] actual = Tokenizer.tokenize(text, text.length());

        assertThat(actual).containsExactly("HttpPartBuilder","create","name", "");
    }

    @Test
    void shouldReturnCorrectTokensWhenNewLineFollowedBySpaces() {
        // Given
        String text = "def myFunction(context, message) {\n" +
                "    HttpPartBuilder.create()\n" +
                "        .";

        // When
        String[] actual = Tokenizer.tokenize(text, text.length());

        // Then
        assertThat(actual).containsExactly("HttpPartBuilder", "create", "");
    }

    @Test
    void shouldReturnCorrectTokensForRootType() {
        // Given
        String text = "HttpPartBuilder.";

        // When
        String[] actual = Tokenizer.tokenize(text, text.length());

        // Then
        assertThat(actual).containsExactly("HttpPartBuilder", "");
    }

    @Test
    void shouldReturnCorrectTokensWhenBuilderWithInnerMethodCall() {
        // Given
        String text = "def myFunction(context, message) {\n" +
                "    HttpPartBuilder.create()\n" +
                "        .attribute('myFile', 'myName')\n" +
                "        .binary(message.payload())\n" +
                "        .";

        // When
        String[] actual = Tokenizer.tokenize(text, text.length());

        // Then
        assertThat(actual).containsExactly("HttpPartBuilder","create","attribute", "binary", "");
    }

    @Test
    void shouldReturnCorrectTokensForNestedMethodCall() {
        // Given
        String text = "message.payload(Util.";

        // When
        String[] actual = Tokenizer.tokenize(text, text.length());

        // Then
        assertThat(actual).containsExactly("Util", "");
    }

    @Test
    void shouldReturnCorrectTokensForNestedBuilderMethodCall() {
        String text = "def myFunction(context, message) {\n" +
                "    HttpPartBuilder.create()\n" +
                "        .attribute('myFile', 'myName')\n" +
                "        .binary(message.";

        // When
        String[] actual = Tokenizer.tokenize(text, text.length());

        // Then
        assertThat(actual).containsExactly("message", "");
    }

    @Test
    void shouldReturnCorrectTokensForListIteratorClosure() {
        // Given
        String text = "message.payload().collect { it, each -> it.something(message.payload()) each.";

        // When
        String[] actual = Tokenizer.tokenize(text, text.length());

        // Then
        assertThat(actual).containsExactly("message", "payload", "collect", "{", "each", "");
    }

    @Test
    void shouldReturnCorrectTokensForListIteratorClosureWithNestedType() {
        // Given
        String text = "message.payload().collect { it, each -> it.something(Util.";

        // When
        String[] actual = Tokenizer.tokenize(text, text.length());

        // Then
        assertThat(actual).containsExactly("Util", "");
    }

    @Test
    void shouldReturnCorrectTokensForMultipleNestedListIteratorClosureWithNestedType() {
        // Given
        String text = "message.payload().collect { it, each -> each.collect { it.super(message.";

        // When
        String[] actual = Tokenizer.tokenize(text, text.length());

        // Then
        assertThat(actual).containsExactly("message", "");
    }

    @Test
    void shouldReturnCorrectTokensWhenClosureIsClosed() {
        // Given
        String text = "message.payload().collect { it.myMethod()  }";

        // When
        String[] actual = Tokenizer.tokenize(text, text.length());

        // Then
        assertThat(actual).containsExactly("message", "payload", "collect");
    }

    @Test
    void shouldReturnCorrectTokensWhenMultipleClosureWithNestedEach() {
        // Given
        String text = "message.payload().each { entry.attributes().each { entry.";

        // When
        String[] actual =  Tokenizer.tokenize(text, text.length());

        //  Then
        assertThat(actual).contains("payload", "each", "{", "entry", "attributes", "each", "{", "entry", "");
    }
}
