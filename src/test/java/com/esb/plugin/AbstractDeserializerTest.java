package com.esb.plugin;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.DeserializerContext;
import com.esb.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import static java.util.Arrays.stream;
import static org.mockito.Mockito.doReturn;

public abstract class AbstractDeserializerTest extends AbstractGraphTest {

    @Mock
    protected DeserializerContext context;

    protected FlowGraph graph;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        mockContextInstantiateGraphNode(root);

        mockContextInstantiateGraphNode(componentNode1);
        mockContextInstantiateGraphNode(componentNode2);
        mockContextInstantiateGraphNode(componentNode3);
        mockContextInstantiateGraphNode(componentNode4);
        mockContextInstantiateGraphNode(componentNode5);
        mockContextInstantiateGraphNode(componentNode6);
        mockContextInstantiateGraphNode(componentNode7);
        mockContextInstantiateGraphNode(componentNode8);
        mockContextInstantiateGraphNode(componentNode9);
        mockContextInstantiateGraphNode(componentNode10);
        mockContextInstantiateGraphNode(componentNode11);

        mockContextInstantiateGraphNode(stopNode1, stopNode2);
        mockContextInstantiateGraphNode(forkNode1, forkNode2);
        mockContextInstantiateGraphNode(choiceNode1, choiceNode2);
        mockContextInstantiateGraphNode(flowReferenceNode1, flowReferenceNode2);

        graph = graphProvider.createGraph();
        graph.root(root);
    }

    @SafeVarargs
    protected final <T extends GraphNode> void mockContextInstantiateGraphNode(T... graphNodes) {
        GraphNode firstNodeToBeReturned = graphNodes[0];
        String fullyQualifiedName = firstNodeToBeReturned.componentData().getFullyQualifiedName();
        GraphNode[] remaining = new GraphNode[0];
        if (graphNodes.length > 1) {
            remaining = stream(graphNodes, 1, graphNodes.length)
                    .toArray(GraphNode[]::new);
        }
        doReturn(firstNodeToBeReturned, (Object[]) remaining)
                .when(context)
                .instantiateGraphNode(fullyQualifiedName);
    }

}
