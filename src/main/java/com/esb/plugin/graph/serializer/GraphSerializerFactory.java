package com.esb.plugin.graph.serializer;

import com.esb.api.exception.ESBException;
import com.esb.plugin.component.choice.ChoiceSerializer;
import com.esb.plugin.component.flowreference.FlowReferenceSerializer;
import com.esb.plugin.component.fork.ForkSerializer;
import com.esb.plugin.component.generic.GenericComponentSerializer;
import com.esb.plugin.component.stop.StopSerializer;
import com.esb.plugin.component.unknown.UnknownSerializer;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.system.component.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.esb.internal.commons.Preconditions.checkState;

public class GraphSerializerFactory {

    private static final Class<? extends Serializer> GENERIC_SERIALIZER = GenericComponentSerializer.class;
    private static final Map<String, Class<? extends Serializer>> COMPONENT_SERIALIZER_MAP;

    static {
        Map<String, Class<? extends Serializer>> tmp = new HashMap<>();
        tmp.put(Stop.class.getName(), StopSerializer.class);
        tmp.put(Fork.class.getName(), ForkSerializer.class);
        tmp.put(Choice.class.getName(), ChoiceSerializer.class);
        tmp.put(Unknown.class.getName(), UnknownSerializer.class);
        tmp.put(FlowReference.class.getName(), FlowReferenceSerializer.class);
        COMPONENT_SERIALIZER_MAP = Collections.unmodifiableMap(tmp);
    }

    private GraphNode node;

    private GraphSerializerFactory() {
    }

    public static GraphSerializerFactory get() {
        return new GraphSerializerFactory();
    }

    public GraphSerializerFactory node(GraphNode node) {
        this.node = node;
        return this;
    }

    public Serializer build() {
        checkState(node != null, "node must not be null");

        String fullyQualifiedName = node.componentData().getFullyQualifiedName();
        Class<? extends Serializer> serializerClazz = COMPONENT_SERIALIZER_MAP
                .getOrDefault(fullyQualifiedName, GENERIC_SERIALIZER);
        return instantiate(serializerClazz);
    }

    private static Serializer instantiate(Class<? extends Serializer> builderClazz) {
        try {
            return builderClazz
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ESBException(e);
        }
    }

}
