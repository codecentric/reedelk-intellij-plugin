package com.reedelk.plugin.component.type.unknown;

import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBLabel;
import com.reedelk.component.descriptor.ComponentData;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.editor.properties.renderer.AbstractComponentPropertiesRenderer;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;

import java.awt.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class UnknownComponentPropertiesRenderer extends AbstractComponentPropertiesRenderer {

    public UnknownComponentPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public DisposablePanel render(GraphNode node) {
        ComponentData componentData = node.componentData();
        String unknownImplementorClazz = componentData.get(Implementor.name());
        DisposablePanel propertiesPanel = new DisposablePanel(new GridBagLayout());

        FormBuilder.get()
                .addLabel(message("component.unknown"), propertiesPanel)
                .addLastField(new JBLabel(unknownImplementorClazz), propertiesPanel);

        return propertiesPanel;
    }
}
