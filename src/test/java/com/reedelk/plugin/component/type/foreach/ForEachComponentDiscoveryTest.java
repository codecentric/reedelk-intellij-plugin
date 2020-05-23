package com.reedelk.plugin.component.type.foreach;

import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.metadata.AbstractComponentDiscoveryTest;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutputDefault;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutputForEach;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class ForEachComponentDiscoveryTest extends AbstractComponentDiscoveryTest {

    private ForEachComponentDiscovery discovery;

    @BeforeEach
    public void setUp() {
        super.setUp();

        graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);
        graph.add(componentNode1, forEachNode1);
        graph.add(forEachNode1, componentNode2);
        graph.add(componentNode2, componentNode3);

        discovery = spy(new ForEachComponentDiscovery(module, moduleService, typeAndTries));
    }

    @Test
    void shouldReturnCorrectForEachPreviousComponentOutput() {
        // Given
        PreviousComponentOutput previousPreviousComponentOutput = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                asList(MyTestType.class.getName(), String.class.getName()),
                "Description of the previous");

        ComponentContext componentContext = mockComponentContext(null);

        doReturn(Optional.of(previousPreviousComponentOutput))
                .when(discovery)
                .discover(componentContext, forEachNode1);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, forEachNode1, componentNode2);

        // Then
        PreviousComponentOutputForEach expected =
                new PreviousComponentOutputForEach(previousPreviousComponentOutput);
        assertThat(maybeActualOutput).contains(expected);
    }
}
