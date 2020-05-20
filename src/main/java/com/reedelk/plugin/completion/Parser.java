package com.reedelk.plugin.completion;

import com.intellij.lexer.Lexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.plugins.groovy.lang.psi.GroovyElementTypes;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final Lexer lexer;
    private final List<Token> tokens = new ArrayList<>();

    Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public List<Token> find() {
        IElementType tokenType = lexer.getTokenType();
        if (tokenType == null) return tokens;

        if (isCurrentTokenOfType(GroovyElementTypes.T_LBRACE)) {
            lbrace();

        } else if (isCurrentTokenOfType(GroovyElementTypes.T_RBRACE)) {
            return tokens; // The scope has stopped.

        } else if (isCurrentTokenOfType(GroovyElementTypes.T_LPAREN)) {
            lparen();

        } else if (isCurrentTokenOfType(GroovyElementTypes.T_RPAREN)) {
            return tokens; // If it was closed we return the tokens. The scope has stopped.

        } else if (isCurrentTokenOfType(GroovyElementTypes.IDENTIFIER)) {
            identifier();
        } else if (isCurrentTokenOfType(GroovyElementTypes.T_DOT)) {
            tokens.add(Token.create(lexer));

        } else if (isCurrentTokenOfType(TokenType.WHITE_SPACE) || isCurrentTokenOfType(GroovyElementTypes.NL)) {
            // We only accept white spaces and new line, and we just consume it without
            // doing nothing.

        } else {
            // Anything else marks the beginning of a new statement.
            tokens.clear();
        }

        lexer.advance();

        find();

        return tokens;
    }

    private void identifier() {
        if (tokens.isEmpty() || isLastTokenOfType(GroovyElementTypes.T_DOT)) {
            tokens.add(Token.create(lexer));
        } else {
            tokens.clear();
            tokens.add(Token.create(lexer));
        }
    }

    private void lparen() {
        Token lparenToken = Token.create(lexer);
        tokens.add(lparenToken);
        // Try to consume and build stack until right paren found.
        // if right paren not found, then inner stack should be used.
        lexer.advance();
        List<Token> subTokens = new Parser(lexer).find();
        if (lexer.getTokenType() != GroovyElementTypes.T_RPAREN) {
            // We skip the parenthesis, they are not consumed.
            tokens.clear();
            tokens.add(lparenToken);
            tokens.addAll(subTokens);
        } else {
            tokens.add(lparenToken);
        }
    }

    private void lbrace() {
        // An lbrace might be the beginning of a function or of a lambda.
        // It is the beginning of a lambda if and only if the previous element is an identifier.
        if (isLastTokenOfType(GroovyElementTypes.IDENTIFIER)) {
            Token lBraceToken = Token.create(lexer);
            lexer.advance();
            List<Token> subTokens = new Parser(lexer).find();
            if (lexer.getTokenType() != GroovyElementTypes.T_RBRACE) {
                // It is a new statement, e.g 'each.collect { it.super(message.', we want messge
                if (isFirstTokenOfType(subTokens, GroovyElementTypes.T_LPAREN)) {
                    tokens.clear();
                    tokens.addAll(subTokens);
                } else {
                    // We skip the parenthesis, they are not consumed.
                    tokens.add(lBraceToken);
                    tokens.addAll(subTokens);
                }
            } // Otherwise they were consumed and we don't care... we just continue
        }
    }

    private boolean isFirstTokenOfType(List<Token> collection, IElementType target) {
        return !collection.isEmpty() &&
                collection.get(0).tokenType == target;

    }

    private boolean isLastTokenOfType(IElementType target) {
        return !tokens.isEmpty() &&
                tokens.get(tokens.size() - 1).tokenType == target;
    }

    private boolean isCurrentTokenOfType(IElementType target) {
        return target == lexer.getTokenType();
    }
}
