package com.esb.plugin.designer.graph.layout;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.DirectedGraph;
import com.esb.plugin.designer.graph.drawable.ChoiceDrawable;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.GenericComponentDrawable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlowGraphLayoutTest {

    @Test
    void shouldComputeSubTreeHeightForGenericRootCorrectly() {
        // Given
        Drawable n1 = new GenericComponentDrawable(new Component("n1"));

        DirectedGraph<Drawable> graph = new DirectedGraph<>(n1);

        // When
        int height = FlowGraphLayout.computeSubTreeHeight(graph, n1);

        // Then
        assertThat(height).isEqualTo(130);
    }

    @Test
    void shouldComputeSubTreeHeightForScopedDrawableCorrectly() {
        // Given
        Drawable choice = new ChoiceDrawable(new Component("choice"));

        DirectedGraph<Drawable> graph = new DirectedGraph<>(choice);

        // When
        int height = FlowGraphLayout.computeSubTreeHeight(graph, choice);

        // Then
        assertThat(height).isEqualTo(130 + 5 + 5);
    }

    @Test
    void shouldComputeSubTreeHeightForNestedChoiceCorrectly() {
        // Given
        Drawable root = new GenericComponentDrawable(new Component("root"));
        Drawable choice1 = new ChoiceDrawable(new Component("choice1"));
        Drawable choice2 = new ChoiceDrawable(new Component("choice2"));

        DirectedGraph<Drawable> graph = new DirectedGraph<>(root);
        graph.putEdge(root, choice1);
        graph.putEdge(choice1, choice2);

        // When
        int height = FlowGraphLayout.computeSubTreeHeight(graph, root);

        // Then: plus 2 padding/s for two choices
        assertThat(height).isEqualTo(130 + 5 + 5 + 5 + 5);
    }

    @Test
    void shouldComputeSubTreeHeightForMultipleNextedChoices() {
        // Given
        Drawable root = new GenericComponentDrawable(new Component("root"));
        Drawable choice1 = new ChoiceDrawable(new Component("choice1"));
        Drawable choice2 = new ChoiceDrawable(new Component("choice2"));
        Drawable choice3 = new ChoiceDrawable(new Component("choice3"));

        DirectedGraph<Drawable> graph = new DirectedGraph<>(root);
        graph.putEdge(root, choice1);
        graph.putEdge(choice1, choice2);
        graph.putEdge(choice1, choice3);

        // When
        int height = FlowGraphLayout.computeSubTreeHeight(graph, root);

        // Then: 2 choices on top of each other + 3 padding/s for 3 choices
        assertThat(height).isEqualTo(130 + 130 + 5 + 5 + 5 + 5 + 5 + 5);
    }

}
