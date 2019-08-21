package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeScriptDescriptor;
import com.reedelk.plugin.component.domain.VariableDefinition;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.ContainerFactory;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.editor.properties.widget.FormBuilder;
import com.reedelk.plugin.editor.properties.widget.input.script.ContainerContext;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptContextManager;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptInputField;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptInputInlineField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class TypeScriptPropertyRenderer implements TypePropertyRenderer {

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
                            @NotNull ComponentPropertyDescriptor descriptor,
                            @NotNull ContainerContext tracker) {
        // Wrap it in a bordered box with title the name of the object property.
        DisposablePanel wrappedRenderedComponent =
                ContainerFactory.createObjectTypeContainer(descriptor.getDisplayName(), rendered);

        // Add the rendered component to the parent.
        FormBuilder.get()
                .addLastField(wrappedRenderedComponent, parent);
    }
}
