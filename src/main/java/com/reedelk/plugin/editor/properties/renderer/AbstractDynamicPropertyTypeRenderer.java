package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.renderer.commons.DynamicValueField;
import com.reedelk.plugin.editor.properties.renderer.commons.DynamicValueInputFieldAdapter;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public abstract class AbstractDynamicPropertyTypeRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module, @NotNull ComponentPropertyDescriptor propertyDescriptor, @NotNull PropertyAccessor propertyAccessor, @NotNull ContainerContext context) {
        String hint = propertyDescriptor.getHintValue();

        DynamicValueField field =
                new DynamicValueField(module, inputFieldAdapter(hint));
        field.setValue(propertyAccessor.get());
        field.addListener(propertyAccessor::set);
        return field;
    }

    protected abstract DynamicValueInputFieldAdapter inputFieldAdapter(String hint);

}
