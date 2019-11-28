package com.reedelk.plugin.editor.properties.renderer.typecombo;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeComboDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static java.awt.BorderLayout.WEST;

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
            propertyAccessor.set(propertyDescriptor.getDefaultValue());
        }

        String prototype = typeComboDescriptor.getPrototype();
        boolean editable = typeComboDescriptor.isEditable();
        String[] comboValues = typeComboDescriptor.getComboValues();

        Arrays.sort(comboValues); // sort ascending order
        StringDropDown dropDown = new StringDropDown(comboValues, editable, prototype);
        dropDown.setValue(propertyAccessor.get());
        dropDown.addListener(propertyAccessor::set);

        JPanel dropDownContainer = new DisposablePanel(new BorderLayout());
        dropDownContainer.add(dropDown, WEST);
        return dropDownContainer;
    }
}
