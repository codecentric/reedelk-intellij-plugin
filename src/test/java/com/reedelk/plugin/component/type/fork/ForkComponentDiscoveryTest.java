package com.reedelk.plugin.component.type.fork;

import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.metadata.AbstractComponentDiscoveryTest;
import com.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategy;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutputDefault;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class ForkComponentDiscoveryTest extends AbstractComponentDiscoveryTest {

    private ForkComponentDiscovery discovery;

    @BeforeEach
    public void setUp() {
        super.setUp();
        discovery = spy(new ForkComponentDiscovery(module, moduleService, typeAndTries));
    }

    @Test
    void shouldReturnPreviousPreviousComponentOutput() {
        // Given
        PreviousComponentOutput previousPreviousComponentOutput = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                asList(MyTestType.class.getName(), String.class.getName()),
                "Description of the previous");

        ComponentContext componentContext = mockComponentContext(null);

        doReturn(Optional.of(previousPreviousComponentOutput))
                .when(discovery)
                .discover(componentContext, componentNode2);


        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, componentNode2);

        // Then
        assertThat(maybeActualOutput).contains(DiscoveryStrategy.DEFAULT);
    }
}
