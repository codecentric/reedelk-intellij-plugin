package com.esb.plugin.editor.properties.widget.input.script;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.util.ThrowableRunnable;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;

/**
 * Matches individual words instead of complete text
 */
public class TextComponentWordSuggestionClient implements SuggestionClient {

    private static final Logger LOG = Logger.getInstance(TextComponentWordSuggestionClient.class);

    private SuggestionProvider suggestionProvider;
    private Project project;

    // We just invoke update if we are not  moving back with the caret.
    private int previousCaretPosition = -1;

    public TextComponentWordSuggestionClient(Project project, SuggestionProvider suggestionProvider) {
        this.suggestionProvider = suggestionProvider;
        this.project = project;
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
    public void setSelectedText(JTextComponent textComponent, Document document, String selectedValue) {
        int cp = textComponent.getCaretPosition();
        try {
            if (cp == 0 || textComponent.getText(cp - 1, 1).trim().isEmpty()) {
                document.insertString(cp, selectedValue);
            } else {
                int previousWordIndex = Utilities.getPreviousWord(textComponent, cp - 1);
                String text = textComponent.getText(previousWordIndex, cp - previousWordIndex);
                if (selectedValue.startsWith(text)) {
                    WriteCommandAction.writeCommandAction(project).run((ThrowableRunnable<Throwable>) () -> {
                        document.insertString(cp, selectedValue.substring(text.length()));

                        int newCaretPosition = cp + selectedValue.substring(text.length()).length();
                        textComponent.setCaretPosition(newCaretPosition);
                        previousCaretPosition = newCaretPosition;
                    });

                } else {
                    int lastIndex = text.lastIndexOf(".");
                    int length = text.length();
                    int delta = length - lastIndex;
                    String realSelected = selectedValue.substring(delta - 1);

                    WriteCommandAction.writeCommandAction(project).run((ThrowableRunnable<Throwable>) () -> {
                        document.insertString(cp, realSelected);
                        textComponent.setCaretPosition(cp + realSelected.length());

                        int newCaretPosition = cp + realSelected.length();
                        textComponent.setCaretPosition(newCaretPosition);
                        previousCaretPosition = newCaretPosition;
                    });
                }
            }
        } catch (BadLocationException e) {
            System.err.println(e);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public List<String> getSuggestions(JTextComponent textComponent) {

        int currentCaretPosition = textComponent.getCaretPosition();
        if (previousCaretPosition > currentCaretPosition) {
            previousCaretPosition = currentCaretPosition;
            return null;
        } else {
            previousCaretPosition = currentCaretPosition;
        }


        try {
            String text = getText(textComponent);
            LOG.info("Get suggestions for text: " + text);
            return text != null ?
                    suggestionProvider.suggest(text) :
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
        int wordStartIndex = Utilities.getWordStart(textComponent, caretPosition - 1);
        String text = textComponent.getText(wordStartIndex, caretPosition - wordStartIndex);
        if (".".equals(text)) {
            int previousWordIndex = Utilities.getPreviousWord(textComponent, caretPosition - 1);
            text = textComponent.getText(previousWordIndex, caretPosition - previousWordIndex);
        }
        return text.trim();
    }
}
