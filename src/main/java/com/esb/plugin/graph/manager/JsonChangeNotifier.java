package com.esb.plugin.graph.manager;

import com.esb.plugin.graph.FlowGraph;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.Topic;

/**
 * Topic to notify that the json has been changed from the editor.
 */
public interface JsonChangeNotifier {

    Topic<JsonChangeNotifier> TOPIC = Topic.create("Json Change Notifier", JsonChangeNotifier.class);

    default void onChange(FlowGraph graph, VirtualFile relatedFile) {
    }

}
