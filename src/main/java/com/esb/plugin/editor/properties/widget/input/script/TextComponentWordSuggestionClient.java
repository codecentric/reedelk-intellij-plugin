package com.esb.plugin.editor.properties.widget.input.script;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Matches individual words instead of complete text
 */
public class TextComponentWordSuggestionClient implements SuggestionClient {
    private Function<String, List<String>> suggestionProvider;

    public TextComponentWordSuggestionClient(Function<String, List<String>> suggestionProvider) {
        this.suggestionProvider = suggestionProvider;
    }

    @Override
    public Point getPopupLocation(JTextComponent invoker) {
        int caretPosition = invoker.getCaretPosition();
        try {
            Rectangle2D rectangle2D = invoker.modelToView(caretPosition);
            return new Point((int) rectangle2D.getX(), (int) (rectangle2D.getY() + rectangle2D.getHeight()));
        } catch (BadLocationException e) {
            System.err.println(e);
            return null;
        }
    }

    @Override
    public void setSelectedText(JTextComponent tp, String selectedValue) {
        int cp = tp.getCaretPosition();
        try {
            if (cp == 0 || tp.getText(cp - 1, 1).trim().isEmpty()) {
                tp.getDocument().insertString(cp, selectedValue, null);
            } else {
                int previousWordIndex = Utilities.getPreviousWord(tp, cp);
                String text = tp.getText(previousWordIndex, cp - previousWordIndex);
                if (selectedValue.startsWith(text)) {
                    tp.getDocument().insertString(cp, selectedValue.substring(text.length()), null);
                } else {
                    int lastIndex = text.lastIndexOf(".");
                    int length = text.length();
                    int delta = length - lastIndex;
                    String realSelected = selectedValue.substring(delta - 1);
                    tp.getDocument().insertString(cp, realSelected, null);
                }
            }
        } catch (BadLocationException e) {
            System.err.println(e);
        }
    }

    @Override
    public List<String> getSuggestions(JTextComponent textComponent) {
        try {
            String text = getText(textComponent);
            return text != null ?
                    suggestionProvider.apply(text) :
                    Collections.emptyList();
        } catch (BadLocationException e) {
            System.err.println(e);
            return Collections.emptyList();
        }
    }

    private String getText(JTextComponent textComponent) throws BadLocationException {
        int caretPosition = textComponent.getCaretPosition();
        if (caretPosition != 0) {
            String text = textComponent.getText(caretPosition - 1, 1);
            if (text.trim().isEmpty()) {
                return null;
            }
        }
        int previousWordIndex = Utilities.getPreviousWord(textComponent, caretPosition);
        String text = textComponent.getText(previousWordIndex, caretPosition - previousWordIndex);
        if (".".equals(text)) {
            int previousWord = Utilities.getPreviousWord(textComponent, caretPosition - 2);
            text = textComponent.getText(previousWord, caretPosition - 1 - previousWord)+ ".";
        }

        return text.trim();
    }
}
