package com.esb.plugin.graph.deserializer;

import com.esb.api.exception.ESBException;
import com.esb.component.Choice;
import com.esb.component.FlowReference;
import com.esb.component.Fork;
import com.esb.component.Stop;
import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.choice.ChoiceDeserializer;
import com.esb.plugin.component.flowreference.FlowReferenceDeserializer;
import com.esb.plugin.component.forkjoin.ForkJoinDeserializer;
import com.esb.plugin.component.generic.GenericComponentDeserializer;
import com.esb.plugin.component.stop.StopDeserializer;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GraphNodeDeserializerFactory {

    private FlowGraph graph;
    private GraphNode parent;
    private DeserializerContext context;
    private JSONObject componentDefinition;

    private static final Class<? extends Deserializer> GENERIC_HANDLER = GenericComponentDeserializer.class;

    private static final Map<String, Class<? extends Deserializer>> COMPONENT_NAME_HANDLER;

    static {
        Map<String, Class<? extends Deserializer>> tmp = new HashMap<>();
        tmp.put(Stop.class.getName(), StopDeserializer.class);
        tmp.put(Fork.class.getName(), ForkJoinDeserializer.class);
        tmp.put(Choice.class.getName(), ChoiceDeserializer.class);
        tmp.put(FlowReference.class.getName(), FlowReferenceDeserializer.class);
        COMPONENT_NAME_HANDLER = Collections.unmodifiableMap(tmp);
    }

    private GraphNodeDeserializerFactory() {
    }

    public GraphNodeDeserializerFactory context(DeserializerContext context) {
        this.context = context;
        return this;
    }

    private static Deserializer instantiateBuilder(FlowGraph graph, DeserializerContext context, Class<? extends Deserializer> builderClazz) {
        try {
            return builderClazz
                    .getDeclaredConstructor(FlowGraph.class, DeserializerContext.class)
                    .newInstance(graph, context);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ESBException(e);
        }
    }

    public GraphNodeDeserializerFactory parent(GraphNode parent) {
        this.parent = parent;
        return this;
    }

    public GraphNodeDeserializerFactory componentDefinition(JSONObject componentDefinition) {
        this.componentDefinition = componentDefinition;
        return this;
    }

    public static GraphNodeDeserializerFactory get() {
        return new GraphNodeDeserializerFactory();
    }

    public GraphNode build() {
        String componentName = JsonParser.Implementor.name(componentDefinition);
        Class<? extends Deserializer> builderClazz = COMPONENT_NAME_HANDLER.getOrDefault(componentName, GENERIC_HANDLER);
        return instantiateBuilder(graph, context, builderClazz).deserialize(parent, componentDefinition);
    }

    public GraphNodeDeserializerFactory graph(FlowGraph graph) {
        this.graph = graph;
        return this;
    }

}
