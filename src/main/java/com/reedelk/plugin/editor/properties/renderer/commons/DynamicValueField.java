package com.reedelk.plugin.editor.properties.renderer.commons;

import com.intellij.openapi.module.Module;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.Fonts;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.renderer.typedynamicvalue.DynamicValueScriptEditor;
import com.reedelk.runtime.api.commons.ScriptUtils;

import javax.swing.*;
import java.awt.*;

import static com.intellij.icons.AllIcons.Actions.EditSource;
import static com.reedelk.plugin.commons.Icons.Script.Code;
import static java.awt.BorderLayout.CENTER;

public class DynamicValueField extends DisposablePanel {

    private final transient DynamicValueInputFieldAdapter inputFieldAdapter;

    private ScriptEditor editor;
    private DisposablePanel scriptContainer;
    private DisposablePanel inputFieldContainer;
    private transient ScriptEditorChangeListener listener;

    public DynamicValueField(Module module, DynamicValueInputFieldAdapter inputFieldAdapter, ContainerContext context) {
        this.inputFieldAdapter = inputFieldAdapter;

        this.editor = new DynamicValueScriptEditor(module, context);

        this.scriptContainer = createScriptModePanel(editor);
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

    private DisposablePanel createScriptModePanel(JComponent editorComponent) {
        JLabel codeIcon = new ClickableLabel(Code, () -> {
                    switchComponent(inputFieldContainer, scriptContainer);
                    listener.onChange(inputFieldAdapter.getValue());
                });
        return ContainerFactory.createLabelNextToComponent(codeIcon, editorComponent);
    }

    private DisposablePanel createInputFieldContainer() {
        JLabel textIcon = new ClickableLabel(EditSource, () -> {
                    switchComponent(scriptContainer, inputFieldContainer);
                    String script = ScriptUtils.asScript(editor.getValue());
                    listener.onChange(script);
                });
        inputFieldAdapter.setMargin(JBUI.emptyInsets());
        inputFieldAdapter.setBorder(JBUI.Borders.empty());
        inputFieldAdapter.setFont(Fonts.ScriptEditor.DYNAMIC_FIELD_FONT_SIZE);
        return ContainerFactory.createLabelNextToComponent(textIcon, inputFieldAdapter.getComponent());
    }

    private void switchComponent(DisposablePanel visible, DisposablePanel invisible) {
        SwingUtilities.invokeLater(() -> {
            DynamicValueField.this.add(visible, CENTER);
            visible.requestFocus();
            DynamicValueField.this.remove(invisible);
            DynamicValueField.this.revalidate();
        });
    }

    public void addListener(ScriptEditorChangeListener listener) {
        this.listener = listener;
        this.editor.setListener(listener);
        this.inputFieldAdapter.addListener(listener);
    }
}