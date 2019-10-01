package com.reedelk.plugin.editor.properties.widget.input.script;

import com.intellij.openapi.module.Module;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.Fonts;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.editor.properties.widget.ClickableLabel;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.editor.properties.widget.input.script.editor.DynamicValueScriptEditor;
import com.reedelk.plugin.editor.properties.widget.input.script.editor.ScriptEditor;
import com.reedelk.runtime.api.commons.ScriptUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static com.intellij.util.ui.JBUI.emptyInsets;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public class DynamicValueField extends DisposablePanel {

    private final InputFieldAdapter inputFieldAdapter;

    private ScriptEditor editor;
    private DisposablePanel scriptContainer;
    private DisposablePanel inputFieldContainer;
    private OnChangeListener listener;

    public DynamicValueField(Module module, ScriptContextManager context, InputFieldAdapter inputFieldAdapter) {
        this.inputFieldAdapter = inputFieldAdapter;

        this.editor = new DynamicValueScriptEditor(module.getProject(), context);
        this.scriptContainer = createScriptModePanel(editor.getComponent());
        this.inputFieldContainer = createInputFieldContainer();

        setBorder(JBUI.Borders.empty(0, 3));
        setLayout(new BorderLayout());
        add(this.inputFieldContainer, CENTER);
    }

    public void setValue(Object newValue) {
        // If the value is a script, we switch to a script
        if (ScriptUtils.isScript(newValue)) {
            editor.setValue((String) newValue);
            switchComponent(scriptContainer, inputFieldContainer);
        } else {
            inputFieldAdapter.setValue(newValue);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        editor.dispose();
    }

    public interface OnChangeListener {
        void onChange(Object newValue);
    }

    private DisposablePanel createScriptModePanel(JComponent editorComponent) {
        JLabel codeIcon = new ClickableLabel(Icons.Script.Code, Icons.Script.Code,
                () -> {
                    switchComponent(inputFieldContainer, scriptContainer);
                    listener.onChange(inputFieldAdapter.getValue());
                });
        return layoutWith(codeIcon, editorComponent);
    }

    private DisposablePanel createInputFieldContainer() {
        JLabel textIcon = new ClickableLabel(Icons.Script.Edit, Icons.Script.Edit,
                () -> {
                    switchComponent(scriptContainer, inputFieldContainer);
                    String script = ScriptUtils.asScript(editor.getValue());
                    listener.onChange(script);
                });
        inputFieldAdapter.setMargin(emptyInsets());
        inputFieldAdapter.setBorder(JBUI.Borders.empty());
        inputFieldAdapter.setFont(Fonts.ScriptEditor.SCRIPT_EDITOR);
        return layoutWith(textIcon, inputFieldAdapter.getComponent());
    }

    private void switchComponent(DisposablePanel visible, DisposablePanel invisible) {
        SwingUtilities.invokeLater(() -> {
            DynamicValueField.this.add(visible, CENTER);
            visible.requestFocus();
            DynamicValueField.this.remove(invisible);
            DynamicValueField.this.revalidate();
        });
    }

    private DisposablePanel layoutWith(JLabel icon, JComponent body) {
        Border iconOutside = JBUI.Borders.customLine(Colors.SCRIPT_EDITOR_INLINE_ICON_BORDER, 1, 1, 1, 0);
        Border iconInside = JBUI.Borders.empty(0, 4);
        CompoundBorder iconBorder = new CompoundBorder(iconOutside, iconInside);
        icon.setBorder(iconBorder);

        Border bodyOutside = JBUI.Borders.customLine(Colors.SCRIPT_EDITOR_INLINE_ICON_BORDER, 1, 1, 1, 1);
        Border bodyInside = JBUI.Borders.empty(0, 2);
        CompoundBorder bodyBorder = new CompoundBorder(bodyOutside, bodyInside);
        body.setBorder(bodyBorder);

        return new Focusable(icon, body);
    }

    public void addListener(OnChangeListener listener) {
        this.listener = listener;
        this.inputFieldAdapter.addListener(listener);
        this.editor.setListener(listener);
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
}