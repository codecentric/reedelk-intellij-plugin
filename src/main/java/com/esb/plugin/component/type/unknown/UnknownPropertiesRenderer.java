package com.esb.plugin.component.type.unknown;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.properties.renderer.AbstractNodePropertiesRenderer;
import com.esb.plugin.editor.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.editor.properties.widget.FormBuilder;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;

public class UnknownPropertiesRenderer extends AbstractNodePropertiesRenderer {

    public UnknownPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public JBPanel render(GraphNode node) {
        ComponentData componentData = node.componentData();
        String unknownImplementorClazz = componentData.get(JsonParser.Implementor.name());
        DefaultPropertiesPanel propertiesPanel = new DefaultPropertiesPanel();

        FormBuilder.get()
                .addLabel("Unknown implementor", propertiesPanel)
                .addLastField(new JBLabel(unknownImplementorClazz), propertiesPanel);

        return propertiesPanel;
    }
}
