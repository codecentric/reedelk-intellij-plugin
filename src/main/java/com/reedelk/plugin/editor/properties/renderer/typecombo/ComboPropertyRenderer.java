package com.reedelk.plugin.editor.properties.renderer.typecombo;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeComboDescriptor;
import com.reedelk.plugin.commons.InitPropertyValue;
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
                             @NotNull PropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        TypeComboDescriptor typeComboDescriptor = propertyDescriptor.getType();

        // We set the default value if not present
        if (propertyAccessor.get() == null) {
            Object initValue = InitPropertyValue.of(propertyDescriptor);
            propertyAccessor.set(initValue);
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
