package com.esb.plugin.editor.designer;

import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.util.messages.Topic;

public interface ComponentSelectedListener {

    Topic<ComponentSelectedListener> COMPONENT_SELECTED_TOPIC = Topic.create("Component Selected", ComponentSelectedListener.class);

    void onComponentUnSelected();

    void onComponentSelected(Module module, FlowSnapshot snapshot, GraphNode selected);
}
