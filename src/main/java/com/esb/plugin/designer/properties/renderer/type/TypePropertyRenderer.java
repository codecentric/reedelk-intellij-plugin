package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.graph.GraphSnapshot;

import javax.swing.*;

public interface TypePropertyRenderer {

    JComponent render(ComponentPropertyDescriptor descriptor, ComponentData componentData, GraphSnapshot snapshot);

}
