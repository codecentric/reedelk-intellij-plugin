package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import com.reedelk.plugin.editor.properties.widget.input.PasswordInputField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PasswordPropertyRenderer extends AbstractTypePropertyRenderer {
    @NotNull
    @Override
    public JComponent render(@NotNull Module module, @NotNull ComponentPropertyDescriptor propertyDescriptor, @NotNull PropertyAccessor propertyAccessor, @NotNull ContainerContext context) {
        PasswordInputField field = new PasswordInputField();
        field.setValue(propertyAccessor.get());
        field.addListener(propertyAccessor::set);
        return field;
    }
}
