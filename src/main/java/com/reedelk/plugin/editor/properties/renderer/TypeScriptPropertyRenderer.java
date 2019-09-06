package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeScriptDescriptor;
import com.reedelk.plugin.component.domain.VariableDefinition;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import com.reedelk.plugin.editor.properties.widget.FormBuilder;
import com.reedelk.plugin.editor.properties.widget.JComponentHolder;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptContextManager;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptInputField;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptInputInlineField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class TypeScriptPropertyRenderer extends AbstractTypePropertyRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

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
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull ComponentPropertyDescriptor propertyDescriptor,
                            @NotNull ContainerContext context) {

        TypeScriptDescriptor descriptor = (TypeScriptDescriptor) propertyDescriptor.getPropertyType();
        if (descriptor.isInline()) {
            // If the script input field is inline, it is like any other input field.
            super.addToParent(parent, rendered, propertyDescriptor, context);

        } else {
            // Apply visibility condition for the Script input.
            applyWhenVisibilityConditions(propertyDescriptor.getWhenDefinitions(), context, rendered);

            // Add the component to the parent container.
            FormBuilder.get()
                    .addLastField(rendered, parent);

            // Add the component to the context.
            context.addComponent(new JComponentHolder(rendered));
        }
    }
}
