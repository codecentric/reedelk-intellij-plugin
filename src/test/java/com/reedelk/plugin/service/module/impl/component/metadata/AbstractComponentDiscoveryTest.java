package com.reedelk.plugin.service.module.impl.component.metadata;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.fixture.ComponentNode2;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractComponentDiscoveryTest extends AbstractGraphTest {

    @Mock
    protected Module module;
    @Mock
    protected PlatformModuleService moduleService;

    protected TypeAndTries typeAndTries;

    protected FlowGraph graph;

    @BeforeEach
    public void setUp() {
        super.setUp();
        typeAndTries = new TypeAndTries();
    }

    protected ComponentContext mockComponentContext(ComponentOutputDescriptor outputDescriptor) {
        ComponentDescriptor descriptor = new ComponentDescriptor();
        descriptor.setOutput(outputDescriptor);
        lenient().doReturn(descriptor)
                .when(moduleService)
                .componentDescriptorOf(ComponentNode2.class.getName());
        return Mockito.spy(new ComponentContext(graph, componentNode3));
    }

    protected static class MyTestType {
    }
}
