package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import com.reedelk.plugin.editor.properties.widget.input.InputField;
import com.reedelk.plugin.editor.properties.widget.input.StringInputField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class StringPropertyRenderer extends AbstractTypePropertyRenderer {
    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        InputField<String> field = new StringInputField("/resource/{id}");
        field.setValue(propertyAccessor.get());
        field.addListener(propertyAccessor::set);
        return field;
    }
}
