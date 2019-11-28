package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.commons.Labels;
import com.reedelk.plugin.commons.ModuleUtils;
import com.reedelk.plugin.editor.properties.renderer.commons.ScriptEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;

import static com.reedelk.runtime.commons.ModuleProperties.Script;

public class EditScriptDialog extends DialogWrapper {

    private static final Dimension DEFAULT_SCRIPT_DIMENSION = new Dimension(700, 400);

    private ScriptEditor editor;

    EditScriptDialog(@NotNull Module module, String scriptFile) {
        super(module.getProject(), false);
        setTitle(Labels.DIALOG_TITLE_EDIT_SCRIPT);
        setResizable(true);

        String resources = ModuleUtils.getResourcesFolder(module).orElseThrow(() -> new RuntimeException("error"));
        VirtualFile file = VfsUtil.findFile(Paths.get(resources, Script.RESOURCE_DIRECTORY, scriptFile), true);
        Document document = FileDocumentManager.getInstance().getDocument(file);

        editor = new ScriptEditorDefault(module.getProject(), document, true);
        editor.getComponent().setPreferredSize(DEFAULT_SCRIPT_DIMENSION);


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
        return editor.getComponent();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.editor != null) {
            this.editor.dispose();
        }
    }

    public String getValue() {
        return editor.getValue();
    }
}
