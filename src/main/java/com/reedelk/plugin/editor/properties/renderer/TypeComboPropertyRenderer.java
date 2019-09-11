package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.ComboBox;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeComboDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class TypeComboPropertyRenderer extends AbstractTypePropertyRenderer {

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

        boolean editable = typeComboDescriptor.isEditable();
        String[] comboValues = typeComboDescriptor.getComboValues();

        // TODO: Extract this combo box as a widgetlike the input field...
        ComboBox<String> comboBox = new ComboBox<>(comboValues);
        comboBox.setEditable(editable);
        comboBox.setSelectedItem(propertyAccessor.get());
        comboBox.addItemListener(itemEvent -> {
            if (ItemEvent.SELECTED == itemEvent.getStateChange()) {
                propertyAccessor.set(itemEvent.getItem());
            }
        });

        DisposablePanel panel = new DisposablePanel(new BorderLayout());
        panel.add(comboBox, BorderLayout.WEST);
        return panel;
    }
}
