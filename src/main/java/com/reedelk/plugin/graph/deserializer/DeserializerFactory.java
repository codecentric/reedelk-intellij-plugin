package com.reedelk.plugin.graph.deserializer;

import com.reedelk.plugin.component.type.flowreference.FlowReferenceDeserializer;
import com.reedelk.plugin.component.type.fork.ForkDeserializer;
import com.reedelk.plugin.component.type.generic.GenericComponentDeserializer;
import com.reedelk.plugin.component.type.placeholder.PlaceholderDeserializer;
import com.reedelk.plugin.component.type.router.RouterDeserializer;
import com.reedelk.plugin.component.type.stop.StopDeserializer;
import com.reedelk.plugin.component.type.trycatch.TryCatchDeserializer;
import com.reedelk.plugin.component.type.unknown.Unknown;
import com.reedelk.plugin.component.type.unknown.UnknownDeserializer;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.commons.JsonParser;
import com.reedelk.runtime.component.*;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.reedelk.runtime.api.commons.Preconditions.checkState;

public class DeserializerFactory {

    private FlowGraph graph;
    private DeserializerContext context;
    private JSONObject componentDefinition;

    private static final Class<? extends Deserializer> GENERIC_DESERIALIZER = GenericComponentDeserializer.class;
    private static final Map<String, Class<? extends Deserializer>> COMPONENT_DESERIALIZER_MAP;

    static {
        Map<String, Class<? extends Deserializer>> tmp = new HashMap<>();
        tmp.put(Stop.class.getName(), StopDeserializer.class);
        tmp.put(Fork.class.getName(), ForkDeserializer.class);
        tmp.put(Router.class.getName(), RouterDeserializer.class);
        tmp.put(Unknown.class.getName(), UnknownDeserializer.class);
        tmp.put(TryCatch.class.getName(), TryCatchDeserializer.class);
        tmp.put(Placeholder.class.getName(), PlaceholderDeserializer.class);
        tmp.put(FlowReference.class.getName(), FlowReferenceDeserializer.class);
        COMPONENT_DESERIALIZER_MAP = Collections.unmodifiableMap(tmp);
    }

    private DeserializerFactory() {
    }

    public static DeserializerFactory get() {
        return new DeserializerFactory();
    }

    public DeserializerFactory context(DeserializerContext context) {
        this.context = context;
        return this;
    }

    public DeserializerFactory componentDefinition(JSONObject componentDefinition) {
        this.componentDefinition = componentDefinition;
        return this;
    }

    public DeserializerFactory graph(FlowGraph graph) {
        this.graph = graph;
        return this;
    }

    public Deserializer build() {
        checkState(graph != null, "graph must not be null");
        checkState(context != null, "context must not be null");
        checkState(componentDefinition != null, "component definition must not be null");

        String componentName = JsonParser.Implementor.name(componentDefinition);
        Class<? extends Deserializer> deserializerClazz = COMPONENT_DESERIALIZER_MAP.getOrDefault(componentName, GENERIC_DESERIALIZER);
        return instantiate(deserializerClazz);
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
