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
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractComponentDiscoveryTest extends AbstractGraphTest {

    @Mock
    protected Module module;
    @Mock
    protected PlatformModuleService moduleService;

    protected TypeAndTries typeAndTries;

    private FlowGraph graph;

    @BeforeEach
    public void setUp() {
        super.setUp();
        graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);
        graph.add(componentNode1, componentNode2);
        graph.add(componentNode2, componentNode3);

        typeAndTries = new TypeAndTries();
    }

    protected ComponentContext mockComponentContext(ComponentOutputDescriptor outputDescriptor) {
        ComponentDescriptor descriptor = new ComponentDescriptor();
        descriptor.setOutput(outputDescriptor);
        doReturn(descriptor)
                .when(moduleService)
                .componentDescriptorOf(ComponentNode2.class.getName());
        return new ComponentContext(graph, componentNode3);
    }

    protected static class MyTestType {
    }
}
