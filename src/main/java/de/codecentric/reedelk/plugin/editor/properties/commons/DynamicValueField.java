package de.codecentric.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.util.ui.JBUI;
import de.codecentric.reedelk.plugin.commons.Colors;
import de.codecentric.reedelk.plugin.commons.Fonts;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typedynamicvalue.DynamicValueScriptEditor;
import de.codecentric.reedelk.runtime.api.commons.ScriptUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import static com.intellij.icons.AllIcons.Actions.EditSource;
import static de.codecentric.reedelk.plugin.commons.Icons.Script.Code;
import static java.awt.BorderLayout.CENTER;

public class DynamicValueField extends DisposablePanel {

    private final transient DynamicValueInputFieldAdapter inputFieldAdapter;

    private ScriptEditor editor;
    private DisposablePanel scriptContainer;
    private DisposablePanel inputFieldContainer;
    private transient ScriptEditorChangeListener listener;

    public DynamicValueField(Module module,
                             DynamicValueInputFieldAdapter inputFieldAdapter,
                             String scriptPropertyPath,
                             ContainerContext context,
                             Object initValue) {
        this.inputFieldAdapter = inputFieldAdapter;
        this.editor = new DynamicValueScriptEditor(module, scriptPropertyPath, context);
        this.scriptContainer = createScriptModePanel(editor);
        this.inputFieldContainer = createInputFieldContainer();

        setBorder(JBUI.Borders.empty(2, 3));
        setLayout(new BorderLayout());

        if (ScriptUtils.isScript(initValue)) {
            editor.setValue((String) initValue);
            add(this.scriptContainer, CENTER);
        } else {
            inputFieldAdapter.setValue(initValue);
            add(this.inputFieldContainer, CENTER);
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
        DisposablePanel container = ContainerFactory.createLabelNextToComponentWithOuterBorder(codeIcon, editorComponent);
        Border iconOutside = JBUI.Borders.customLine(Colors.SCRIPT_EDITOR_INLINE_BORDER, 1, 1, 1, 0);
        codeIcon.setPreferredSize(new Dimension(25, 22));
        codeIcon.setBorder(iconOutside);
        return new FixedHeightPanel(container);
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
        DisposablePanel container =
                ContainerFactory.createLabelNextToComponentWithOuterBorder(textIcon, inputFieldAdapter.getComponent());
        return new FixedHeightPanel(container);
    }

    private void switchComponent(DisposablePanel visible, DisposablePanel invisible) {
        ApplicationManager.getApplication().invokeLater(() -> {
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

    private static class FixedHeightPanel extends DisposablePanel {

        private static final int HEIGHT = 24;

        public FixedHeightPanel(JComponent content) {
            setLayout(new BorderLayout());
            add(content, CENTER);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            size.height = HEIGHT;
            return size;
        }

        @Override
        public Dimension getMaximumSize() {
            Dimension size = super.getMaximumSize();
            size.height = HEIGHT;
            return size;
        }

        @Override
        public Dimension getMinimumSize() {
            Dimension size = super.getMinimumSize();
            size.height = HEIGHT;
            return size;
        }
    }
}
