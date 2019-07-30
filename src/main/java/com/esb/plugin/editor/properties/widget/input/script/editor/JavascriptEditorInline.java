package com.esb.plugin.editor.properties.widget.input.script.editor;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.editor.properties.widget.input.script.ScriptContextManager;
import com.esb.plugin.editor.properties.widget.input.script.suggestion.SuggestionDropDownDecorator;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.esb.plugin.editor.properties.widget.input.script.editor.EditorConstants.JAVASCRIPT_FILE_TYPE;
import static java.util.Collections.singletonList;
import static javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER;

class JavascriptEditorInline extends JavascriptEditor {

    JavascriptEditorInline(@NotNull Project project,
                           @NotNull JavascriptEditorMode mode,
                           @NotNull ScriptContextManager contextManager,
                           @NotNull String initialText) {

        this.project = project;
        this.document = EditorFactory.getInstance().createDocument(initialText);
        this.editor = (EditorEx) EditorFactory.getInstance()
                .createEditor(document, project, JAVASCRIPT_FILE_TYPE, false);

        editor.setOneLineMode(true);
        SuggestionDropDownDecorator.decorate(editor, document,
                new EditorWordSuggestionClient(project, contextManager));

        configureSettings(editor.getSettings());

        editor.getScrollPane().setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);

        JComponent editorComponent = editor.getComponent();

        setLayout(new BorderLayout());
        setPreferredSize(mode.preferredSize(editor));
        JLabel icon = new JLabel();
        icon.setIcon(Icons.Script.Code);
        icon.setBorder(JBUI.Borders.emptyRight(5));
        add(icon, BorderLayout.WEST);
        add(editorComponent, BorderLayout.CENTER);
    }

    private static void configureSettings(EditorSettings settings) {
        settings.setRightMargin(0);
        settings.setAdditionalLinesCount(0);
        settings.setAdditionalColumnsCount(0);
        settings.setSoftMargins(singletonList(0));
        settings.setBlockCursor(false);
        settings.setCaretRowShown(false);
        settings.setGutterIconsShown(false);
        settings.setLineNumbersShown(false);
        settings.setShowIntentionBulb(false);
        settings.setLineMarkerAreaShown(false);
        settings.setAdditionalPageAtBottom(false);
        settings.setLeadingWhitespaceShown(false);
        settings.setAllowSingleLogicalLineFolding(false);
    }
}
