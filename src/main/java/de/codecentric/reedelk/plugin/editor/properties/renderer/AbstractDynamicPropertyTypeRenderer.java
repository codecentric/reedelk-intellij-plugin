package de.codecentric.reedelk.plugin.editor.properties.renderer;

import de.codecentric.reedelk.plugin.editor.properties.commons.DynamicValueField;
import de.codecentric.reedelk.plugin.editor.properties.commons.DynamicValueInputFieldAdapter;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractDynamicPropertyTypeRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public RenderedComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {
        String hint = propertyDescriptor.getHintValue();
        String componentPropertyPath = context.getPropertyPath(propertyDescriptor.getName());

        Object initValue = propertyAccessor.get();

        DynamicValueInputFieldAdapter inputFieldAdapter = inputFieldAdapter(hint);
        DynamicValueField field =
                new DynamicValueField(module, inputFieldAdapter, componentPropertyPath, context, initValue);
        field.addListener(propertyAccessor::set);
        return RenderedComponent.create(field);
    }

    protected abstract DynamicValueInputFieldAdapter inputFieldAdapter(String hint);

}
