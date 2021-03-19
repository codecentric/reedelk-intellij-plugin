package de.codecentric.reedelk.plugin.editor.designer.action.add;

import de.codecentric.reedelk.plugin.AbstractGraphTest;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.FlowGraphChangeAware;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
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
