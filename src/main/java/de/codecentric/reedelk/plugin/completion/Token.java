package de.codecentric.reedelk.plugin.completion;

import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;

public class Token {

    IElementType tokenType;
    String tokenText;

    Token(IElementType tokenType, String tokenText) {
        this.tokenType = tokenType;
        this.tokenText = tokenText;
    }

    public static Token create(Lexer lexer) {
        IElementType tokenType = lexer.getTokenType();
        String tokenText = lexer.getTokenText();
        return new Token(tokenType, tokenText);
    }
}
