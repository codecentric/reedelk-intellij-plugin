package com.esb.plugin.editor.properties.widget.input.script;

import com.esb.plugin.commons.Labels;
import com.esb.plugin.editor.properties.widget.input.script.trie.Trie;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class EditScriptDialog extends DialogWrapper {

    private final FileType JAVASCRIPT_FILE_TYPE =
            FileTypeManager.getInstance().getFileTypeByExtension("js");

    private final Module module;

    private static final int EDITOR_WIDTH = 600;
    private static final int EDITOR_HEIGHT = 400;

    private final Trie trie = new Trie();
    private final Document document;

    public EditScriptDialog(@NotNull Module module, @NotNull String initialValue) {
        super(module.getProject(), false);
        this.module = module;
        setTitle(Labels.DIALOG_TITLE_EDIT_SCRIPT);
        setResizable(true);

        document = EditorFactory.getInstance().createDocument(initialValue);
        MessageSuggestions.SUGGESTIONS.forEach(trie::insert);

        init();
    }

    @NotNull
    @Override
    protected Action getOKAction() {
        Action okAction = super.getOKAction();
        okAction.putValue(Action.NAME, Labels.DIALOG_BTN_SAVE_SCRIPT);
        return okAction;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        Editor editor = EditorFactory.getInstance().createEditor(
                document,
                module.getProject(),
                JAVASCRIPT_FILE_TYPE,
                false);

        editor.getComponent().setMinimumSize(new Dimension(EDITOR_WIDTH, EDITOR_HEIGHT));
        JTextComponent contentComponent = (JTextComponent) editor.getContentComponent();

        SuggestionDropDownDecorator.decorate(
                contentComponent,
                document,
                new TextComponentWordSuggestionClient(module.getProject(), trie::searchByPrefix));

        return editor.getComponent();
    }

    public String getText() {
        return document.getText();
    }
}
