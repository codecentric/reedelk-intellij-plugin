package com.esb.plugin.graph.connector;

import com.esb.api.exception.ESBException;
import com.esb.plugin.component.type.choice.ChoiceConnectorBuilder;
import com.esb.plugin.component.type.flowreference.FlowReferenceConnectorBuilder;
import com.esb.plugin.component.type.fork.ForkConnectorBuilder;
import com.esb.plugin.component.type.generic.GenericComponentConnectorBuilder;
import com.esb.plugin.component.type.stop.StopConnectionBuilder;
import com.esb.plugin.component.type.unknown.UnknownConnectionBuilder;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.system.component.*;
import com.intellij.openapi.module.Module;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConnectorFactory {

    private static final Class<? extends ConnectorBuilder> GENERIC_BUILDER = GenericComponentConnectorBuilder.class;
    private static final Map<String, Class<? extends ConnectorBuilder>> CONNECTOR_BUILDER;
    static {
        Map<String, Class<? extends ConnectorBuilder>> tmp = new HashMap<>();
        tmp.put(Stop.class.getName(), StopConnectionBuilder.class);
        tmp.put(Fork.class.getName(), ForkConnectorBuilder.class);
        tmp.put(Choice.class.getName(), ChoiceConnectorBuilder.class);
        tmp.put(Unknown.class.getName(), UnknownConnectionBuilder.class);
        tmp.put(FlowReference.class.getName(), FlowReferenceConnectorBuilder.class);
        CONNECTOR_BUILDER = Collections.unmodifiableMap(tmp);
    }

    private Module module;
    private FlowGraph graph;
    private GraphNode nodeToAdd;

    private ConnectorFactory() {
    }

    public static ConnectorFactory get() {
        return new ConnectorFactory();
    }

    public ConnectorFactory nodeToAdd(GraphNode nodeToAdd) {
        this.nodeToAdd = nodeToAdd;
        return this;
    }

    public ConnectorFactory graph(FlowGraph graph) {
        this.graph = graph;
        return this;
    }

    public ConnectorFactory module(Module module) {
        this.module = module;
        return this;
    }

    public Connector build() {
        checkNotNull(graph, "graph");
        checkNotNull(module, "module");
        checkNotNull(nodeToAdd, "nodeToAdd");

        String fullyQualifiedName = nodeToAdd.componentData().getFullyQualifiedName();
        Class<? extends ConnectorBuilder> builderClazz = CONNECTOR_BUILDER.getOrDefault(fullyQualifiedName, GENERIC_BUILDER);
        return instantiateBuilder(builderClazz).build(module, graph, nodeToAdd);
    }

    private static ConnectorBuilder instantiateBuilder(Class<? extends ConnectorBuilder> builderClazz) {
        try {
            return builderClazz
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ESBException(e);
        }
    }

}
