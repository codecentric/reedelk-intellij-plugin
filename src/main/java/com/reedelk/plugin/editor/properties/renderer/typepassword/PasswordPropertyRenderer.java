package com.reedelk.plugin.editor.properties.renderer.typepassword;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import org.jetbrains.annotations.NotNull;

public class PasswordPropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public RenderedComponent render(@NotNull Module module, @NotNull PropertyDescriptor propertyDescriptor, @NotNull PropertyAccessor propertyAccessor, @NotNull ContainerContext context) {
        PasswordInputField field = new PasswordInputField();
        field.setValue(propertyAccessor.get());
        field.addListener(propertyAccessor::set);
        return RenderedComponent.create(field, field::setValue);
    }
}
