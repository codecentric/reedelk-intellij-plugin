package com.esb.plugin.designer.properties;

import com.esb.plugin.component.Component;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.manager.GraphChangeNotifier;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;

public class PropertyTracker {

    private final String propertyName;
    private final Component component;

    public PropertyTracker(Component component, String propertyName) {
        this.component = component;
        this.propertyName = propertyName;
    }

    public void onPropertyChange(Module module, FlowGraph graph, VirtualFile file, Object newValue) {
        component.setPropertyValue(propertyName, newValue);
        GraphChangeNotifier notifier = module.getMessageBus().syncPublisher(GraphChangeNotifier.TOPIC);
        notifier.onChange(graph, file);
    }

    public String getValueAsString() {
        return (String) component.getData(propertyName);
    }

}