package com.reedelk.plugin.editor.properties.renderer.typechar;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.editor.properties.commons.CharInputField;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.InputField;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import org.jetbrains.annotations.NotNull;

public class CharPropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public RenderedComponent render(
            @NotNull Module module,
            @NotNull PropertyDescriptor propertyDescriptor,
            @NotNull PropertyAccessor propertyAccessor,
            @NotNull ContainerContext context) {

        InputField<String> field = new CharInputField(propertyDescriptor.getHintValue());
        field.setValue(propertyAccessor.get());
        field.addListener(propertyAccessor::set);

        return RenderedComponent.create(ContainerFactory.pushLeft(field));
    }
}
