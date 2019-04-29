package com.esb.plugin.graph.deserializer;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.component.Component;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.GraphNode;
import com.esb.plugin.graph.TestJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractBuilderTest {

    @Mock
    protected GraphNode root;
    @Mock
    protected BuilderContext context;

    protected FlowGraphImpl graph;

    @BeforeEach
    protected void setUp() {
        graph = new FlowGraphImpl();
        graph.root(root);
    }

    public void assertSuccessorsAre(FlowGraph graph, GraphNode target, String... successorsComponentNames) {
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

    public void assertPredecessorsAre(FlowGraph graph, GraphNode target, String... predecessorsComponentsNames) {
        int numberOfPredecessors = predecessorsComponentsNames.length;

        List<GraphNode> predecessors = graph.predecessors(target);
        assertThat(predecessors).isNotNull().hasSize(numberOfPredecessors);

        Collection<String> toBeFound = new ArrayList<>(Arrays.asList(predecessorsComponentsNames));
        predecessors.forEach(predecessor -> {
            String componentName = predecessor.component().getFullyQualifiedName();
            toBeFound.remove(componentName);
        });

        assertThat(toBeFound).isEmpty();
    }

    public void assertThatComponentHasName(GraphNode target, String expectedName) {
        assertThat(target).isNotNull();
        Component component = target.component();
        assertThat(component).isNotNull();
        assertThat(component.getFullyQualifiedName()).isEqualTo(expectedName);
    }

    public GraphNode firstSuccessorOf(FlowGraph graph, GraphNode target) {
        return graph.successors(target).stream().findFirst().get();
    }

    public GraphNode getNodeHavingComponentName(Collection<GraphNode> drawables, String componentName) {
        for (GraphNode drawable : drawables) {
            Component component = drawable.component();
            if (componentName.equals(component.getFullyQualifiedName())) {
                return drawable;
            }
        }
        throw new RuntimeException("Could not find: " + componentName);
    }

    public Component mockComponent(String fullyQualifiedName) {
        Component component = new Component(ComponentDescriptor.create()
                .fullyQualifiedName(fullyQualifiedName)
                .build());

        doReturn(component)
                .when(context)
                .instantiateComponent(fullyQualifiedName);

        return component;
    }

    String readJson(TestJson testJson) {
        URL url = testJson.url();
        return FileUtils.readFrom(url);
    }
}
