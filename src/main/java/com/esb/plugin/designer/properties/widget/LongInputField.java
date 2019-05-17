package com.esb.plugin.designer.properties.widget;

import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.converter.ValueConverterFactory;
import com.esb.plugin.designer.properties.InputChangeListener;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public class LongInputField extends JBPanel implements DocumentListener {

    private static final int COLUMNS_NUMBER = 16;

    private InputChangeListener<Long> listener;
    private final JTextField inputField;
    private final ValueConverter<Long> converter = ValueConverterFactory.forType(Long.class);


    public LongInputField() {
        super(new BorderLayout());
        inputField = new JTextField(COLUMNS_NUMBER);

        PlainDocument document = (PlainDocument) inputField.getDocument();
        document.setDocumentFilter(new IntegerDocumentFilter());
        document.addDocumentListener(this);

        add(inputField, WEST);
        add(Box.createHorizontalBox(), CENTER);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        notifyListener();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        notifyListener();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        notifyListener();
    }

    public void addListener(InputChangeListener<Long> changeListener) {
        this.listener = changeListener;
    }

    private void notifyListener() {
        if (listener != null) {
            Long objectValue = converter.from(inputField.getText());
            listener.onChange(objectValue);
        }
    }

    public void setValue(Object value) {
        String valueAsString = converter.toString(value);
        inputField.setText(valueAsString);
    }

    static class IntegerDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.insert(offset, string);

            if (sb.length() == 1 && sb.toString().equals("-")) {
                super.insertString(fb, offset, "-", attr);
            } else if (sb.length() == 1 && sb.toString().equals("+")) {
                super.insertString(fb, offset, "+", attr);
            } else if (sb.length() == 0) {
                super.insertString(fb, offset, "0", attr);
            } else if (test(sb.toString())) {
                super.insertString(fb, offset, string, attr);
            } else {
                // warn the user and don't allow the insert
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.replace(offset, offset + length, text);

            if (sb.length() == 1 && sb.toString().equals("-")) {
                super.replace(fb, offset, length, text, attrs);
            } else if (sb.length() == 1 && sb.toString().equals("+")) {
                super.replace(fb, offset, length, text, attrs);
            } else if (sb.length() == 0) {
                super.replace(fb, offset, length, text, attrs);
            } else if (test(sb.toString())) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                // warn the user and don't allow the insert
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.delete(offset, offset + length);

            if (sb.length() == 1 && sb.toString().equals("-")) {
                super.remove(fb, offset, length);
            } else if (sb.length() == 1 && sb.toString().equals("+")) {
                super.remove(fb, offset, length);
            } else if (sb.length() == 0) {
                super.remove(fb, offset, length);
            } else if (test(sb.toString())) {
                super.remove(fb, offset, length);
            } else {
                // warn the user and don't allow the insert
            }
        }

        private boolean test(String text) {
            try {
                Long.parseLong(text);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

    }
}
