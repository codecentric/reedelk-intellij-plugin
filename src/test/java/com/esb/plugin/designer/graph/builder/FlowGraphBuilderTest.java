package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.Drawable;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.TestJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class FlowGraphBuilderTest {

    @Test
    void shouldBuildFlowWithChoiceCorrectly() {
        // Given
        String json = readJson(TestJson.FLOW_WITH_CHOICE);
        FlowGraphBuilder builder = new FlowGraphBuilder(json);

        // When
        FlowGraph graph = builder.get();

        // Then
        assertThat(graph).isNotNull();

        Drawable root = graph.root();
        assertThatComponentHasName(root, "com.esb.rest.component.RestListener");
        assertSuccessorsAre(graph, root, "com.esb.component.Choice");

        Drawable choice = findFirstSuccessor(graph, root);
        assertSuccessorsAre(graph, choice,
                "com.esb.core.component.SetPayload1",
                "com.esb.core.component.SetPayload2",
                "com.esb.core.component.SetPayload3");


        Drawable setPayload1 = getNode(graph.successors(choice), "com.esb.core.component.SetPayload1");
        Drawable stopDrawable = findFirstSuccessor(graph, setPayload1);
        assertSuccessorsAre(graph, stopDrawable, "com.esb.rest.component.SetHeader");

        Drawable setHeaderDrawable = findFirstSuccessor(graph, stopDrawable);
        assertSuccessorsAre(graph, setHeaderDrawable, "com.esb.rest.component.SetStatus");

        Drawable setStatusDrawable = findFirstSuccessor(graph, setHeaderDrawable);
        assertSuccessorsAre(graph, setStatusDrawable, "com.esb.logger.component.LogComponent");


        Drawable logComponentDrawable = findFirstSuccessor(graph, setStatusDrawable);
        assertThat(graph.successors(logComponentDrawable)).isEmpty();
    }

    private Drawable findFirstSuccessor(FlowGraph graph, Drawable target) {
        return graph.successors(target).stream().findFirst().get();
    }

    private void assertSuccessorsAre(FlowGraph graph, Drawable target, String... successorsComponentNames) {
        int numberOfSuccessors = successorsComponentNames.length;
        List<Drawable> successors = graph.successors(target);
        assertThat(successors).isNotNull();
        assertThat(successors).hasSize(numberOfSuccessors);
        Collection<String> toBeFound = new ArrayList<>(Arrays.asList(successorsComponentNames));
        for (Drawable successor : successors) {
            String componentName = successor.component().getName();
            toBeFound.remove(componentName);
        }
        assertThat(toBeFound).isEmpty();
    }

    private Drawable getNode(Collection<Drawable> drawableCollection, String componentName) {
        for (Drawable successor : drawableCollection) {
            Component component = successor.component();
            if (componentName.equals(component.getName())) {
                return successor;
            }
        }
        throw new RuntimeException("Could not find: " + componentName);
    }

    private void assertThatComponentHasName(Drawable target, String expectedName) {
        assertThat(target).isNotNull();
        Component component = target.component();
        assertThat(component).isNotNull();
        assertThat(component.getName()).isEqualTo(expectedName);
    }

    private String readJson(TestJson testJson) {
        URL url = testJson.url();
        return FileUtils.readFrom(url);
    }

}