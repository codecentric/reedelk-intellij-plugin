package com.esb.plugin.component.generic;

import com.esb.plugin.component.Component;
import com.esb.plugin.designer.properties.PropertyBox;
import com.esb.plugin.designer.properties.PropertyRenderer;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.manager.GraphChangeNotifier;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;

public class GenericComponentPropertyRenderer implements PropertyRenderer {

    private final Module module;
    private final FlowGraph graph;
    private final VirtualFile file;

    public GenericComponentPropertyRenderer(final Module module, final FlowGraph graph, final VirtualFile file) {
        this.graph = graph;
        this.module = module;
        this.file = file;
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
