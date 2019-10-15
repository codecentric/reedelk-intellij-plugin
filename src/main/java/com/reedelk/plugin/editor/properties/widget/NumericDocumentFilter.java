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
            super.insertString(fb, offset, string, attr);
        } else if (sb.length() == 1 && sb.toString().equals("+")) {
            super.insertString(fb, offset, string, attr);
        } else if (sb.length() == 1 && sb.toString().equals("$")) {
            super.insertString(fb, offset, string, attr);
        } else if (sb.length() == 2 && sb.toString().equals("${")) {
            super.insertString(fb, offset, string, attr);
        } else if (sb.toString().endsWith("}")) {
            super.insertString(fb, offset, string, attr);
        } else if (sb.length() > 2 && sb.toString().startsWith("${") && !sb.toString().contains("}")) {
            super.insertString(fb, offset, string, attr);
        } else if (sb.length() == 0) {
            super.insertString(fb, offset, string, attr);
        } else if (tester.test(sb.toString())) {
            super.insertString(fb, offset, string, attr);
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
        } else if (sb.length() == 1 && sb.toString().equals("$")) {
            super.replace(fb, offset, length, text, attrs);
        } else if (sb.length() == 2 && sb.toString().equals("${")) {
            super.replace(fb, offset, length, text, attrs);
        } else if (sb.toString().endsWith("}")) {
            super.replace(fb, offset, length, text, attrs);
        } else if (sb.length() > 2 && sb.toString().startsWith("${") && !sb.toString().contains("}")) {
            super.replace(fb, offset, length, text, attrs);
        } else if (sb.length() == 0) {
            super.replace(fb, offset, length, text, attrs);
        } else if (tester.test(sb.toString())) {
            super.replace(fb, offset, length, text, attrs);
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
        } else if (sb.length() == 1 && sb.toString().equals("$")) {
            super.remove(fb, offset, length);
        } else if (sb.length() == 2 && sb.toString().equals("${")) {
            super.remove(fb, offset, length);
        } else if (sb.toString().endsWith("}")) {
            super.remove(fb, offset, length);
        } else if (sb.length() > 2 && sb.toString().startsWith("${") && !sb.toString().contains("}")) {
            super.remove(fb, offset, length);
        } else if (sb.length() == 0) {
            super.remove(fb, offset, length);
        } else if (tester.test(sb.toString())) {
            super.remove(fb, offset, length);
        }
    }

    public interface InputTest {
        boolean test(String value);
    }
}
