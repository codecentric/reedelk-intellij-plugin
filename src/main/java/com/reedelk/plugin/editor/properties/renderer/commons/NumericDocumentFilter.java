package com.reedelk.plugin.editor.properties.renderer.commons;

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

        if (isAllowedForInsertionOrReplace(sb)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.replace(offset, offset + length, text);

        if (isAllowedForInsertionOrReplace(sb)) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.delete(offset, offset + length);

        if (isAllowedForInsertionOrReplace(sb)) {
            super.remove(fb, offset, length);
        }
    }

    private boolean isAllowedForInsertionOrReplace(StringBuilder sb) {
        return sb.length() == 1 && sb.toString().equals("-") ||
                sb.length() == 1 && sb.toString().equals("+") ||
                sb.length() == 1 && sb.toString().equals("$") ||
                sb.length() == 2 && sb.toString().equals("${") ||
                sb.toString().endsWith("}") ||
                sb.length() > 2 && sb.toString().startsWith("${") && !sb.toString().contains("}") ||
                sb.length() == 0 ||
                tester.test(sb.toString());
    }

    public interface InputTest {
        boolean test(String value);
    }
}
