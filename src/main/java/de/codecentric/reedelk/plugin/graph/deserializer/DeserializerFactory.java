package de.codecentric.reedelk.plugin.graph.deserializer;

import de.codecentric.reedelk.plugin.component.type.flowreference.FlowReferenceDeserializer;
import de.codecentric.reedelk.plugin.component.type.flowreference.FlowReferenceNode;
import de.codecentric.reedelk.plugin.component.type.foreach.ForEachDeserializer;
import de.codecentric.reedelk.plugin.component.type.foreach.ForEachNode;
import de.codecentric.reedelk.plugin.component.type.generic.GenericComponentDeserializer;
import de.codecentric.reedelk.plugin.component.type.generic.GenericComponentNode;
import de.codecentric.reedelk.plugin.component.type.placeholder.PlaceholderDeserializer;
import de.codecentric.reedelk.plugin.component.type.placeholder.PlaceholderNode;
import de.codecentric.reedelk.plugin.component.type.trycatch.TryCatchDeserializer;
import de.codecentric.reedelk.plugin.component.type.trycatch.TryCatchNode;
import de.codecentric.reedelk.plugin.component.type.unknown.UnknownDeserializer;
import de.codecentric.reedelk.plugin.component.type.unknown.UnknownNode;
import de.codecentric.reedelk.plugin.exception.PluginException;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.component.type.fork.ForkDeserializer;
import de.codecentric.reedelk.plugin.component.type.fork.ForkNode;
import de.codecentric.reedelk.plugin.component.type.router.RouterDeserializer;
import de.codecentric.reedelk.plugin.component.type.router.RouterNode;
import de.codecentric.reedelk.plugin.component.type.stop.StopDeserializer;
import de.codecentric.reedelk.plugin.component.type.stop.StopNode;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static de.codecentric.reedelk.runtime.api.commons.Preconditions.checkState;
import static de.codecentric.reedelk.runtime.commons.JsonParser.Implementor;

public class DeserializerFactory {

    private FlowGraph graph;
    private DeserializerContext context;
    private JSONObject componentDefinition;

    private static final Map<Class<?>, Class<? extends Deserializer>> COMPONENT_DESERIALIZER_MAP;
    static {
        Map<Class<?>, Class<? extends Deserializer>> tmp = new HashMap<>();
        tmp.put(StopNode.class, StopDeserializer.class);
        tmp.put(ForkNode.class, ForkDeserializer.class);
        tmp.put(RouterNode.class, RouterDeserializer.class);
        tmp.put(ForEachNode.class, ForEachDeserializer.class);
        tmp.put(UnknownNode.class, UnknownDeserializer.class);
        tmp.put(TryCatchNode.class, TryCatchDeserializer.class);
        tmp.put(PlaceholderNode.class, PlaceholderDeserializer.class);
        tmp.put(FlowReferenceNode.class, FlowReferenceDeserializer.class);
        tmp.put(GenericComponentNode.class, GenericComponentDeserializer.class);
        COMPONENT_DESERIALIZER_MAP = Collections.unmodifiableMap(tmp);
    }

    private DeserializerFactory() {
    }

    public static DeserializerFactory get() {
        return new DeserializerFactory();
    }

    public DeserializerFactory graph(FlowGraph graph) {
        this.graph = graph;
        return this;
    }

    public DeserializerFactory context(DeserializerContext context) {
        this.context = context;
        return this;
    }

    public DeserializerFactory componentDefinition(JSONObject componentDefinition) {
        this.componentDefinition = componentDefinition;
        return this;
    }

    public Deserializer build() {
        checkState(graph != null, "graph must not be null");
        checkState(context != null, "context must not be null");
        checkState(componentDefinition != null, "component definition must not be null");

        String componentName = Implementor.name(componentDefinition);

        GraphNode current = context.instantiateGraphNode(componentName);

        Class<? extends Deserializer> deserializerClazz = deserializerOf(current);

        return instantiate(deserializerClazz, current);
    }

    private Deserializer instantiate(Class<? extends Deserializer> deserializerClazz, GraphNode current) {
        try {
            return deserializerClazz
                    .getDeclaredConstructor(FlowGraph.class, GraphNode.class, DeserializerContext.class)
                    .newInstance(graph, current, context);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            throw new PluginException("Could not instantiate deserializer class=" + deserializerClazz.getName(), exception);
        }
    }

    private static Class<? extends Deserializer> deserializerOf(GraphNode current) {
        Class<?> nodeClass = COMPONENT_DESERIALIZER_MAP.keySet()
                .stream()
                .filter(aClass -> aClass.isAssignableFrom(current.getClass()))
                .findFirst()
                .orElseThrow(() -> new PluginException("Deserializer for graph node=[" + current.getClass() + "] not found."));
        return COMPONENT_DESERIALIZER_MAP.get(nodeClass);
    }
}
