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
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.deserializer.DeserializerContext;
import com.esb.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    protected void assertThatComponentHasName(GraphNode target, String expectedName) {
        assertThat(target).isNotNull();
        ComponentData componentData = target.component();
        assertThat(componentData).isNotNull();
        assertThat(componentData.getFullyQualifiedName()).isEqualTo(expectedName);
    }

    protected void assertSuccessorsAre(FlowGraph graph, GraphNode target, String... successorsComponentNames) {
        int numberOfSuccessors = successorsComponentNames.length;
        List<GraphNode> successors = graph.successors(target);
        assertThat(successors).isNotNull().hasSize(numberOfSuccessors);

        Collection<String> toBeFound = new ArrayList<>(Arrays.asList(successorsComponentNames));
        successors.forEach(successor -> {
            String componentName = successor.component().getFullyQualifiedName();
            toBeFound.remove(componentName);
        });

        assertThat(toBeFound).isEmpty();
    }

    protected GraphNode firstSuccessorOf(FlowGraph graph, GraphNode target) {
        return graph.successors(target).stream().findFirst().get();
    }

    protected GraphNode getNodeHavingComponentName(Collection<GraphNode> drawables, String componentName) {
        for (GraphNode drawable : drawables) {
            ComponentData componentData = drawable.component();
            if (componentName.equals(componentData.getFullyQualifiedName())) {
                return drawable;
            }
        }
        throw new RuntimeException("Could not find: " + componentName);
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
