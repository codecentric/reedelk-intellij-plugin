package com.esb.plugin;

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

    protected FlowGraphImpl graph;

    @BeforeEach
    protected void setUp() {
        graph = new FlowGraphImpl();
        graph.root(root);
    }

    protected ForkNode mockForkNode() {
        return mockComponentGraphNode(Fork.class, ForkNode.class);
    }

    protected StopNode mockStopNode() {
        return mockComponentGraphNode(Stop.class, StopNode.class);
    }

    protected ChoiceNode mockChoiceNode() {
        return mockComponentGraphNode(Choice.class, ChoiceNode.class);
    }

    protected FlowReferenceNode mockFlowReferenceNode() {
        return mockComponentGraphNode(FlowReference.class, FlowReferenceNode.class);
    }

    protected GraphNode mockGenericComponentNode(Class componentNodeClazz) {
        return mockComponentGraphNode(componentNodeClazz, GenericComponentNode.class);
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
