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
import java.nio.file.Paths;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class EditScriptDialog extends DialogWrapper {

    private ScriptEditor editor;

    EditScriptDialog(@NotNull Module module, String scriptFilePathAndName) {
        super(module.getProject(), false);
        setTitle(message("script.dialog.edit.title"));
        setResizable(true);

        ModuleUtils.getScriptsFolder(module).ifPresent(scriptsFolder -> {
            VirtualFile scriptVirtualFile = VfsUtil.findFile(Paths.get(scriptsFolder, scriptFilePathAndName), true);
            if (scriptVirtualFile != null) {
                Document document = FileDocumentManager.getInstance().getDocument(scriptVirtualFile);
                editor = new ScriptEditorDefault(module, document);
            }
        });

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
        // TODO: If editor is null, then error  center  panel.
        return editor;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.editor != null) {
            this.editor.dispose();
        }
    }
}
