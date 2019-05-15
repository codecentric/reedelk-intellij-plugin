package com.esb.plugin.designer.properties.renderer;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.designer.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.graph.GraphSnapshot;

public interface PropertyRenderer {

    void render(ComponentPropertyDescriptor descriptor, ComponentData componentData, DefaultPropertiesPanel parent, GraphSnapshot snapshot);

}
