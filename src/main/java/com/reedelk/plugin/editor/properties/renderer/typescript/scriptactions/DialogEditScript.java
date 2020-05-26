package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.PluginModuleUtils;
import com.reedelk.plugin.commons.ScriptFileUtils;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.function.Function;

import static com.reedelk.plugin.commons.Colors.FOREGROUND_TEXT;
import static com.reedelk.plugin.commons.Icons.Script.ScriptLoadError;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.Optional.ofNullable;
import static javax.swing.SwingConstants.CENTER;

public class DialogEditScript extends DialogWrapper {

    private final String scriptFilePathAndName;
    private final DisposablePanel editorPanel;
    private final Module module;
    private Document tmpDocument;
    private Document originalDocument;

    DialogEditScript(@NotNull Module module,
                     @NotNull String scriptFilePathAndName,
                     @NotNull String scriptPropertyPath,
                     @NotNull ContainerContext context) {
        super(module.getProject(), false);
        this.module = module;
        setTitle(message("script.dialog.edit.title"));
        setResizable(true);

        this.scriptFilePathAndName = scriptFilePathAndName;
        this.editorPanel = PluginModuleUtils.getScriptsFolder(module)
                .flatMap(scriptsFolder -> ofNullable(VfsUtil.findFile(Paths.get(scriptsFolder, scriptFilePathAndName), true))).map((Function<VirtualFile, DisposablePanel>) scriptVirtualFile -> {
                    originalDocument = FileDocumentManager.getInstance().getDocument(scriptVirtualFile);
                    // We create a tmp virtual Reedelk file so that we can workaround,
                    // wrong autocompletion for Groovy files inside expression fields and script editors.
                    tmpDocument = ScriptFileUtils.createInMemoryDocument(scriptPropertyPath, originalDocument.getText());
                    return new ScriptEditorDefault(module, tmpDocument, scriptPropertyPath, context);
                }).orElse(null);

        init();
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        WriteCommandAction.runWriteCommandAction(module.getProject(),() ->
                // Whenever we are done, we must write back the updated script in the file system file.
                originalDocument.setText(tmpDocument.getText()));
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
        if (editorPanel == null) {
            return new Action[]{getCancelAction()};
        } else {
            return new Action[]{getOKAction(), getCancelAction()};
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return editorPanel == null ?
                new EditorErrorPanel(scriptFilePathAndName) :
                editorPanel;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (editorPanel != null) {
            editorPanel.dispose();
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
