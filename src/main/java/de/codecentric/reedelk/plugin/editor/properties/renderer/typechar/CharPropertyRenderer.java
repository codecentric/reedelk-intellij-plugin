package de.codecentric.reedelk.plugin.editor.properties.renderer.typechar;

import de.codecentric.reedelk.plugin.editor.properties.commons.CharInputField;
import de.codecentric.reedelk.plugin.editor.properties.commons.ContainerFactory;
import de.codecentric.reedelk.plugin.editor.properties.commons.InputField;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
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
