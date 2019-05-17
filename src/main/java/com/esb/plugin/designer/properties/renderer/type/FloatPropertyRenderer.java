package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.designer.properties.widget.FloatInputField;
import com.esb.plugin.graph.GraphSnapshot;

import javax.swing.*;

public class FloatPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(ComponentPropertyDescriptor descriptor, ComponentData componentData, GraphSnapshot snapshot) {
        return new FloatInputField();
    }
}