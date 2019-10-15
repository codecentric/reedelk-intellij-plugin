package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.VariableDefinition;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import com.reedelk.plugin.editor.properties.widget.input.script.DynamicValueField;
import com.reedelk.plugin.editor.properties.widget.input.script.InputFieldAdapter;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptContextManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

abstract class AbstractDynamicPropertyRenderer extends AbstractTypePropertyRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module, @NotNull ComponentPropertyDescriptor propertyDescriptor, @NotNull PropertyAccessor propertyAccessor, @NotNull ContainerContext context) {
        List<VariableDefinition> variableDefinitions = propertyDescriptor.getVariableDefinitions();
        ScriptContextManager scriptContext = new ScriptContextManager(module, context, variableDefinitions);

        String hint = propertyDescriptor.getHintValue();

        DynamicValueField field =
                new DynamicValueField(module, scriptContext, inputFieldAdapter(hint));
        field.setValue(propertyAccessor.get());
        field.addListener(propertyAccessor::set);
        return field;
    }

    protected abstract InputFieldAdapter inputFieldAdapter(String hint);

}