package com.reedelk.plugin.editor.properties.widget;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class NumericDocumentFilter extends DocumentFilter {

    private final InputTest tester;

    public NumericDocumentFilter(InputTest tester) {
        this.tester = tester;
    }

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
        } else if (tester.test(sb.toString())) {
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
        } else if (tester.test(sb.toString())) {
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
        } else if (tester.test(sb.toString())) {
            super.remove(fb, offset, length);
        } else {
            // warn the user and don't allow the insert
        }
    }

    public interface InputTest {
        boolean test(String value);
    }
}
