package com.reedelk.plugin.component.type.fork;

import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.metadata.AbstractComponentDiscoveryTest;
import com.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategy;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

class ForkComponentDiscoveryTest extends AbstractComponentDiscoveryTest {

    private ForkComponentDiscovery discovery;

    @BeforeEach
    public void setUp() {
        super.setUp();
        discovery = spy(new ForkComponentDiscovery(module, moduleService, typeAndTries));
    }

    @Test
    void shouldReturnDefaultPreviousComponentOutputWhenComponentDescriptorOutputIsNull() {
        // Given
        ComponentContext componentContext = mockComponentContext(null);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, componentNode2);

        // Then
        assertThat(maybeActualOutput).contains(DiscoveryStrategy.DEFAULT);
    }
}
