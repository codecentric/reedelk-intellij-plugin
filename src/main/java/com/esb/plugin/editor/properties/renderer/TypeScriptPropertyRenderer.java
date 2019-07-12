package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.scanner.AutocompleteContext;
import com.esb.plugin.component.scanner.AutocompleteVariable;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.widget.ContainerFactory;
import com.esb.plugin.editor.properties.widget.DefaultPanelContext;
import com.esb.plugin.editor.properties.widget.FormBuilder;
import com.esb.plugin.editor.properties.widget.input.InputChangeListener;
import com.esb.plugin.editor.properties.widget.input.script.ScriptInputField;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.util.List;

public class TypeScriptPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(Module module, ComponentPropertyDescriptor propertyDescriptor, PropertyAccessor propertyAccessor, DefaultPanelContext context) {
        List<AutocompleteVariable> autocompleteVariables = propertyDescriptor.getAutocompleteVariables();
        List<AutocompleteContext> autocompleteContexts = propertyDescriptor.getAutocompleteContexts();

        String inputSchema = "inputJsonSchema";
        String propertyValue = context.getPropertyValue(inputSchema);

        // Init Script Input Field with variables and context. Also we must listen on variables
        // connected to  it

        ScriptInputField field = new ScriptInputField(module);
        field.setValue(propertyAccessor.get());
        field.addListener(propertyAccessor::set);

        context.subscribe("inputJsonSchema", new InputChangeListener<String>() {
            @Override
            public void onChange(String value) {
                System.out.println("value");
            }
        });


        return field;
    }

    @Override
    public void addToParent(JComponent parent, JComponent rendered, String label) {
        // If the property type is a complex object, we wrap it in a
        // bordered box with title the name of the object property.
        JBPanel wrappedRenderedComponent = ContainerFactory
                .createObjectTypeContainer(label, rendered);
        FormBuilder.get()
                .addLastField(wrappedRenderedComponent, parent);
    }
}
