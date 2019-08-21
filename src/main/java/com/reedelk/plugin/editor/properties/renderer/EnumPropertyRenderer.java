package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeEnumDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.editor.properties.widget.input.EnumDropDown;
import com.reedelk.plugin.editor.properties.widget.input.script.ContainerContext;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;
import static javax.swing.Box.createHorizontalGlue;

public class EnumPropertyRenderer implements TypePropertyRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        TypeEnumDescriptor propertyType = (TypeEnumDescriptor) propertyDescriptor.getPropertyType();

        EnumDropDown dropDown = new EnumDropDown(propertyType.possibleValues());

        // It the value is null, we set the default value. But probably
        // it  should be set in the accessor ??
        if (propertyAccessor.get() == null) {
            Object defaultValue = propertyDescriptor.getDefaultValue();
            dropDown.setValue(defaultValue);
        } else {
            dropDown.setValue(propertyAccessor.get());
        }

        dropDown.addListener(propertyAccessor::set);

        JPanel dropDownContainer = new DisposablePanel(new BorderLayout());
        dropDownContainer.add(dropDown, WEST);
        dropDownContainer.add(createHorizontalGlue(), CENTER);
        return dropDownContainer;
    }
}
