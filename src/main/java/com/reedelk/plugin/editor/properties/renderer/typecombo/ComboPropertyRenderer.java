package com.reedelk.plugin.editor.properties.renderer.typecombo;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeComboDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;

import static java.util.Optional.ofNullable;

public class ComboPropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        TypeComboDescriptor typeComboDescriptor = propertyDescriptor.getType();

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
        return ContainerFactory.pushLeft(dropDown);
    }
}
