package com.esb.plugin.designer.properties;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.manager.GraphChangeListener;

public class DefaultPropertyTracker implements PropertyTracker {

    private final FlowGraph graph;
    private final String propertyName;
    private final ComponentData componentData;
    private final GraphChangeListener listener;

    public DefaultPropertyTracker(ComponentData componentData, String propertyName, FlowGraph graph, GraphChangeListener listener) {
        this.componentData = componentData;
        this.propertyName = propertyName;
        this.listener = listener;
        this.graph = graph;
    }

    public void onPropertyChange(Object newValue) {
        componentData.set(propertyName, newValue);
        listener.onGraphChanged(graph);
    }

    public String getValueAsString() {
        return (String) componentData.get(propertyName);
    }

}
