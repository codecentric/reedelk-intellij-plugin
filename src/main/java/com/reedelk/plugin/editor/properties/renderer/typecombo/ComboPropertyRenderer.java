package com.reedelk.plugin.editor.properties.renderer.typecombo;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.ComponentPropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeComboDescriptor;
import com.reedelk.plugin.commons.DefaultPropertyValue;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;

public class ComboPropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        TypeComboDescriptor typeComboDescriptor = propertyDescriptor.getPropertyType();

        // We set the default value if not present
        if (propertyAccessor.get() == null) {
            Object defaultValue = DefaultPropertyValue.of(propertyDescriptor);
            propertyAccessor.set(defaultValue);
        }

        String prototype = typeComboDescriptor.getPrototype();
        boolean editable = typeComboDescriptor.isEditable();
        String[] comboValues = typeComboDescriptor.getComboValues();

        Arrays.sort(comboValues); // sort ascending order
        StringDropDown dropDown = new StringDropDown(module, comboValues, editable, prototype);
        dropDown.setValue(propertyAccessor.get());
        dropDown.addListener(propertyAccessor::set);

        return ContainerFactory.pushLeft(dropDown);
    }
}