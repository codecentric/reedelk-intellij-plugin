package com.reedelk.plugin.editor.properties.renderer.typeenum;

import com.intellij.openapi.module.Module;
import com.reedelk.component.descriptor.ComponentPropertyDescriptor;
import com.reedelk.component.descriptor.TypeEnumDescriptor;
import com.reedelk.plugin.commons.DefaultPropertyValue;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.WEST;

public class EnumPropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        TypeEnumDescriptor propertyType = propertyDescriptor.getPropertyType();

        EnumDropDown dropDown = new EnumDropDown(propertyType.getNameAndDisplayNameMap());

        // We set the default value if not present
        if (propertyAccessor.get() == null) {
            Object defaultValue = DefaultPropertyValue.of(propertyDescriptor);
            propertyAccessor.set(defaultValue);
        }

        dropDown.setValue(propertyAccessor.get());
        dropDown.addListener(propertyAccessor::set);

        JPanel dropDownContainer = new DisposablePanel(new BorderLayout());
        dropDownContainer.add(dropDown, WEST);
        return dropDownContainer;
    }
}
