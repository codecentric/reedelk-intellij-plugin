package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeScriptDescriptor;
import com.reedelk.plugin.component.domain.VariableDefinition;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.ContainerFactory;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.editor.properties.widget.FormBuilder;
import com.reedelk.plugin.editor.properties.widget.input.script.PropertyPanelContext;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptContextManager;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptInputField;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptInputInlineField;

import javax.swing.*;
import java.util.List;

public class TypeScriptPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(Module module, ComponentPropertyDescriptor propertyDescriptor, PropertyAccessor propertyAccessor, PropertyPanelContext context) {
        TypeScriptDescriptor descriptor = (TypeScriptDescriptor) propertyDescriptor.getPropertyType();
        List<VariableDefinition> variableDefinitions = propertyDescriptor.getVariableDefinitions();
        ScriptContextManager scriptContextManager = new ScriptContextManager(module, context, variableDefinitions);

        if (descriptor.isInline()) {
            ScriptInputInlineField field = new ScriptInputInlineField(module, scriptContextManager);
            field.setValue(propertyAccessor.get());
            field.addListener(propertyAccessor::set);
            return field;

        } else {
            ScriptInputField field = new ScriptInputField(module, scriptContextManager);
            field.setValue(propertyAccessor.get());
            field.addListener(propertyAccessor::set);
            return field;
        }
    }

    @Override
    public void addToParent(JComponent parent, JComponent rendered, String label) {
        // If the property type is a complex object, we wrap it in a
        // bordered box with title the name of the object property.
        DisposablePanel wrappedRenderedComponent = ContainerFactory
                .createObjectTypeContainer(label, rendered);
        FormBuilder.get()
                .addLastField(wrappedRenderedComponent, parent);
    }
}
