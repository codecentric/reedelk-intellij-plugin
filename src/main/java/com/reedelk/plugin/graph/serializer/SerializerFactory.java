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
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.runtime.component.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.reedelk.runtime.api.commons.Preconditions.checkState;

public class SerializerFactory {

    private static final Class<? extends Serializer> GENERIC_SERIALIZER = GenericComponentSerializer.class;
    private static final Map<String, Class<? extends Serializer>> COMPONENT_SERIALIZER_MAP;

    static {
        Map<String, Class<? extends Serializer>> tmp = new HashMap<>();
        tmp.put(Stop.class.getName(), StopSerializer.class);
        tmp.put(Fork.class.getName(), ForkSerializer.class);
        tmp.put(Router.class.getName(), RouterSerializer.class);
        tmp.put(Unknown.class.getName(), UnknownSerializer.class);
        tmp.put(ForEach.class.getName(), ForEachSerializer.class);
        tmp.put(TryCatch.class.getName(), TryCatchSerializer.class);
        tmp.put(Placeholder.class.getName(), PlaceholderSerializer.class);
        tmp.put(FlowReference.class.getName(), FlowReferenceSerializer.class);
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
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            throw new PluginException("Could not instantiate builder class=" + builderClazz.getName(), exception);
        }
    }
}
