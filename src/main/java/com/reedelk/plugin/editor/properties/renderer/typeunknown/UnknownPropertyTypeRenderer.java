package com.reedelk.plugin.editor.properties.renderer.typeunknown;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.renderer.PropertyTypeRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class UnknownPropertyTypeRenderer implements PropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull ComponentPropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {
        throw new UnsupportedOperationException();
    }
}
