package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import com.reedelk.plugin.editor.properties.widget.FormBuilder;
import com.reedelk.plugin.editor.properties.widget.JComponentHolder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public interface TypePropertyRenderer {

    @NotNull
    JComponent render(@NotNull Module module,
                      @NotNull ComponentPropertyDescriptor propertyDescriptor,
                      @NotNull PropertyAccessor propertyAccessor,
                      @NotNull ContainerContext context);

    default void addToParent(@NotNull JComponent parent,
                             @NotNull JComponent rendered,
                             @NotNull ComponentPropertyDescriptor descriptor,
                             @NotNull ContainerContext context) {

        // Add the component to the parent container.
        FormBuilder.get()
                .addLabel(descriptor.getDisplayName(), parent)
                .addLastField(rendered, parent);

        // Add the component to the context.
        context.addComponent(new JComponentHolder(rendered));
    }
}
