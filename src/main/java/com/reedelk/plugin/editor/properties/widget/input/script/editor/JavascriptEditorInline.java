package com.reedelk.plugin.editor.properties.widget.input.script.editor;

import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptContextManager;
import com.reedelk.plugin.editor.properties.widget.input.script.suggestion.SuggestionDropDownDecorator;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static com.reedelk.plugin.editor.properties.widget.input.script.editor.EditorConstants.JAVASCRIPT_FILE_TYPE;
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


        JLabel codeIcon = new JLabel();
        codeIcon.setIcon(Icons.Script.Code);

        Border outside = JBUI.Borders.customLine(Colors.SCRIPT_EDITOR_INLINE_ICON_BORDER, 1, 1, 1, 0);
        Border inside = JBUI.Borders.empty(0, 4);
        CompoundBorder border = new CompoundBorder(outside, inside);
        codeIcon.setBorder(border);

        setLayout(new BorderLayout());
        setPreferredSize(mode.preferredSize(editor));
        add(codeIcon, BorderLayout.WEST);
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
