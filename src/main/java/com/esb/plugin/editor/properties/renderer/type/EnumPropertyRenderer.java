package com.esb.plugin.editor.properties.renderer.type;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeEnumDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.widget.input.EnumDropDown;
import com.intellij.openapi.module.Module;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;
import static javax.swing.Box.createHorizontalGlue;

public class EnumPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(Module module, ComponentPropertyDescriptor propertyDescriptor, PropertyAccessor propertyAccessor) {
        TypeEnumDescriptor propertyType = (TypeEnumDescriptor) propertyDescriptor.getPropertyType();
        EnumDropDown dropDown = new EnumDropDown(propertyType.possibleValues());
        dropDown.setValue(propertyAccessor.get());
        dropDown.addListener(propertyAccessor::set);

        JPanel dropDownContainer = new JPanel(new BorderLayout());
        dropDownContainer.add(dropDown, WEST);
        dropDownContainer.add(createHorizontalGlue(), CENTER);
        return dropDownContainer;
    }
}
