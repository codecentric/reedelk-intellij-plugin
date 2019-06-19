package com.esb.plugin.editor.properties.renderer.type;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.widget.input.BooleanCheckbox;
import com.esb.plugin.graph.FlowSnapshot;
import com.intellij.openapi.module.Module;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public class BooleanPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(Module module, ComponentPropertyDescriptor descriptor, PropertyAccessor accessor, FlowSnapshot snapshot) {
        boolean selected = accessor.get() == null ?
                Boolean.FALSE :
                (Boolean) accessor.get();

        BooleanCheckbox checkbox = new BooleanCheckbox();
        checkbox.setSelected(selected);
        checkbox.addListener(valueAsString -> {
            accessor.set(valueAsString);
            snapshot.onDataChange();
        });
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.add(checkbox, WEST);
        container.add(Box.createHorizontalGlue(), CENTER);
        return container;
    }
}
