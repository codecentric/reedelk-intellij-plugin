package com.esb.plugin.designer.graph.manager;

import com.esb.plugin.designer.graph.FlowGraph;
import com.intellij.util.messages.Topic;

/**
 * Topic to notify that the graph has been changed from the designer.
 */
public interface GraphChangeNotifier {

    Topic<GraphChangeNotifier> TOPIC = Topic.create("Graph Change Notifier", GraphChangeNotifier.class);

    default void onChange(FlowGraph graph) {
    }

}
