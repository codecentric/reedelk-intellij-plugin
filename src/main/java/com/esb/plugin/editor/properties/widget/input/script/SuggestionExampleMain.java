package com.esb.plugin.editor.properties.widget.input.script;

import com.esb.plugin.editor.properties.widget.input.script.trie.Trie;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.List;

public class SuggestionExampleMain {


    private static final Trie trie = new Trie();

    public static void main(String[] args) {
        JFrame frame = createFrame();

        MessageSuggestions.SUGGESTIONS.forEach(trie::insert);

        DefaultStyledDocument doc = new JavascriptStyle();


        JTextPane textPane = new JTextPane(doc);
        textPane.setText("public class Hi {}");

        Font font = new Font("Menlo", Font.PLAIN, 20);
        textPane.setFont(font);

      //  SuggestionDropDownDecorator.decorate(textPane,
        //        document, new TextComponentWordSuggestionClient(SuggestionExampleMain::getSuggestions));
        frame.add(new JScrollPane(textPane));
        frame.setVisible(true);
    }

    private static int findLastNonWordChar (String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }
        return index;
    }

    private static int findFirstNonWordChar (String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
            index++;
        }
        return index;
    }

    private static List<String> getSuggestions(String input) {
        //the suggestion provider can control text search related stuff, e.g case insensitive match, the search  limit etc.
        return trie.searchByPrefix(input);
    }

    private static JFrame createFrame() {
        JFrame frame = new JFrame("Suggestion Dropdown Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(600, 300));
        return frame;
    }

    static class JavascriptStyle extends DefaultStyledDocument {

        private SimpleAttributeSet attr;
        private SimpleAttributeSet attrBlack;

        public JavascriptStyle() {

            this.attr = new SimpleAttributeSet();
            this.attr.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
            this.attr.addAttribute(StyleConstants.CharacterConstants.Foreground, new Color(0, 0, 128));

            this.attrBlack = new SimpleAttributeSet();
            this.attrBlack.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.FALSE);
            this.attrBlack.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.BLACK);

        }

        private int findLastNonWordChar (String text, int index) {
            while (--index >= 0) {
                if (String.valueOf(text.charAt(index)).matches("\\W")) {
                    break;
                }
            }
            return index;
        }

        private int findFirstNonWordChar (String text, int index) {
            while (index < text.length()) {
                if (String.valueOf(text.charAt(index)).matches("\\W")) {
                    break;
                }
                index++;
            }
            return index;
        }

        public void insertString (int offset, String str, AttributeSet a) throws BadLocationException {
            super.insertString(offset, str, a);

            String text = getText(0, getLength());
            int before = findLastNonWordChar(text, offset);
            if (before < 0) before = 0;
            int after = findFirstNonWordChar(text, offset + str.length());
            int wordL = before;
            int wordR = before;

            while (wordR <= after) {
                if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {
                    if (text.substring(wordL, wordR).matches(JavascriptKeywords.REGEX))
                        setCharacterAttributes(wordL, wordR - wordL, attr, false);
                    else
                        setCharacterAttributes(wordL, wordR - wordL, attrBlack, false);
                    wordL = wordR;
                }
                wordR++;
            }
        }

        public void remove (int offs, int len) throws BadLocationException {
            super.remove(offs, len);


            String text = getText(0, getLength());

            int before = findLastNonWordChar(text, offs);
            if (before < 0) before = 0;
            int after = findFirstNonWordChar(text, offs);

            if (text.substring(before, after).matches(JavascriptKeywords.REGEX)) {
                setCharacterAttributes(before, after - before, attr, false);
            } else {
                setCharacterAttributes(before, after - before, attrBlack, false);
            }
        }
    }
}
