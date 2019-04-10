package com.esb.plugin.designer.graph.layout;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.FlowGraphLayout;
import com.esb.plugin.designer.graph.drawable.ChoiceDrawable;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.GenericComponentDrawable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FlowGraphLayoutTest {

    @Mock
    private Graphics2D graphics;

    @Test
    void shouldComputeSubTreeHeightForGenericRootCorrectly() {
        // Given
        Drawable n1 = new GenericComponentDrawable(new Component("n1"));

        FlowGraphImpl graph = new FlowGraphImpl();
        graph.add(null, n1);

        // When
        int height = FlowGraphLayout.computeSubTreeHeight(graph, n1, graphics);

        // Then
        assertThat(height).isEqualTo(130);
    }

    @Test
    void shouldComputeSubTreeHeightForScopedDrawableCorrectly() {
        // Given
        Drawable choice = new ChoiceDrawable(new Component("choice"));

        FlowGraphImpl graph = new FlowGraphImpl();
        graph.add(null, choice);

        // When
        int height = FlowGraphLayout.computeSubTreeHeight(graph, choice, graphics);

        // Then
        assertThat(height).isEqualTo(130 + 5 + 5);
    }

    @Test
    void shouldComputeSubTreeHeightForNestedChoiceCorrectly() {
        // Given
        Drawable root = new GenericComponentDrawable(new Component("root"));
        Drawable choice1 = new ChoiceDrawable(new Component("choice1"));
        Drawable choice2 = new ChoiceDrawable(new Component("choice2"));

        FlowGraphImpl graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choice1);
        graph.add(choice1, choice2);

        // When
        int height = FlowGraphLayout.computeSubTreeHeight(graph, root, graphics);

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

        FlowGraphImpl graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choice1);
        graph.add(choice1, choice2);
        graph.add(choice1, choice3);

        // When
        int height = FlowGraphLayout.computeSubTreeHeight(graph, root, graphics);

        // Then: 2 choices on top of each other + 3 padding/s for 3 choices
        assertThat(height).isEqualTo(130 + 130 + 5 + 5 + 5 + 5 + 5 + 5);
    }

}
