package com.esb.plugin.designer.graph.layout;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.DirectedGraph;
import com.esb.plugin.designer.graph.drawable.ChoiceDrawable;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.GenericComponentDrawable;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class FlowGraphLayoutTest {

    private Drawable n1 = new GenericComponentDrawable(new Component("n1"));
    private Drawable n2 = new GenericComponentDrawable(new Component("n2"));
    private Drawable n3 = new GenericComponentDrawable(new Component("n3"));
    private Drawable choice1 = new ChoiceDrawable(new Component("choice1"));
    private Drawable choice2 = new ChoiceDrawable(new Component("choice2"));

    @Test
    void shouldCorrectlyComputeMax() {
        // Given
        DirectedGraph<Drawable> graph = new DirectedGraph<>(n1);
        graph.putEdge(n1, choice1);
        graph.putEdge(choice1, n2);
        graph.putEdge(choice1, n3);
        graph.putEdge(n2, choice2);

        FlowGraphLayout layout = new FlowGraphLayout(graph);

        // When
        int max = layout.computeMax(n1);

        // Then
        Assertions.assertThat(max).isEqualTo(310);
    }
}
