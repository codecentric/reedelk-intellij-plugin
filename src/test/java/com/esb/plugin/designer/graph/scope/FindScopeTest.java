package com.esb.plugin.designer.graph.scope;

import com.esb.plugin.designer.graph.AbstractGraphTest;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FindScopeTest extends AbstractGraphTest {

    @Test
    void shouldReturnCorrectScope() {
        // Given
        FlowGraph graph = new FlowGraphImpl();
        graph.add(null, root);
        graph.add(root, choice1);
        graph.add(choice1, choice2);
        graph.add(choice1, n1);

        choice1.addToScope(choice2);
        choice2.addToScope(n1);

        // When
        Optional<ScopedDrawable> actualScope = FindScope.of(graph, n1);

        // Then
        assertThat(actualScope.isPresent()).isTrue();
        assertThat(actualScope.get()).isEqualTo(choice2);
    }
}
