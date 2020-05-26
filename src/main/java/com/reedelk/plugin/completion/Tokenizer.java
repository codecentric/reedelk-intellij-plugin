package com.reedelk.plugin.completion;

import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.plugins.groovy.lang.lexer.GroovyLexer;
import org.jetbrains.plugins.groovy.lang.psi.GroovyElementTypes;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class Tokenizer {

    private static final String[] EMPTY = new String[0];

    private GroovyLexer lexer;

    private Tokenizer() {
        lexer = new GroovyLexer();
    }

    public static String[] tokenize(String text, int offset) {
        return new Tokenizer().tokenizeInternal(text, offset);
    }

    String[] tokenizeInternal(String text, int offset) {
        try {

            lexer.start(text, 0, offset);

            List<Token> tokens = new Parser(lexer).find();

            List<String> processedTokens = process(tokens);

            return processedTokens.toArray(new String[0]);

        } catch (Exception exception) {
            return EMPTY;
        }
    }


    private List<String> process(List<Token> tokensArray) {
        // Add an empty string if it ends with '.'.
        if (!tokensArray.isEmpty() &&
                tokensArray.get(tokensArray.size() - 1).tokenText.equals(".")) {
            tokensArray.add(new Token(null, StringUtils.EMPTY));
        }
        return tokensArray.stream()
                .filter(token -> token.tokenType != GroovyElementTypes.T_DOT)
                .filter(token -> token.tokenType != GroovyElementTypes.T_LPAREN)
                .filter(token -> token.tokenType != GroovyElementTypes.T_RPAREN)
                .map(token -> token.tokenText)
                .collect(toList());
    }
}
