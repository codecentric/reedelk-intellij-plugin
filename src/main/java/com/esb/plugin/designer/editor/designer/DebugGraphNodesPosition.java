package com.esb.plugin.designer.editor.designer;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.ComponentAware;

public class DebugGraphNodesPosition {

    public static void debug(FlowGraph graph) {
        System.out.println("------- Graph Updated --------");
        graph.breadthFirstTraversal(drawable ->
                System.out.println("Name: " + ((ComponentAware) drawable).component().getDisplayName() + ", x: " + drawable.x() + ", y: " + drawable.y()));
    }
}
