package com.reedelk.plugin.graph.serializer;

import com.reedelk.plugin.component.type.flowreference.FlowReferenceSerializer;
import com.reedelk.plugin.component.type.foreach.ForEachSerializer;
import com.reedelk.plugin.component.type.fork.ForkSerializer;
import com.reedelk.plugin.component.type.generic.GenericComponentSerializer;
import com.reedelk.plugin.component.type.placeholder.PlaceholderSerializer;
import com.reedelk.plugin.component.type.router.RouterSerializer;
import com.reedelk.plugin.component.type.stop.StopSerializer;
import com.reedelk.plugin.component.type.trycatch.TryCatchSerializer;
import com.reedelk.plugin.component.type.unknown.Unknown;
import com.reedelk.plugin.component.type.unknown.UnknownSerializer;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.runtime.component.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.reedelk.runtime.api.commons.Preconditions.checkState;

public class SerializerFactory {

    private static final Serializer GENERIC_SERIALIZER = new GenericComponentSerializer();

    private static final Map<String, Serializer> COMPONENT_SERIALIZER_MAP;
    static {
        Map<String, Serializer> tmp = new HashMap<>();
        tmp.put(Stop.class.getName(), new StopSerializer());
        tmp.put(Fork.class.getName(), new ForkSerializer());
        tmp.put(Router.class.getName(), new RouterSerializer());
        tmp.put(Unknown.class.getName(), new UnknownSerializer());
        tmp.put(ForEach.class.getName(), new ForEachSerializer());
        tmp.put(TryCatch.class.getName(), new TryCatchSerializer());
        tmp.put(Placeholder.class.getName(), new PlaceholderSerializer());
        tmp.put(FlowReference.class.getName(), new FlowReferenceSerializer());
        COMPONENT_SERIALIZER_MAP = Collections.unmodifiableMap(tmp);
    }

    private GraphNode node;

    private SerializerFactory() {
    }

    public static SerializerFactory get() {
        return new SerializerFactory();
    }

    public SerializerFactory node(GraphNode node) {
        this.node = node;
        return this;
    }

    public Serializer build() {
        checkState(node != null, "graph node must not be null");
        String fullyQualifiedName = node.componentData().getFullyQualifiedName();
        return COMPONENT_SERIALIZER_MAP
                .getOrDefault(fullyQualifiedName, GENERIC_SERIALIZER);
    }
}
