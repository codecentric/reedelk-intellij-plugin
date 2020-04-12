package com.reedelk.plugin.component.type.unknown;

import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBEmptyBorder;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.editor.properties.renderer.AbstractComponentPropertiesRenderer;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;

import java.awt.*;

public class UnknownComponentPropertiesRenderer extends AbstractComponentPropertiesRenderer {

    private static final JBEmptyBorder BORDER = JBUI.Borders.empty(5, 10, 0, 0);
    private static final int BORDER_TOP_BOTTOM = 5;

    public UnknownComponentPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public DisposablePanel render(GraphNode node) {
        ComponentData componentData = node.componentData();

        DisposablePanel propertiesPanel = new DisposablePanel(new GridBagLayout());
        node.componentData().getPropertiesDescriptors().forEach(propertyDescriptor -> {
            String displayName = propertyDescriptor.getDisplayName();
            String propertyValue = componentData.get(propertyDescriptor.getName());

            JBLabel label = new JBLabel(propertyValue);
            label.setBorder(JBUI.Borders.empty(BORDER_TOP_BOTTOM,0));
            FormBuilder.get()
                    .addLabel(displayName, propertiesPanel)
                    .addLastField(label, propertiesPanel);
        });

        propertiesPanel.setBorder(BORDER);
        return ContainerFactory.pushTop(propertiesPanel);
    }
}
