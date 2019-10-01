package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.VariableDefinition;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import com.reedelk.plugin.editor.properties.widget.input.IntegerInputField;
import com.reedelk.plugin.editor.properties.widget.input.script.DynamicValueField;
import com.reedelk.plugin.editor.properties.widget.input.script.InputFieldAdapter;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptContextManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;

public class DynamicIntegerPropertyRenderer extends AbstractTypePropertyRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module, @NotNull ComponentPropertyDescriptor propertyDescriptor, @NotNull PropertyAccessor propertyAccessor, @NotNull ContainerContext context) {
        List<VariableDefinition> variableDefinitions = propertyDescriptor.getVariableDefinitions();
        ScriptContextManager scriptContext = new ScriptContextManager(module, context, variableDefinitions);

        String hint = propertyDescriptor.getHintValue();

        DynamicValueField field =
                new DynamicValueField(module, scriptContext, new IntegerInputFieldAdapter(hint));
        field.setValue(propertyAccessor.get());
        field.addListener(propertyAccessor::set);
        return field;
    }

    class IntegerInputFieldAdapter implements InputFieldAdapter {

        private IntegerInputField inputField;

        IntegerInputFieldAdapter(String hint) {
            this.inputField = new IntegerInputField(hint);
        }

        @Override
        public Object getValue() {
            return inputField.getValue();
        }

        @Override
        public JComponent getComponent() {
            return inputField;
        }

        @Override
        public void setFont(Font font) {
            inputField.setFont(font);
        }

        @Override
        public void setValue(Object value) {
            inputField.setValue((Integer) value);
        }

        @Override
        public void setMargin(Insets insets) {
            inputField.setMargin(insets);
        }

        @Override
        public void setBorder(Border border) {
            inputField.setBorder(border);
        }

        @Override
        public void addListener(DynamicValueField.OnChangeListener listener) {
            inputField.addListener(listener::onChange);
        }
    }
}
