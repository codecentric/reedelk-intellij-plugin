package com.reedelk.plugin.editor.properties.renderer.typestring;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.editor.properties.commons.InputField;
import com.reedelk.plugin.editor.properties.commons.StringInputField;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import org.jetbrains.annotations.NotNull;

public class StringPropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public RenderedComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        InputField<String> field = new StringInputField(propertyDescriptor.getHintValue());
        field.setValue(propertyAccessor.get());
        field.addListener(propertyAccessor::set);

        return RenderedComponent.create(field, value -> field.setValue((String) value));
    }
}
