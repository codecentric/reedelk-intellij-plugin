package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.ModuleUtils;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.renderer.commons.ScriptEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;

import static com.reedelk.plugin.commons.Colors.FOREGROUND_TEXT;
import static com.reedelk.plugin.commons.Icons.Script.ScriptLoadError;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static javax.swing.SwingConstants.CENTER;

public class EditScriptDialog extends DialogWrapper {

    private final String scriptFilePathAndName;
    private ScriptEditor editor;


    EditScriptDialog(@NotNull Module module, String scriptFilePathAndName) {
        super(module.getProject(), false);
        setTitle(message("script.dialog.edit.title"));
        setResizable(true);
        this.scriptFilePathAndName = scriptFilePathAndName;

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
        okAction.putValue(Action.NAME, message("script.dialog.edit.btn.edit"));
        return okAction;
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        if (editor == null) {
            return new Action[]{getCancelAction()};
        } else {
            return new Action[]{getOKAction(), getCancelAction()};
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return editor == null ?
                new EditorErrorPanel(scriptFilePathAndName):
                editor;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (editor != null) {
            editor.dispose();
        }
    }

    static class EditorErrorPanel extends DisposablePanel {

        EditorErrorPanel(String scriptFilePathAndName) {
            setLayout(new BorderLayout());
            JLabel loading =
                    new JLabel(message("script.error.could.not.be.loaded", scriptFilePathAndName), ScriptLoadError, CENTER);
            loading.setHorizontalTextPosition(JLabel.CENTER);
            loading.setVerticalTextPosition(JLabel.BOTTOM);
            loading.setForeground(FOREGROUND_TEXT);
            add(loading, BorderLayout.CENTER);
            setPreferredSize(ScriptEditorDefault.DEFAULT_SCRIPT_DIMENSION);
            setBackground(Colors.PROPERTIES_EMPTY_SELECTION_BACKGROUND);
        }
    }
}
