package com.esb.plugin.designer.editor.designer;

import com.esb.plugin.designer.graph.FlowGraph;

public class DebugGraphNodesPosition {

    public static void debug(FlowGraph graph) {
        System.out.println("\n------- Graph Updated --------");
        graph.breadthFirstTraversal(drawable ->
                System.out.println("Name: " +
                        drawable.component().getDisplayName() + ", x: " +
                        drawable.x() + ", y: " +
                        drawable.y()));
    }
}
