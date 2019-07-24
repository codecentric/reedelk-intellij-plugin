package com.esb.plugin.component.type.unknown;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.properties.renderer.AbstractNodePropertiesRenderer;
import com.esb.plugin.editor.properties.widget.DisposablePanel;
import com.esb.plugin.editor.properties.widget.FormBuilder;
import com.esb.plugin.editor.properties.widget.PropertiesPanelHolder;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBLabel;

public class UnknownPropertiesRenderer extends AbstractNodePropertiesRenderer {

    public UnknownPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public DisposablePanel render(GraphNode node) {
        ComponentData componentData = node.componentData();
        String unknownImplementorClazz = componentData.get(JsonParser.Implementor.name());
        PropertiesPanelHolder propertiesPanel = new PropertiesPanelHolder(componentData, snapshot);

        FormBuilder.get()
                .addLabel("Unknown implementor", propertiesPanel)
                .addLastField(new JBLabel(unknownImplementorClazz), propertiesPanel);

        return propertiesPanel;
    }
}
