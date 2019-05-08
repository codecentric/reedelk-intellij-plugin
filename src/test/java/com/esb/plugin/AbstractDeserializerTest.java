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
    protected ChoiceNode choiceNode;
    protected FlowReferenceNode flowReferenceNode;

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
        forkNode = mockForkNode();
        choiceNode = mockChoiceNode();
        flowReferenceNode = mockFlowReferenceNode();
        componentNode1 = mockGenericComponentNode(ComponentNode1.class);
        componentNode2 = mockGenericComponentNode(ComponentNode2.class);
        componentNode3 = mockGenericComponentNode(ComponentNode3.class);
        componentNode4 = mockGenericComponentNode(ComponentNode4.class);
        componentNode5 = mockGenericComponentNode(ComponentNode5.class);
        componentNode6 = mockGenericComponentNode(ComponentNode6.class);
    }

    protected ForkNode mockForkNode() {
        return mockComponentGraphNode(Fork.class, ForkNode.class);
    }

    protected void mockStopNodes() {
        ComponentData componentData1 = componentDataFrom(Stop.class);
        this.stopNode1 = createInstance(StopNode.class, componentData1);

        ComponentData componentData2 = componentDataFrom(Stop.class);
        this.stopNode2 = createInstance(StopNode.class, componentData2);

        doReturn(stopNode1, stopNode2).when(context)
                .instantiateGraphNode(Stop.class.getName());
    }

    protected ChoiceNode mockChoiceNode() {
        return mockComponentGraphNode(Choice.class, ChoiceNode.class);
    }

    protected FlowReferenceNode mockFlowReferenceNode() {
        return mockComponentGraphNode(FlowReference.class, FlowReferenceNode.class);
    }

    protected GraphNode mockGenericComponentNode(Class<? extends Component> componentClazz) {
        return mockComponentGraphNode(componentClazz, GenericComponentNode.class);
    }

    private <T extends GraphNode> T mockComponentGraphNode(Class componentClazz, Class<T> graphNodeClazz) {
        ComponentData componentData = componentDataFrom(componentClazz);
        T node = createInstance(graphNodeClazz, componentData);
        doReturn(node).when(context)
                .instantiateGraphNode(componentClazz.getName());
        return node;
    }

    private <T extends GraphNode> T createInstance(Class<T> graphNodeClazz, ComponentData componentData) {
        try {
            return graphNodeClazz.getConstructor(ComponentData.class)
                    .newInstance(componentData);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private ComponentData componentDataFrom(Class componentNodeClazz) {
        return new ComponentData(ComponentDescriptor.create()
                .fullyQualifiedName(componentNodeClazz.getName())
                .build());
    }

}
