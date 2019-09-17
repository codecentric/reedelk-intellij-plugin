package com.reedelk.plugin.component.type.unknown;

import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.commons.Labels;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.editor.properties.renderer.AbstractNodePropertiesRenderer;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.editor.properties.widget.FormBuilder;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.runtime.commons.JsonParser;

import java.awt.*;

public class UnknownPropertiesRenderer extends AbstractNodePropertiesRenderer {

    public UnknownPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public DisposablePanel render(GraphNode node) {
        ComponentData componentData = node.componentData();
        String unknownImplementorClazz = componentData.get(JsonParser.Implementor.name());
        DisposablePanel propertiesPanel = new DisposablePanel(new GridBagLayout());

        FormBuilder.get()
                .addLabel(Labels.UNKNOWN_COMPONENT, propertiesPanel)
                .addLastField(new JBLabel(unknownImplementorClazz), propertiesPanel);

        return propertiesPanel;
    }
}
