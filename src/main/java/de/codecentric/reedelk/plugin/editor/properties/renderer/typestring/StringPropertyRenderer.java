package de.codecentric.reedelk.plugin.editor.properties.renderer.typestring;

import de.codecentric.reedelk.plugin.editor.properties.commons.InputField;
import de.codecentric.reedelk.plugin.editor.properties.commons.StringInputField;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
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
