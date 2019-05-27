package com.esb.plugin.graph.deserializer;

import com.esb.api.exception.ESBException;
import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.type.choice.ChoiceDeserializer;
import com.esb.plugin.component.type.flowreference.FlowReferenceDeserializer;
import com.esb.plugin.component.type.fork.ForkDeserializer;
import com.esb.plugin.component.type.generic.GenericComponentDeserializer;
import com.esb.plugin.component.type.placeholder.PlaceholderDeserializer;
import com.esb.plugin.component.type.stop.StopDeserializer;
import com.esb.plugin.component.type.unknown.UnknownDeserializer;
import com.esb.plugin.graph.FlowGraph;
import com.esb.system.component.*;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.esb.internal.commons.Preconditions.checkState;

public class GraphDeserializerFactory {

    private FlowGraph graph;
    private DeserializerContext context;
    private JSONObject componentDefinition;

    private static final Class<? extends Deserializer> GENERIC_DESERIALIZER = GenericComponentDeserializer.class;
    private static final Map<String, Class<? extends Deserializer>> COMPONENT_DESERIALIZER_MAP;
    static {
        Map<String, Class<? extends Deserializer>> tmp = new HashMap<>();
        tmp.put(Stop.class.getName(), StopDeserializer.class);
        tmp.put(Fork.class.getName(), ForkDeserializer.class);
        tmp.put(Choice.class.getName(), ChoiceDeserializer.class);
        tmp.put(Unknown.class.getName(), UnknownDeserializer.class);
        tmp.put(Placeholder.class.getName(), PlaceholderDeserializer.class);
        tmp.put(FlowReference.class.getName(), FlowReferenceDeserializer.class);
        COMPONENT_DESERIALIZER_MAP = Collections.unmodifiableMap(tmp);
    }

    private GraphDeserializerFactory() {
    }

    public GraphDeserializerFactory context(DeserializerContext context) {
        this.context = context;
        return this;
    }

    public GraphDeserializerFactory componentDefinition(JSONObject componentDefinition) {
        this.componentDefinition = componentDefinition;
        return this;
    }

    public static GraphDeserializerFactory get() {
        return new GraphDeserializerFactory();
    }

    public Deserializer build() {
        checkState(graph != null, "graph must not be null");
        checkState(context != null, "context must not be null");
        checkState(componentDefinition != null, "component definition must not be null");

        String componentName = JsonParser.Implementor.name(componentDefinition);
        Class<? extends Deserializer> builderClazz = COMPONENT_DESERIALIZER_MAP.getOrDefault(componentName, GENERIC_DESERIALIZER);
        return instantiate(builderClazz);
    }

    public GraphDeserializerFactory graph(FlowGraph graph) {
        this.graph = graph;
        return this;
    }

    private Deserializer instantiate(Class<? extends Deserializer> deserializerClazz) {
        try {
            return deserializerClazz
                    .getDeclaredConstructor(FlowGraph.class, DeserializerContext.class)
                    .newInstance(graph, context);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ESBException(e);
        }
    }

}
