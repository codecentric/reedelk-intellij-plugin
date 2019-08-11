package com.reedelk.plugin.editor.properties.widget.input.script.editor;

import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ThreeComponentsSplitter;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptContextManager;
import com.reedelk.plugin.editor.properties.widget.input.script.suggestion.SuggestionDropDownDecorator;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.reedelk.plugin.editor.properties.widget.input.script.editor.EditorConstants.JAVASCRIPT_FILE_TYPE;

class JavascriptEditorDefault extends JavascriptEditor {

    private static final boolean HORIZONTAL = false;

    private static class Dimensions {
        private static final int DIVIDER_WIDTH = 0;
        private static final int DIVIDER_MOUSE_ZONE_WIDTH = 4;
        private static final int EDITOR_CONTEXT_VARIABLES_SIZE = 170;
    }

    JavascriptEditorDefault(@NotNull Project project,
                            @NotNull JavascriptEditorMode mode,
                            @NotNull ScriptContextManager contextManager,
                            @NotNull String initialText) {

        this.project = project;
        this.document = EditorFactory.getInstance().createDocument(initialText);
        this.editor = (EditorEx) EditorFactory.getInstance()
                .createEditor(document, project, JAVASCRIPT_FILE_TYPE, false);


        SuggestionDropDownDecorator.decorate(editor, document,
                new EditorWordSuggestionClient(project, contextManager));


        setPreferredSize(mode.preferredSize(editor));

        JavascriptEditorContextPanel contextPanel =
                new JavascriptEditorContextPanel(contextManager.getVariables());

        JComponent editorComponent = editor.getComponent();

        ThreeComponentsSplitter componentsSplitter = new ThreeComponentsSplitter(HORIZONTAL);
        componentsSplitter.setFirstComponent(contextPanel);
        componentsSplitter.setLastComponent(editorComponent);
        componentsSplitter.setDividerWidth(Dimensions.DIVIDER_WIDTH);
        componentsSplitter.setFirstSize(Dimensions.EDITOR_CONTEXT_VARIABLES_SIZE);
        componentsSplitter.setDividerMouseZoneSize(Dimensions.DIVIDER_MOUSE_ZONE_WIDTH);

        setLayout(new BorderLayout());
        setPreferredSize(mode.preferredSize(editor));
        add(componentsSplitter, BorderLayout.CENTER);
    }
}
