package com.esb.plugin.editor.properties.widget.input;

import com.esb.plugin.commons.Labels;
import com.esb.plugin.editor.properties.widget.input.script.JavascriptKeywords;
import com.esb.plugin.editor.properties.widget.input.script.MessageSuggestions;
import com.esb.plugin.editor.properties.widget.input.script.SuggestionDropDownDecorator;
import com.esb.plugin.editor.properties.widget.input.script.TextComponentWordSuggestionClient;
import com.esb.plugin.editor.properties.widget.input.script.trie.Trie;
import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.CodeCompletionHandlerBase;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Function;

public class ScriptInputField extends JButton implements ActionListener {

    private InputChangeListener<String> listener;

    private final Module module;
    private String value;

    public ScriptInputField(@NotNull Module module)  {
        this.addActionListener(this);
        this.module =  module;
        setName("Edit Script");
        setText("Edit Script");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EditScriptDialog editScriptDialog = new EditScriptDialog(module);
        if (editScriptDialog.showAndGet()) {
            this.value = editScriptDialog.getText();
            listener.onChange(this.value);
        }
    }

    public void addListener(InputChangeListener<String> listener) {
        this.listener =  listener;
    }

    public void setValue(Object o) {
        this.value = (String) o;
    }

    public class EditScriptDialog extends DialogWrapper {

        private final Module module;

        private JTextPane textPane;

        public EditScriptDialog(@NotNull Module module) {
            super(module.getProject(), false);
            this.module = module;
            setTitle(Labels.DIALOG_TITLE_EDIT_SCRIPT);
            setResizable(true);
            setSize(400, 400);
            init();
        }

        @NotNull
        @Override
        protected Action getOKAction() {
            Action okAction = super.getOKAction();
            okAction.putValue(Action.NAME, Labels.DIALOG_BTN_SAVE_SCRIPT);
            return okAction;
        }

        private void notifyListener() {
            if (listener != null) {
                if (textPane != null) {
             //       listener.onChange(textPane.getText());
                }
            }
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            Trie trie = new Trie();
            MessageSuggestions.SUGGESTIONS.forEach(trie::insert);

            DefaultStyledDocument doc = new JavascriptStyle();


            FileType js = FileTypeManager.getInstance().getFileTypeByExtension("js");
            Document document = EditorFactory.getInstance().createDocument("function test() {}");



            Editor  editor = EditorFactory.getInstance().createEditor(document, module.getProject(), js, false);
            editor.getCaretModel().addCaretListener(new CaretListener() {
                @Override
                public void caretPositionChanged(@NotNull CaretEvent event) {
                    System.out.println("D");
                }

                @Override
                public void caretAdded(@NotNull CaretEvent event) {
                    System.out.println("D");
                }

                @Override
                public void caretRemoved(@NotNull CaretEvent event) {
                    System.out.println("D");
                }
            });

            editor.putUserData(AutoPopupController.ALWAYS_AUTO_POPUP, true);
            editor.getComponent().setMinimumSize(new Dimension(600, 400));
            JTextComponent contentComponent = (JTextComponent) editor.getContentComponent();



            SuggestionDropDownDecorator.decorate(contentComponent,document,
                    new TextComponentWordSuggestionClient(new Function<String, java.util.List<String>>() {
                        @Override
                        public List<String> apply(String s) {
                            return trie.searchByPrefix(s);
                        }
                    }));

            /**
            textPane.setText(value);
            textPane.setPreferredSize(new Dimension(500, 400));
            textPane.setBackground(Color.WHITE);

            Font font = new Font("Menlo", Font.PLAIN, 19);
            textPane.setFont(font);

             */
            return editor.getComponent();
        }

        public String getText() {
            return textPane.getText();
        }
    }


    class MyTextPane extends JTextPane {

        public MyTextPane(StyledDocument doc) {
            super(doc);

        }


        // Override getScrollableTracksViewportWidth
        // to preserve the full width of the text
        @Override
        public boolean getScrollableTracksViewportWidth() {
            Component parent = getParent();
            ComponentUI ui = getUI();

            return parent != null ? (ui.getPreferredSize(this).width <= parent
                    .getSize().width) : true;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON );
            super.paintComponent(g);
        }
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
