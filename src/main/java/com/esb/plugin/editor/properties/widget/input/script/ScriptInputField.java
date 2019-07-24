package com.esb.plugin.editor.properties.widget.input.script;

import com.esb.plugin.commons.Labels;
import com.esb.plugin.editor.properties.widget.input.InputChangeListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.esb.plugin.commons.Icons.Script;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class ScriptInputField extends JPanel implements ActionListener, DocumentListener, Disposable {

    private InputChangeListener<String> listener;
    private JavascriptEditor editor;
    private String value = "";

    private final Module module;
    private final ScriptContextManager context;

    public ScriptInputField(@NotNull Module module,
                            @NotNull ScriptContextManager context) {
        super(new BorderLayout());

        this.module = module;
        this.context = context;

        JPanel openEditorBtn = new OpenEditorButton();
        add(openEditorBtn, NORTH);

        editor = JavascriptEditorFactory.get()
                .mode(JavascriptEditorMode.DEFAULT)
                .project(module.getProject())
                .context(context)
                .build();

        editor.addDocumentListener(this);
        add(editor, CENTER);
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        if (listener != null) {
            listener.onChange(event.getDocument().getText());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EditScriptDialog editScriptDialog = new EditScriptDialog(module, value, context);
        if (editScriptDialog.showAndGet()) {
            this.value = editScriptDialog.getValue();
            listener.onChange(this.value);
        }
    }

    @Override
    public void dispose() {
        this.editor.dispose();
    }


    public void addListener(InputChangeListener<String> listener) {
        this.listener = listener;
    }

    public void setValue(Object o) {
        this.value = (String) o;
        this.editor.setValue(this.value);
    }

    class OpenEditorButton extends JPanel {

        private final Border BORDER_BTN_OPEN_EDITOR =
                BorderFactory.createEmptyBorder(3, 0, 10, 0);

        private JLabel openEditorBtn;

        OpenEditorButton() {
            super(new BorderLayout());

            this.openEditorBtn = new JLabel(Labels.SCRIPT_EDITOR_BTN_OPEN_EDITOR);
            this.openEditorBtn.setIcon(Script.Edit);
            this.openEditorBtn.setDisabledIcon(Script.EditDisabled);
            this.openEditorBtn.addMouseListener(new OpenScriptEditorDialog());
            add(openEditorBtn, NORTH);
            setBorder(BORDER_BTN_OPEN_EDITOR);
        }
    }

    class OpenScriptEditorDialog extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            EditScriptDialog editScriptDialog = new EditScriptDialog(module, editor.getValue(), context);
            if (editScriptDialog.showAndGet()) {
                value = editScriptDialog.getValue();
                listener.onChange(value);
                editor.setValue(value);
            }
        }
    }
}
