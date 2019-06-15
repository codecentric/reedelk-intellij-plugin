package com.esb.plugin;

import com.esb.plugin.component.domain.ComponentClass;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.ComponentDefaultDescriptor;
import com.esb.plugin.component.domain.ComponentDescriptor;
import com.esb.plugin.component.type.flowreference.FlowReferenceNode;
import com.esb.plugin.component.type.fork.ForkNode;
import com.esb.plugin.component.type.generic.GenericComponentNode;
import com.esb.plugin.component.type.router.RouterNode;
import com.esb.plugin.component.type.stop.StopNode;
import com.esb.plugin.fixture.*;
import com.esb.plugin.graph.FlowGraphProvider;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.system.component.FlowReference;
import com.esb.system.component.Fork;
import com.esb.system.component.Router;
import com.esb.system.component.Stop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;

import static com.esb.plugin.component.domain.ComponentClass.INBOUND;
import static com.esb.plugin.component.domain.ComponentClass.PROCESSOR;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractGraphTest {

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

    // Router
    protected RouterNode routerNode1;
    protected RouterNode routerNode2;
    protected RouterNode routerNode3;
    protected RouterNode routerNode4;
    protected RouterNode routerNode5;

    // Flow Reference
    protected FlowReferenceNode flowReferenceNode1;
    protected FlowReferenceNode flowReferenceNode2;

    protected FlowGraphProvider provider;


    @BeforeEach
    protected void setUp() {
        provider = new FlowGraphProvider();

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

        routerNode1 = createGraphNodeInstance(Router.class, RouterNode.class);
        routerNode2 = createGraphNodeInstance(Router.class, RouterNode.class);
        routerNode3 = createGraphNodeInstance(Router.class, RouterNode.class);
        routerNode4 = createGraphNodeInstance(Router.class, RouterNode.class);
        routerNode5 = createGraphNodeInstance(Router.class, RouterNode.class);

        flowReferenceNode1 = createGraphNodeInstance(FlowReference.class, FlowReferenceNode.class);
        flowReferenceNode2 = createGraphNodeInstance(FlowReference.class, FlowReferenceNode.class);
    }

    protected static <T extends GraphNode> T createGraphNodeInstance(Class<T> graphNodeClazz, ComponentDescriptor descriptor) {
        ComponentData componentData = new ComponentData(descriptor);
        return createGraphNodeInstance(graphNodeClazz, componentData);
    }

    protected static <T extends GraphNode> T createGraphNodeInstance(Class componentClazz, Class<T> graphNodeClazz, ComponentClass componentClass) {
        ComponentDescriptor descriptor = ComponentDefaultDescriptor.create()
                .fullyQualifiedName(componentClazz.getName())
                .componentClass(componentClass)
                .build();
        return createGraphNodeInstance(graphNodeClazz, descriptor);
    }

    private static <T extends GraphNode> T createGraphNodeInstance(Class componentClazz, Class<T> graphNodeClazz) {
        return createGraphNodeInstance(componentClazz, graphNodeClazz, PROCESSOR);
    }

    private static <T extends GraphNode> T createGraphNodeInstance(Class<T> graphNodeClazz, ComponentData componentData) {
        try {
            return graphNodeClazz.getConstructor(ComponentData.class).newInstance(componentData);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
