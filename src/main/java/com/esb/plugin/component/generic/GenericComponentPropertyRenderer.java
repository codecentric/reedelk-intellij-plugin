package com.esb.plugin.component.generic;

import com.esb.plugin.component.Component;
import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.designer.properties.PropertyBox;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.manager.GraphChangeNotifier;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;

public class GenericComponentPropertyRenderer extends AbstractPropertyRenderer {

    public GenericComponentPropertyRenderer(Module module, FlowGraph graph, VirtualFile file) {
        super(module, graph, file);
    }

    @Override
    public void render(JBPanel panel, Component component) {
        component.getPropertiesNames().forEach(propertyName -> {

            PropertyBox propertyBox = new PropertyBox(propertyName);
            propertyBox.setText((String) component.getData(propertyName));
            propertyBox.addListener(newText -> {
                component.setPropertyValue(propertyName, newText);
                GraphChangeNotifier notifier = module.getMessageBus().syncPublisher(GraphChangeNotifier.TOPIC);
                notifier.onChange(graph, file);
            });

            panel.add(propertyBox);
        });
    }
}
