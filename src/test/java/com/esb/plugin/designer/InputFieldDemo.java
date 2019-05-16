package com.esb.plugin.designer;

import javax.swing.*;
import javax.swing.text.*;

public class InputFieldDemo {

    public static void main(String args[]) {
        JTextField textField = new JTextField(10);

        JPanel panel = new JPanel();
        panel.add(textField);

        PlainDocument doc = (PlainDocument) textField.getDocument();
        doc.setDocumentFilter(new IntegerDocumentFilter());


        JOptionPane.showMessageDialog(null, panel);
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

        private boolean test(String text) {
            try {
                Integer.parseInt(text);
                return true;
            } catch (NumberFormatException e) {
                return false;
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
    }
}
