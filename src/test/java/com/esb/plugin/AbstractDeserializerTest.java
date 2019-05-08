package com.esb.plugin;

import com.esb.api.component.Component;
import com.esb.component.Choice;
import com.esb.component.FlowReference;
import com.esb.component.Fork;
import com.esb.component.Stop;
import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.choice.ChoiceNode;
import com.esb.plugin.component.flowreference.FlowReferenceNode;
import com.esb.plugin.component.fork.ForkNode;
import com.esb.plugin.component.generic.GenericComponentNode;
import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.fixture.*;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.deserializer.DeserializerContext;
import com.esb.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractDeserializerTest {

    @Mock
    protected GraphNode root;
    @Mock
    protected DeserializerContext context;

    protected StopNode stopNode1;
    protected StopNode stopNode2;

    protected ForkNode forkNode;

    protected ChoiceNode choiceNode1;
    protected ChoiceNode choiceNode2;

    protected FlowReferenceNode flowReferenceNode1;
    protected FlowReferenceNode flowReferenceNode2;

    protected GraphNode componentNode1;
    protected GraphNode componentNode2;
    protected GraphNode componentNode3;
    protected GraphNode componentNode4;
    protected GraphNode componentNode5;
    protected GraphNode componentNode6;

    protected FlowGraphImpl graph;

    @BeforeEach
    protected void setUp() {
        graph = new FlowGraphImpl();
        graph.root(root);

        mockStopNodes();
        mockChoiceNodes();
        mockFlowReferenceNodes();

        forkNode = mockForkNode();
        componentNode1 = mockGenericComponentNode(ComponentNode1.class);
        componentNode2 = mockGenericComponentNode(ComponentNode2.class);
        componentNode3 = mockGenericComponentNode(ComponentNode3.class);
        componentNode4 = mockGenericComponentNode(ComponentNode4.class);
        componentNode5 = mockGenericComponentNode(ComponentNode5.class);
        componentNode6 = mockGenericComponentNode(ComponentNode6.class);
    }

    private void mockStopNodes() {
        stopNode1 = mockComponentGraphNode(Stop.class, StopNode.class);
        stopNode2 = mockComponentGraphNode(Stop.class, StopNode.class);
        mockInstantiateComponent(Stop.class, stopNode1, stopNode2);
    }

    protected <T extends GraphNode> T mockComponentGraphNode(Class componentClazz, Class<T> graphNodeClazz, ComponentDescriptor descriptor) {
        ComponentData componentData = new ComponentData(descriptor);
        T node = createInstance(graphNodeClazz, componentData);
        mockInstantiateComponent(componentClazz, node);
        return node;
    }

    private void mockChoiceNodes() {
        choiceNode1 = mockComponentGraphNode(Choice.class, ChoiceNode.class);
        choiceNode2 = mockComponentGraphNode(Choice.class, ChoiceNode.class);
        mockInstantiateComponent(Choice.class, choiceNode1, choiceNode2);
    }

    private void mockFlowReferenceNodes() {
        flowReferenceNode1 = mockComponentGraphNode(FlowReference.class, FlowReferenceNode.class);
        flowReferenceNode2 = mockComponentGraphNode(FlowReference.class, FlowReferenceNode.class);
        mockInstantiateComponent(FlowReference.class, flowReferenceNode1, flowReferenceNode2);
    }

    private ForkNode mockForkNode() {
        return mockComponentGraphNode(Fork.class, ForkNode.class);
    }

    private GraphNode mockGenericComponentNode(Class<? extends Component> componentClazz) {
        return mockComponentGraphNode(componentClazz, GenericComponentNode.class);
    }

    private <T extends GraphNode> T mockComponentGraphNode(Class componentClazz, Class<T> graphNodeClazz) {
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .fullyQualifiedName(componentClazz.getName())
                .build();
        return mockComponentGraphNode(componentClazz, graphNodeClazz, descriptor);
    }

    private <T extends GraphNode> T createInstance(Class<T> graphNodeClazz, ComponentData componentData) {
        try {
            return graphNodeClazz.getConstructor(ComponentData.class).newInstance(componentData);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SafeVarargs
    private final <T extends GraphNode> void mockInstantiateComponent(Class componentClazz, T... graphNodes) {
        if (graphNodes.length == 1) {
            doReturn(graphNodes[0])
                    .when(context)
                    .instantiateGraphNode(componentClazz.getName());
        } else {
            GraphNode[] remaining = Arrays
                    .stream(graphNodes, 1, graphNodes.length)
                    .toArray(GraphNode[]::new);
            doReturn(graphNodes[0], remaining)
                    .when(context)
                    .instantiateGraphNode(componentClazz.getName());
        }
    }

}
