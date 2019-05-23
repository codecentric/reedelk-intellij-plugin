package com.esb.plugin.component.type.unknown;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.properties.renderer.node.AbstractNodePropertiesRenderer;
import com.esb.plugin.editor.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.editor.properties.widget.FormBuilder;
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