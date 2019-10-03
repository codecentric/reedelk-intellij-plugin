package com.reedelk.plugin.editor.properties.widget.input.script.editor;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.editor.properties.widget.input.script.DynamicValueField;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptContextManager;
import com.reedelk.plugin.editor.properties.widget.input.script.suggestion.SuggestionDropDownDecorator;
import com.reedelk.runtime.api.commons.ScriptUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static com.intellij.openapi.command.WriteCommandAction.writeCommandAction;
import static com.intellij.util.ui.JBUI.Borders;
import static com.reedelk.plugin.editor.properties.widget.input.script.editor.EditorConstants.JAVASCRIPT_FILE_TYPE;
import static java.util.Collections.singletonList;

public class DynamicValueScriptEditor implements ScriptEditor, DocumentListener {

    private static final Logger LOG = Logger.getInstance(DynamicValueScriptEditor.class);

    private final boolean notViewer = false;
    private final boolean singleLineMode = true;

    private Project project;
    private EditorEx editor;
    private Document document;
    private DynamicValueField.OnChangeListener listener;

    public DynamicValueScriptEditor(@NotNull Project project,
                                    @NotNull ScriptContextManager contextManager) {

        this.project = project;

        document = EditorFactory.getInstance().createDocument("");
        document.addDocumentListener(this);

        editor = (EditorEx) EditorFactory.getInstance()
                .createEditor(document, project, JAVASCRIPT_FILE_TYPE, notViewer);
        editor.setOneLineMode(singleLineMode);
        editor.setBorder(Borders.empty());

        configureSettings(editor.getSettings());

        SuggestionDropDownDecorator
                .decorate(editor, document, new EditorWordSuggestionClient(project, contextManager));
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        // Notify when script mode changed
        if (listener != null) {
            String script = ScriptUtils.asScript(event.getDocument().getText());
            listener.onChange(script);
        }
    }

    @Override
    public void setValue(String value) {
        if (value == null) return;
        try {
            String script = ScriptUtils.unwrap(value);
            writeCommandAction(project).run(() -> document.setText(script));
        } catch (Throwable throwable) {
            LOG.error(String.format("Could not write value [%s] to document", value), throwable);
        }
    }

    @Override
    public String getValue() {
        return document.getText();
    }

    @Override
    public JComponent getComponent() {
        return editor.getComponent();
    }

    @Override
    public void setListener(DynamicValueField.OnChangeListener listener) {
        this.listener = listener;
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
