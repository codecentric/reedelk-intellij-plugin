package com.reedelk.plugin.graph.action.add;

import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.editor.designer.action.add.FlowActionNodeAdd;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphChangeAware;
import com.reedelk.plugin.graph.node.GraphNode;
import org.mockito.Mock;

import java.awt.*;
import java.awt.image.ImageObserver;

abstract class BaseFlowActionNodeAddTest extends AbstractGraphTest {

    @Mock
    private ImageObserver observer;

    FlowGraphChangeAware addNodeToGraph(FlowGraph graph, GraphNode dropped, Point dropPoint) {
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);
        FlowActionNodeAdd action = new FlowActionNodeAdd(dropPoint, dropped, graphics, observer, placeholderProvider);
        action.execute(modifiableGraph);
        return modifiableGraph;
    }
}
