package com.esb.plugin.designer.properties;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.manager.GraphChangeNotifier;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;

public class PropertyTracker {

    private final String propertyName;
    private final ComponentData componentData;

    public PropertyTracker(ComponentData componentData, String propertyName) {
        this.componentData = componentData;
        this.propertyName = propertyName;
    }

    public void onPropertyChange(Module module, FlowGraph graph, VirtualFile file, Object newValue) {
        componentData.set(propertyName, newValue);
        GraphChangeNotifier notifier = module.getMessageBus().syncPublisher(GraphChangeNotifier.TOPIC);
        notifier.onChange(graph, file);
    }

    public String getValueAsString() {
        return (String) componentData.get(propertyName);
    }

}