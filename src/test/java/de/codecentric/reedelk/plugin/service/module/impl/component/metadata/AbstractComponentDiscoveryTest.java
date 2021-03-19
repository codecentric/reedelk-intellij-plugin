package de.codecentric.reedelk.plugin.service.module.impl.component.metadata;

import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDescriptor;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import de.codecentric.reedelk.plugin.AbstractGraphTest;
import de.codecentric.reedelk.plugin.fixture.ComponentNode2;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.service.module.PlatformModuleService;
import de.codecentric.reedelk.plugin.service.module.impl.component.ComponentContext;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.Trie;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import de.codecentric.reedelk.runtime.api.component.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.ALL_TYPES;
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
