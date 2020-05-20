package com.reedelk.plugin.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.lang.lexer.GroovyLexer;
import org.jetbrains.plugins.groovy.lang.psi.GroovyElementTypes;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class TokenFinder {

    private GroovyLexer lexer;
    public TokenFinder() {
        lexer = new GroovyLexer();
    }

    public Optional<String[]> find(@NotNull CompletionParameters parameters) {
        String text = parameters.getPosition().getText();
        int offset = parameters.getOffset();
        try {
            List<String> strings = find(text, offset);
            return Optional.of(strings.toArray(new String[0]));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<String> find(String text, int offset) {
        lexer.start(text, 0, offset);

        List<Token> tokens = new Parser(lexer).find();

        return postProcess(tokens);
    }

    private List<String> postProcess(List<Token> tokensArray) {
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
