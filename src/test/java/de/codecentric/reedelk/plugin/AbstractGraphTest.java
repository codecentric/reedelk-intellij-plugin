package de.codecentric.reedelk.plugin;

import de.codecentric.reedelk.module.descriptor.model.component.ComponentDescriptor;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentType;
import de.codecentric.reedelk.plugin.commons.FontInfoProvider;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.component.type.flowreference.FlowReferenceNode;
import de.codecentric.reedelk.plugin.component.type.foreach.ForEachNode;
import de.codecentric.reedelk.plugin.component.type.fork.ForkNode;
import de.codecentric.reedelk.plugin.component.type.generic.GenericComponentNode;
import de.codecentric.reedelk.plugin.component.type.placeholder.PlaceholderNode;
import de.codecentric.reedelk.plugin.component.type.router.RouterNode;
import de.codecentric.reedelk.plugin.component.type.stop.StopNode;
import de.codecentric.reedelk.plugin.component.type.trycatch.TryCatchNode;
import de.codecentric.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
import de.codecentric.reedelk.plugin.fixture.*;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.FlowGraphImpl;
import de.codecentric.reedelk.plugin.graph.FlowGraphProvider;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.runtime.component.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.UUID;

import static de.codecentric.reedelk.module.descriptor.model.component.ComponentType.INBOUND;
import static de.codecentric.reedelk.module.descriptor.model.component.ComponentType.PROCESSOR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractGraphTest {

    @Mock
    protected Graphics2D graphics;
    @Mock
    protected FontMetrics fontMetrics;
    @Mock
    protected PlaceholderProvider placeholderProvider;

    protected GraphNode root;
    protected GraphNode componentNode1;
    protected GraphNode componentNode2;
    protected GraphNode componentNode3;
    protected GraphNode componentNode4;
    protected GraphNode componentNode5;
    protected GraphNode componentNode6;
    protected GraphNode componentNode7;
    protected GraphNode componentNode8;
    protected GraphNode componentNode9;
    protected GraphNode componentNode10;
    protected GraphNode componentNode11;

    // Stop
    protected StopNode stopNode1;
    protected StopNode stopNode2;
    protected StopNode stopNode3;

    // Fork
    protected ForkNode forkNode1;
    protected ForkNode forkNode2;
    protected ForkNode forkNode3;

    // For Each
    protected ForEachNode forEachNode1;

    // Router
    protected RouterNode routerNode1;
    protected RouterNode routerNode2;
    protected RouterNode routerNode3;
    protected RouterNode routerNode4;
    protected RouterNode routerNode5;

    // TryCatch
    protected TryCatchNode tryCatchNode1;

    // Flow Reference
    protected FlowReferenceNode flowReferenceNode1;
    protected FlowReferenceNode flowReferenceNode2;

    // Placeholder
    protected PlaceholderNode placeholderNode1;
    protected PlaceholderNode placeholderNode2;

    protected TestAwareGraphProvider provider;

    @BeforeAll
    static void beforeAll() {
        FontInfoProvider.testing();
    }

    @BeforeEach
    protected void setUp() {
        provider = new TestAwareGraphProvider();

        root = createGraphNodeInstance(ComponentRoot.class, GenericComponentNode.class, INBOUND);

        componentNode1 = createGraphNodeInstance(ComponentNode1.class, GenericComponentNode.class);
        componentNode2 = createGraphNodeInstance(ComponentNode2.class, GenericComponentNode.class);
        componentNode3 = createGraphNodeInstance(ComponentNode3.class, GenericComponentNode.class);
        componentNode4 = createGraphNodeInstance(ComponentNode4.class, GenericComponentNode.class);
        componentNode5 = createGraphNodeInstance(ComponentNode5.class, GenericComponentNode.class);
        componentNode6 = createGraphNodeInstance(ComponentNode6.class, GenericComponentNode.class);
        componentNode7 = createGraphNodeInstance(ComponentNode7.class, GenericComponentNode.class);
        componentNode8 = createGraphNodeInstance(ComponentNode8.class, GenericComponentNode.class);
        componentNode9 = createGraphNodeInstance(ComponentNode9.class, GenericComponentNode.class);
        componentNode10 = createGraphNodeInstance(ComponentNode10.class, GenericComponentNode.class);
        componentNode11 = createGraphNodeInstance(ComponentNode11.class, GenericComponentNode.class);

        stopNode1 = createGraphNodeInstance(Stop.class, StopNode.class);
        stopNode2 = createGraphNodeInstance(Stop.class, StopNode.class);
        stopNode3 = createGraphNodeInstance(Stop.class, StopNode.class);

        forkNode1 = createGraphNodeInstance(Fork.class, ForkNode.class);
        forkNode2 = createGraphNodeInstance(Fork.class, ForkNode.class);
        forkNode3 = createGraphNodeInstance(Fork.class, ForkNode.class);

        forEachNode1 = createGraphNodeInstance(ForEach.class, ForEachNode.class);

        routerNode1 = createGraphNodeInstance(Router.class, RouterNode.class);
        routerNode2 = createGraphNodeInstance(Router.class, RouterNode.class);
        routerNode3 = createGraphNodeInstance(Router.class, RouterNode.class);
        routerNode4 = createGraphNodeInstance(Router.class, RouterNode.class);
        routerNode5 = createGraphNodeInstance(Router.class, RouterNode.class);

        tryCatchNode1 = createGraphNodeInstance(TryCatch.class, TryCatchNode.class);

        flowReferenceNode1 = createGraphNodeInstance(FlowReference.class, FlowReferenceNode.class);
        flowReferenceNode2 = createGraphNodeInstance(FlowReference.class, FlowReferenceNode.class);

        placeholderNode1 = createGraphNodeInstance(Placeholder.class, PlaceholderNode.class);
        placeholderNode2 = createGraphNodeInstance(Placeholder.class, PlaceholderNode.class);

        lenient().doReturn(Optional.of(placeholderNode1), Optional.of(placeholderNode2)).when(placeholderProvider).get();
        lenient().doReturn(Optional.of(placeholderNode1), Optional.of(placeholderNode2)).when(placeholderProvider).get(Mockito.anyString());

        Rectangle2D sampleTextBounds = new Rectangle(0, 0, 40, 6);
        lenient().doReturn(fontMetrics).when(graphics).getFontMetrics();
        lenient().doReturn(sampleTextBounds).when(fontMetrics).getStringBounds(anyString(), any(Graphics2D.class));
    }

    protected static <T extends GraphNode> T createGraphNodeInstance(Class<?> componentClazz, Class<T> graphNodeClazz, ComponentType componentType) {
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .fullyQualifiedName(componentClazz.getName())
                .type(componentType)
                .build();
        return createGraphNodeInstance(graphNodeClazz, descriptor);
    }

    protected static <T extends GraphNode> T createGraphNodeInstance(Class<T> graphNodeClazz, ComponentDescriptor descriptor) {
        ComponentData componentData = spy(new ComponentData(descriptor));
        return spy(createGraphNodeInstance(graphNodeClazz, componentData));
    }

    private static final int DEFAULT_TOP_HEIGHT = 70;
    private static final int DEFAULT_BOTTOM_HEIGHT = 40;
    protected static final int DEFAULT_HEIGHT = DEFAULT_TOP_HEIGHT + DEFAULT_BOTTOM_HEIGHT;

    protected void mockDefaultNodeHeight(GraphNode node) {
        mockNodeHeight(node, DEFAULT_TOP_HEIGHT, DEFAULT_BOTTOM_HEIGHT);
    }

    protected void mockNodeHeight(GraphNode node, int topHeight, int bottomHeight) {
        lenient().doReturn(topHeight + bottomHeight).when(node).height(graphics);
        lenient().doReturn(topHeight).when(node).topHalfHeight(graphics);
        lenient().doReturn(bottomHeight).when(node).bottomHalfHeight(graphics);
    }

    private static <T extends GraphNode> T createGraphNodeInstance(Class<?> componentClazz, Class<T> graphNodeClazz) {
        return spy(createGraphNodeInstance(componentClazz, graphNodeClazz, PROCESSOR));
    }

    private static <T extends GraphNode> T createGraphNodeInstance(Class<T> graphNodeClazz, ComponentData componentData) {
        try {
            return graphNodeClazz.getConstructor(ComponentData.class).newInstance(componentData);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static class TestAwareGraphProvider implements FlowGraphProvider {

        public FlowGraph createGraph() {
            String id = UUID.randomUUID().toString();
            return createGraph(id);
        }

        @Override
        public FlowGraph createGraph(String id) {
            return new FlowGraphImpl(id);
        }
    }
}
