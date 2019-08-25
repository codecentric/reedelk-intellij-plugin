package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import com.reedelk.plugin.editor.properties.widget.input.BooleanCheckbox;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public class BooleanPropertyRenderer extends AbstractTypePropertyRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor descriptor,
                             @NotNull PropertyAccessor accessor,
                             @NotNull ContainerContext context) {
        boolean selected = accessor.get() == null ?
                Boolean.FALSE :
                accessor.get();

        BooleanCheckbox checkbox = new BooleanCheckbox();
        checkbox.setSelected(selected);
        checkbox.addListener(accessor::set);

        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.add(checkbox, WEST);
        container.add(Box.createHorizontalGlue(), CENTER);
        return container;
    }
}
