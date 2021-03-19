package de.codecentric.reedelk.plugin.graph.deserializer;

import de.codecentric.reedelk.plugin.AbstractGraphTest;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import static java.util.Arrays.stream;
import static org.mockito.Mockito.lenient;

public abstract class AbstractNodeDeserializerTest extends AbstractGraphTest {

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

        mockContextInstantiateGraphNode(stopNode1, stopNode2, stopNode3);
        mockContextInstantiateGraphNode(forkNode1, forkNode2, forkNode3);
        mockContextInstantiateGraphNode(routerNode1, routerNode2, routerNode3, routerNode4, routerNode5);
        mockContextInstantiateGraphNode(tryCatchNode1);
        mockContextInstantiateGraphNode(flowReferenceNode1, flowReferenceNode2);

        graph = provider.createGraph();
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
        lenient().doReturn(firstNodeToBeReturned, (Object[]) remaining)
                .when(context)
                .instantiateGraphNode(fullyQualifiedName);
    }
}
