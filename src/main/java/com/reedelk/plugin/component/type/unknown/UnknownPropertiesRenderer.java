package com.reedelk.plugin.component.type.unknown;

import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.editor.properties.renderer.AbstractNodePropertiesRenderer;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.editor.properties.widget.FormBuilder;
import com.reedelk.plugin.editor.properties.widget.PropertiesPanelHolder;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.runtime.commons.JsonParser;

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
