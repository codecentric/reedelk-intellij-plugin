package com.reedelk.plugin.editor.properties.renderer.typestring;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.commons.InputField;
import com.reedelk.plugin.editor.properties.renderer.commons.StringInputField;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class StringPropertyRenderer extends AbstractPropertyTypeRenderer {
    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        InputField<String> field = new StringInputField(propertyDescriptor.getHintValue());
        field.setValue(propertyAccessor.get());
        field.addListener(propertyAccessor::set);
        return field;
    }
}
