package com.reedelk.plugin.editor.properties.widget.input.script.editor;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ThreeComponentsSplitter;
import com.intellij.util.ThrowableRunnable;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.editor.properties.widget.input.InputChangeListener;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptContextManager;
import com.reedelk.plugin.editor.properties.widget.input.script.suggestion.SuggestionDropDownDecorator;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.reedelk.plugin.editor.properties.widget.input.script.editor.EditorConstants.JAVASCRIPT_FILE_TYPE;
import static java.awt.BorderLayout.CENTER;

public class JavascriptEditorDefault extends DisposablePanel implements JavascriptEditor {

    private static final boolean HORIZONTAL = false;

    private static class Dimensions {
        private static final int DIVIDER_WIDTH = 0;
        private static final int DIVIDER_MOUSE_ZONE_WIDTH = 4;
        private static final int EDITOR_CONTEXT_VARIABLES_SIZE = 170;
    }

    protected Project project;
    protected EditorEx editor;
    protected Document document;


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

    @Override
    public void addListener(InputChangeListener<String> listener) {

    }

    public String getValue() {
        return document.getText();
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

    public JavascriptEditorDefault(
            @NotNull Project project,
            @NotNull ScriptContextManager contextManager) {

        this.project = project;
        this.document = EditorFactory.getInstance().createDocument("");
        this.editor = (EditorEx) EditorFactory.getInstance()
                .createEditor(document, project, JAVASCRIPT_FILE_TYPE, false);


        SuggestionDropDownDecorator.decorate(editor, document,
                new EditorWordSuggestionClient(project, contextManager));


        JavascriptEditorContextPanel contextPanel =
                new JavascriptEditorContextPanel(contextManager.getVariables());

        JComponent editorComponent = editor.getComponent();

        ThreeComponentsSplitter splitter = new ThreeComponentsSplitter(HORIZONTAL);
        splitter.setFirstComponent(contextPanel);
        splitter.setLastComponent(editorComponent);
        splitter.setDividerWidth(Dimensions.DIVIDER_WIDTH);
        splitter.setFirstSize(Dimensions.EDITOR_CONTEXT_VARIABLES_SIZE);
        splitter.setDividerMouseZoneSize(Dimensions.DIVIDER_MOUSE_ZONE_WIDTH);

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1000, 600));
        add(splitter, CENTER);
    }
}
