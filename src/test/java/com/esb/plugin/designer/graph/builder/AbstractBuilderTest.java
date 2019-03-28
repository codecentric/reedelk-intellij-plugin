package com.esb.plugin.designer.graph.builder;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.TestJson;
import com.esb.plugin.designer.graph.drawable.Drawable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
abstract class AbstractBuilderTest {

    Drawable firstSuccessorOf(FlowGraph graph, Drawable target) {
        return graph.successors(target).stream().findFirst().get();
    }

    void assertSuccessorsAre(FlowGraph graph, Drawable target, String... successorsComponentNames) {
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

    void assertPredecessorsAre(FlowGraph graph, Drawable target, String... predecessorsComponentsNames) {
        int numberOfPredecessors = predecessorsComponentsNames.length;
        List<Drawable> predecessors = graph.predecessors(target);
        assertThat(predecessors).isNotNull();
        assertThat(predecessors).hasSize(numberOfPredecessors);
        Collection<String> toBeFound = new ArrayList<>(Arrays.asList(predecessorsComponentsNames));
        for (Drawable successor : predecessors) {
            String componentName = successor.component().getName();
            toBeFound.remove(componentName);
        }
        assertThat(toBeFound).isEmpty();
    }

    Drawable getDrawableWithComponentName(Collection<Drawable> drawableCollection, String componentName) {
        for (Drawable successor : drawableCollection) {
            Component component = successor.component();
            if (componentName.equals(component.getName())) {
                return successor;
            }
        }
        throw new RuntimeException("Could not find: " + componentName);
    }

    void assertThatComponentHasName(Drawable target, String expectedName) {
        assertThat(target).isNotNull();
        Component component = target.component();
        assertThat(component).isNotNull();
        assertThat(component.getName()).isEqualTo(expectedName);
    }

    String readJson(TestJson testJson) {
        URL url = testJson.url();
        return FileUtils.readFrom(url);
    }
}
