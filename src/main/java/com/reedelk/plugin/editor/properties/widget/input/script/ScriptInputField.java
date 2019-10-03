package com.reedelk.plugin.editor.properties.widget.input.script;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;
import com.reedelk.plugin.commons.Labels;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.editor.properties.widget.input.InputChangeListener;
import com.reedelk.plugin.editor.properties.widget.input.script.editor.ScriptEditor;
import com.reedelk.plugin.editor.properties.widget.input.script.editor.ScriptEditorDefault;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.reedelk.plugin.commons.Icons.Script;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class ScriptInputField extends DisposablePanel implements Disposable {

    private final Module module;
    private final ScriptContextManager context;


    private InputChangeListener<String> listener;
    private ScriptEditor editor;
    private String value = "";

    public ScriptInputField(@NotNull Module module,
                            @NotNull ScriptContextManager context) {
        this.module = module;
        this.context = context;
        this.editor = new ScriptEditorDefault(module.getProject(), context);

        JPanel openEditorBtn = new OpenEditorButton();

        setLayout(new BorderLayout());
        add(openEditorBtn, NORTH);
        add(editor.getComponent(), CENTER);
    }


    @Override
    public void dispose() {
        this.editor.dispose();
    }

    public void addListener(InputChangeListener<String> listener) {
        this.listener = listener;
        this.editor.setListener(newValue -> listener.onChange((String) newValue));
    }

    public void setValue(Object o) {
        this.value = (String) o;
        this.editor.setValue(this.value);
    }

    class OpenEditorButton extends JBPanel {

        private final Border BORDER_BTN_OPEN_EDITOR =
                BorderFactory.createEmptyBorder(3, 0, 10, 0);

        private JLabel openEditorBtn;

        OpenEditorButton() {
            openEditorBtn = new JLabel(Labels.SCRIPT_EDITOR_BTN_OPEN_EDITOR);
            openEditorBtn.setIcon(Script.Edit);
            openEditorBtn.setDisabledIcon(Script.EditDisabled);
            openEditorBtn.addMouseListener(new OpenScriptEditorDialog());

            setLayout(new BorderLayout());
            setBorder(BORDER_BTN_OPEN_EDITOR);
            add(openEditorBtn, NORTH);
        }
    }

    class OpenScriptEditorDialog extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            EditScriptDialog dialog = new EditScriptDialog(module, value, context);
            if (dialog.showAndGet()) {
                value = dialog.getValue();
                listener.onChange(value);
                editor.setValue(value);
            }
        }
    }
}
