package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public interface PropertyTypeRenderer {

    @NotNull
    JComponent render(@NotNull Module module,
                      @NotNull PropertyDescriptor propertyDescriptor,
                      @NotNull PropertyAccessor propertyAccessor,
                      @NotNull ContainerContext context);

    void addToParent(@NotNull JComponent parent,
                     @NotNull JComponent rendered,
                     @NotNull PropertyDescriptor descriptor,
                     @NotNull ContainerContext context);
}
