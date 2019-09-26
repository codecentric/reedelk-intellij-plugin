package com.reedelk.plugin.editor.properties.widget.input.script.editor;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.Fonts;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.editor.properties.widget.ClickableLabel;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.editor.properties.widget.input.InputChangeListener;
import com.reedelk.plugin.editor.properties.widget.input.StringInputField;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptContextManager;
import com.reedelk.plugin.editor.properties.widget.input.script.suggestion.SuggestionDropDownDecorator;
import com.reedelk.runtime.api.commons.ScriptUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static com.intellij.openapi.command.WriteCommandAction.writeCommandAction;
import static com.intellij.util.ui.JBUI.Borders;
import static com.intellij.util.ui.JBUI.emptyInsets;
import static com.reedelk.plugin.editor.properties.widget.input.script.editor.EditorConstants.JAVASCRIPT_FILE_TYPE;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;
import static java.util.Collections.singletonList;

public class JavascriptDynamicValueEditor extends DisposablePanel implements JavascriptEditor, DocumentListener {

    private final boolean notViewer = false;
    private final boolean singleLineMode = true;

    private DisposablePanel scriptContainer;
    private DisposablePanel textContainer;

    private Project project;
    private EditorEx editor;
    private Document document;
    private InputChangeListener<String> listener;
    private StringInputField textInputField;

    public JavascriptDynamicValueEditor(@NotNull Project project,
                                        @NotNull ScriptContextManager contextManager,
                                        String hint) {

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

        scriptContainer = createScriptModePanel(editor.getComponent());

        textInputField = new StringInputField(hint);
        textContainer = createTextModePanel();

        setLayout(new BorderLayout());
        add(textContainer, CENTER);
    }

    @Override
    public void addListener(InputChangeListener<String> listener) {
        this.listener = listener;
        this.textInputField.addListener(listener);
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
        // If the value is a script, we switch to a script
        if (ScriptUtils.isScript(value)) {
            try {
                String script = ScriptUtils.unwrap(value);
                writeCommandAction(project).run(() -> document.setText(script));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            switchComponent(scriptContainer, textContainer);
        } else {
            // Set the  value to the  text input field
            textInputField.setText(value);
        }
    }

    @Override
    public String getValue() {
        return document.getText();
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void dispose() {
        super.dispose();
        Editor[] allEditors = EditorFactory.getInstance().getAllEditors();
        for (Editor currentEditor : allEditors) {
            if (currentEditor == editor) {
                EditorFactory.getInstance().releaseEditor(currentEditor);
            }
        }
    }

    private DisposablePanel createScriptModePanel(JComponent editorComponent) {
        JLabel codeIcon = new ClickableLabel(Icons.Script.Code, Icons.Script.Code,
                () -> {
                    switchComponent(textContainer, scriptContainer);
                    listener.onChange(textInputField.getText());
                });
        return layoutWith(codeIcon, editorComponent);
    }

    private DisposablePanel createTextModePanel() {
        JLabel textIcon = new ClickableLabel(Icons.Script.Edit, Icons.Script.Edit,
                () -> {
                    switchComponent(scriptContainer, textContainer);
                    String script = ScriptUtils.asScript(document.getText());
                    listener.onChange(script);
                });
        textInputField.setMargin(emptyInsets());
        textInputField.setBorder(Borders.empty());
        textInputField.setFont(Fonts.ScriptEditor.SCRIPT_EDITOR);
        return layoutWith(textIcon, textInputField);
    }

    private void switchComponent(DisposablePanel visible, DisposablePanel invisible) {
        SwingUtilities.invokeLater(() -> {
            JavascriptDynamicValueEditor.this.add(visible, CENTER);
            visible.requestFocus();
            JavascriptDynamicValueEditor.this.remove(invisible);
            JavascriptDynamicValueEditor.this.revalidate();
        });
    }

    private DisposablePanel layoutWith(JLabel icon, JComponent body) {
        Border iconOutside = Borders.customLine(Colors.SCRIPT_EDITOR_INLINE_ICON_BORDER, 1, 1, 1, 0);
        Border iconInside = Borders.empty(0, 4);
        CompoundBorder iconBorder = new CompoundBorder(iconOutside, iconInside);
        icon.setBorder(iconBorder);

        Border bodyOutside = Borders.customLine(Colors.SCRIPT_EDITOR_INLINE_ICON_BORDER, 1, 1, 1, 1);
        Border bodyInside = Borders.empty(0, 2);
        CompoundBorder bodyBorder = new CompoundBorder(bodyOutside, bodyInside);
        body.setBorder(bodyBorder);

        return new Focusable(icon, body);
    }

    class Focusable extends DisposablePanel {

        private final JComponent body;

        Focusable(JLabel icon, JComponent body) {
            super(new BorderLayout());
            add(icon, WEST);
            add(body, CENTER);
            this.body = body;
        }

        @Override
        public void requestFocus() {
            body.requestFocus();
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
