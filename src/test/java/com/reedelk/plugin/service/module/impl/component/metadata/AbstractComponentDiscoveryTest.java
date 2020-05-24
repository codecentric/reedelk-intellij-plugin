package com.reedelk.plugin.service.module.impl.component.metadata;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.fixture.ComponentNode2;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.Trie;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.runtime.api.component.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.ALL_TYPES;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractComponentDiscoveryTest extends AbstractGraphTest {

    @Mock
    protected Module module;
    @Mock
    protected PlatformModuleService moduleService;

    protected TypeAndTries typeAndTries;

    protected Map<String, Trie> defaultTypesAndTries = new HashMap<>();

    protected FlowGraph graph;

    @BeforeEach
    public void setUp() {
        super.setUp();
        typeAndTries = new TypeAndTries(defaultTypesAndTries);
        ALL_TYPES.forEach(trieProvider -> trieProvider.register(typeAndTries, defaultTypesAndTries));
    }

    protected ComponentContext mockComponentContext(ComponentOutputDescriptor outputDescriptor, Class<? extends Component> componentClazz, GraphNode componentNode) {
        ComponentDescriptor descriptor = new ComponentDescriptor();
        descriptor.setOutput(outputDescriptor);
        lenient().doReturn(descriptor)
                .when(moduleService)
                .componentDescriptorOf(componentClazz.getName());
        return spy(new ComponentContext(graph, componentNode));
    }

    protected ComponentContext mockComponentContext(ComponentOutputDescriptor outputDescriptor) {
        return mockComponentContext(outputDescriptor, ComponentNode2.class, componentNode2);
    }

    protected static class MyTestType {
    }
}
