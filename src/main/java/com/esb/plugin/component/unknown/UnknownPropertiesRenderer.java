package com.esb.plugin.component.unknown;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.ComponentData;
import com.esb.plugin.designer.properties.renderer.node.AbstractNodePropertiesRenderer;
import com.esb.plugin.designer.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.designer.properties.widget.FormBuilder;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;

public class UnknownPropertiesRenderer extends AbstractNodePropertiesRenderer {

    public UnknownPropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        ComponentData componentData = node.componentData();
        String unknownImplementorClazz = (String) componentData.get(JsonParser.Implementor.name());
        DefaultPropertiesPanel propertiesPanel = new DefaultPropertiesPanel();

        FormBuilder.get()
                .addLabel("Unknown implementor", propertiesPanel)
                .addLastField(new JBLabel(unknownImplementorClazz), propertiesPanel);

        return propertiesPanel;
    }
}
