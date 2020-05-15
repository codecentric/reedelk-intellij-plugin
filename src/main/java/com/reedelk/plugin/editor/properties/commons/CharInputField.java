package com.reedelk.plugin.editor.properties.commons;

import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.plugin.converter.ValueConverterProvider;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class CharInputField extends InputField<String> {

    public CharInputField(String hint) {
        super(hint, 3);
        document.setDocumentFilter(new JTextFieldLimit(1));
    }

    @Override
    protected ValueConverter<String> getConverter() {
        return ValueConverterProvider.forType(String.class);
    }

    /**
     * Limits to 1 the number of characters that can be added.
     */
    static class JTextFieldLimit extends DocumentFilter {

        private int limit;

        JTextFieldLimit(int limit) {
            super();
            this.limit = limit;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            Document doc = fb.getDocument();
            if (string == null) return;
            if ((doc.getLength() + string.length()) <= limit) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            Document doc = fb.getDocument();
            if (text == null) return;
            if ((doc.getLength() + text.length()) <= limit) {
                super.insertString(fb, offset, text, attrs);
            }
        }
    }
}
