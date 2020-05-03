package com.reedelk.plugin.editor.properties.renderer;

import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.editor.properties.commons.JComponentHolder;
import com.reedelk.plugin.editor.properties.commons.PropertyTitleLabel;
import com.reedelk.plugin.editor.properties.commons.WhenVisibilityApplier;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class AbstractPropertyTypeRenderer implements PropertyTypeRenderer {

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull PropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {

        PropertyTitleLabel propertyTitleLabel = new PropertyTitleLabel(descriptor);

        // Apply visibility conditions to the label and the rendered component
        WhenVisibilityApplier.on(descriptor.getWhens(), context, rendered, propertyTitleLabel);

        // Add the component and its property title label to the parent container.
        FormBuilder.get()
                .addLabel(propertyTitleLabel, parent)
                .addLastField(rendered, parent);

        // Add the component to the context.
        context.addComponent(new JComponentHolder(rendered));
    }
}
