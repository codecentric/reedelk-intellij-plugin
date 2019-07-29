package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeEnumDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.widget.DisposablePanel;
import com.esb.plugin.editor.properties.widget.input.EnumDropDown;
import com.esb.plugin.editor.properties.widget.input.script.PropertyPanelContext;
import com.intellij.openapi.module.Module;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;
import static javax.swing.Box.createHorizontalGlue;

public class EnumPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(Module module, ComponentPropertyDescriptor propertyDescriptor, PropertyAccessor propertyAccessor, PropertyPanelContext propertyPanelContext) {
        TypeEnumDescriptor propertyType = (TypeEnumDescriptor) propertyDescriptor.getPropertyType();
        EnumDropDown dropDown = new EnumDropDown(propertyType.possibleValues());
        dropDown.setValue(propertyAccessor.get());
        dropDown.addListener(propertyAccessor::set);

        JPanel dropDownContainer = new DisposablePanel(new BorderLayout());
        dropDownContainer.add(dropDown, WEST);
        dropDownContainer.add(createHorizontalGlue(), CENTER);
        return dropDownContainer;
    }
}
