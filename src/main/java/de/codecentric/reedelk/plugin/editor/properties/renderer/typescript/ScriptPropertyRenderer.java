package de.codecentric.reedelk.plugin.editor.properties.renderer.typescript;

import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import org.jetbrains.annotations.NotNull;

public class ScriptPropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public RenderedComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {
        ScriptInputField inputField = new ScriptInputField(module, propertyDescriptor, propertyAccessor, context);
        return RenderedComponent.create(inputField);
    }
}
