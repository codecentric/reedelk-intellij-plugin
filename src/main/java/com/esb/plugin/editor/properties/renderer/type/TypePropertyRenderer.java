package com.esb.plugin.editor.properties.renderer.type;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.graph.FlowSnapshot;

import javax.swing.*;

public interface TypePropertyRenderer {

    JComponent render(ComponentPropertyDescriptor descriptor, ComponentData componentData, FlowSnapshot snapshot);

}
