package de.codecentric.reedelk.plugin.editor.properties.renderer.typecombo;

import de.codecentric.reedelk.plugin.editor.properties.commons.ContainerFactory;
import de.codecentric.reedelk.plugin.editor.properties.commons.DisposablePanel;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.property.ComboDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static java.util.Optional.ofNullable;

public class ComboPropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public RenderedComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        ComboDescriptor typeComboDescriptor = propertyDescriptor.getType();

        String prototype = typeComboDescriptor.getPrototype();
        boolean editable = typeComboDescriptor.isEditable();
        String[] comboValues = typeComboDescriptor.getComboValues();

        Arrays.sort(comboValues); // sort ascending order
        StringDropDown dropDown = new StringDropDown(module, comboValues, editable, prototype);

        // We set the default value in the dropdown if present.
        ofNullable(propertyDescriptor.getDefaultValue()).ifPresent(dropDown::setValue);

        // We only add it if it is not null.
        ofNullable(propertyAccessor.get())
                .ifPresent(value -> dropDown.setValue(propertyAccessor.get()));

        dropDown.addListener(propertyAccessor::set);
        DisposablePanel component = ContainerFactory.pushLeft(dropDown);
        return RenderedComponent.create(component, dropDown::setValue);
    }
}
