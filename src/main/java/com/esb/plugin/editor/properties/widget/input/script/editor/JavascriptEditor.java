package com.esb.plugin.editor.properties.widget.input.script.editor;

import com.esb.plugin.editor.properties.widget.input.script.ScriptContextManager;
import com.esb.plugin.editor.properties.widget.input.script.suggestion.SuggestionDropDownDecorator;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ThreeComponentsSplitter;
import com.intellij.util.ThrowableRunnable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class JavascriptEditor extends ThreeComponentsSplitter implements Disposable {

    private static class Dimensions {
        private static final int DIVIDER_WIDTH = 0;
        private static final int DIVIDER_MOUSE_ZONE_WIDTH = 4;
        private static final int EDITOR_CONTEXT_VARIABLES_SIZE = 170;
    }

    private static final boolean HORIZONTAL = false;

    private static final FileType JAVASCRIPT_FILE_TYPE =
            FileTypeManager.getInstance().getFileTypeByExtension("js");

    private final Editor editor;
    private final Project project;
    private final Document document;

    JavascriptEditor(@NotNull Project project,
                     @NotNull JavascriptEditorMode mode,
                     @NotNull ScriptContextManager contextManager,
                     @NotNull String initialText) {

        super(HORIZONTAL);


        this.project = project;
        this.document = EditorFactory.getInstance().createDocument(initialText);
        this.editor = EditorFactory.getInstance().createEditor(document, project, JAVASCRIPT_FILE_TYPE, false);

        SuggestionDropDownDecorator.decorate(editor, document,
                new EditorWordSuggestionClient(project, contextManager));

        JComponent editorComponent = editor.getComponent();

        editorComponent.setBorder(mode.border());
        setPreferredSize(mode.preferredSize());

        JavascriptEditorContextPanel contextPanel =
                new JavascriptEditorContextPanel(contextManager.getVariables());

        setFirstComponent(contextPanel);
        setLastComponent(editorComponent);
        setDividerWidth(Dimensions.DIVIDER_WIDTH);
        setFirstSize(Dimensions.EDITOR_CONTEXT_VARIABLES_SIZE);
        setDividerMouseZoneSize(Dimensions.DIVIDER_MOUSE_ZONE_WIDTH);
    }

    @Override
    public void dispose() {
        Editor[] allEditors = EditorFactory.getInstance().getAllEditors();
        for (Editor currentEditor : allEditors) {
            if (currentEditor == editor) {
                EditorFactory.getInstance().releaseEditor(currentEditor);
            }
        }
    }

    public void addDocumentListener(DocumentListener listener) {
        this.document.addDocumentListener(listener);
    }

    public JComponent getComponent() {
        return editor.getComponent();
    }

    public void setValue(String value) {
        try {
            WriteCommandAction.writeCommandAction(project).run((ThrowableRunnable<Throwable>) () -> {
                document.setText(value);
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public String getValue() {
        return document.getText();
    }
}
