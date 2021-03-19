package de.codecentric.reedelk.plugin.editor.properties.renderer;

import de.codecentric.reedelk.plugin.editor.properties.commons.ContainerFactory;
import de.codecentric.reedelk.plugin.editor.properties.commons.DisposablePanel;
import de.codecentric.reedelk.plugin.editor.properties.commons.InputField;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractNumericPropertyRenderer<T> extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public RenderedComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        InputField<T> inputField = getInputField(propertyDescriptor.getHintValue());
        inputField.setValue(propertyAccessor.get());
        inputField.addListener(propertyAccessor::set);
        DisposablePanel rendered = ContainerFactory.pushLeft(inputField);
        return RenderedComponent.create(rendered, value -> inputField.setValue((T) value));
    }

    protected abstract InputField<T> getInputField(String hint);

}
