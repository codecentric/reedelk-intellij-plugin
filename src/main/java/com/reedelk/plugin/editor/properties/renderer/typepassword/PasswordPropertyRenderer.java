package com.reedelk.plugin.editor.properties.renderer.typepassword;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PasswordPropertyRenderer extends AbstractPropertyTypeRenderer {
    @NotNull
    @Override
    public JComponent render(@NotNull Module module, @NotNull ComponentPropertyDescriptor propertyDescriptor, @NotNull PropertyAccessor propertyAccessor, @NotNull ContainerContext context) {
        PasswordInputField field = new PasswordInputField();
        field.setValue(propertyAccessor.get());
        field.addListener(propertyAccessor::set);
        return field;
    }
}
