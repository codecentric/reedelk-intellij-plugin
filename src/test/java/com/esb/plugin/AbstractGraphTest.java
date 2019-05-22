package com.esb.plugin;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.choice.ChoiceNode;
import com.esb.plugin.component.flowreference.FlowReferenceNode;
import com.esb.plugin.component.fork.ForkNode;
import com.esb.plugin.component.generic.GenericComponentNode;
import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.fixture.*;
import com.esb.plugin.graph.FlowGraphProvider;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.system.component.Choice;
import com.esb.system.component.FlowReference;
import com.esb.system.component.Fork;
import com.esb.system.component.Stop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;

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

    // Fork
    protected ForkNode forkNode1;
    protected ForkNode forkNode2;

    // Choice
    protected ChoiceNode choiceNode1;
    protected ChoiceNode choiceNode2;
    protected ChoiceNode choiceNode3;
    protected ChoiceNode choiceNode4;
    protected ChoiceNode choiceNode5;

    // Flow Reference
    protected FlowReferenceNode flowReferenceNode1;
    protected FlowReferenceNode flowReferenceNode2;

    protected FlowGraphProvider graphProvider;


    @BeforeEach
    protected void setUp() {
        graphProvider = new FlowGraphProvider();

        root = createGraphNodeInstance(ComponentRoot.class, GenericComponentNode.class);

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

        forkNode1 = createGraphNodeInstance(Fork.class, ForkNode.class);
        forkNode2 = createGraphNodeInstance(Fork.class, ForkNode.class);

        choiceNode1 = createGraphNodeInstance(Choice.class, ChoiceNode.class);
        choiceNode2 = createGraphNodeInstance(Choice.class, ChoiceNode.class);
        choiceNode3 = createGraphNodeInstance(Choice.class, ChoiceNode.class);
        choiceNode4 = createGraphNodeInstance(Choice.class, ChoiceNode.class);
        choiceNode5 = createGraphNodeInstance(Choice.class, ChoiceNode.class);

        flowReferenceNode1 = createGraphNodeInstance(FlowReference.class, FlowReferenceNode.class);
        flowReferenceNode2 = createGraphNodeInstance(FlowReference.class, FlowReferenceNode.class);
    }

    protected static <T extends GraphNode> T createGraphNodeInstance(Class<T> graphNodeClazz, ComponentDescriptor descriptor) {
        ComponentData componentData = new ComponentData(descriptor);
        return createGraphNodeInstance(graphNodeClazz, componentData);
    }

    private static <T extends GraphNode> T createGraphNodeInstance(Class componentClazz, Class<T> graphNodeClazz) {
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .fullyQualifiedName(componentClazz.getName())
                .build();
        return createGraphNodeInstance(graphNodeClazz, descriptor);
    }

    private static <T extends GraphNode> T createGraphNodeInstance(Class<T> graphNodeClazz, ComponentData componentData) {
        try {
            return graphNodeClazz.getConstructor(ComponentData.class).newInstance(componentData);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


}
