package com.esb.plugin.designer.canvas;

import com.esb.plugin.graph.FlowGraph;

class DebugGraphNodesPosition {

    static void debug(FlowGraph graph) {
        System.out.println("\n------- Graph Updated --------");
        graph.breadthFirstTraversal(drawable ->
                System.out.println("Name: " +
                        drawable.component().getDisplayName() + ", x: " +
                        drawable.x() + ", y: " +
                        drawable.y()));
    }
}
