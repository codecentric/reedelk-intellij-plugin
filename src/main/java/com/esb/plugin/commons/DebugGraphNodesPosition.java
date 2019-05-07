package com.esb.plugin.commons;

import com.esb.plugin.graph.FlowGraph;
import com.intellij.openapi.diagnostic.Logger;

public class DebugGraphNodesPosition {

    private static final Logger LOG = Logger.getInstance(DebugGraphNodesPosition.class);

    public static void debug(FlowGraph graph) {
        LOG.info("------- Graph Updated --------");
        graph.breadthFirstTraversal(drawable ->
                LOG.info("Name: " +
                        drawable.component().getDisplayName() + ", x: " +
                        drawable.x() + ", y: " +
                        drawable.y()));
    }
}
