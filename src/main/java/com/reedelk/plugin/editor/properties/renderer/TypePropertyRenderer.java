package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.FormBuilder;
import com.reedelk.plugin.editor.properties.widget.input.script.ContainerContext;
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
                             @NotNull ContainerContext tracker) {
        FormBuilder.get()
                .addLabel(descriptor.getDisplayName(), parent)
                .addLastField(rendered, parent);
    }
}
