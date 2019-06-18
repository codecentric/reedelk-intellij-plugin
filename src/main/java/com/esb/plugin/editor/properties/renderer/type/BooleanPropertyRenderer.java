package com.esb.plugin.editor.properties.renderer.type;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.widget.input.BooleanCheckbox;
import com.esb.plugin.graph.FlowSnapshot;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public class BooleanPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(ComponentPropertyDescriptor descriptor, PropertyAccessor accessor, FlowSnapshot snapshot) {
        BooleanCheckbox checkbox = new BooleanCheckbox();
        checkbox.addListener(valueAsString -> {
            accessor.set(valueAsString);
            snapshot.onDataChange();
        });
        checkbox.setValue(accessor.get());

        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.add(checkbox, WEST);
        container.add(Box.createHorizontalGlue(), CENTER);
        return container;
    }
}
