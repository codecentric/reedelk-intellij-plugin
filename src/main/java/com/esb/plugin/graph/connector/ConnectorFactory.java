package com.esb.plugin.graph.connector;

import com.esb.api.exception.ESBException;
import com.esb.component.Choice;
import com.esb.component.FlowReference;
import com.esb.component.Fork;
import com.esb.component.Stop;
import com.esb.plugin.component.choice.ChoiceConnectorBuilder;
import com.esb.plugin.component.flowreference.FlowReferenceConnectorBuilder;
import com.esb.plugin.component.forkjoin.ForkJoinConnectorBuilder;
import com.esb.plugin.component.generic.GenericComponentConnectorBuilder;
import com.esb.plugin.component.stop.StopConnectionBuilder;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
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
        tmp.put(Fork.class.getName(), ForkJoinConnectorBuilder.class);
        tmp.put(Choice.class.getName(), ChoiceConnectorBuilder.class);
        tmp.put(FlowReference.class.getName(), FlowReferenceConnectorBuilder.class);
        CONNECTOR_BUILDER = Collections.unmodifiableMap(tmp);
    }

    private Module module;
    private FlowGraph graph;
    private GraphNode componentToAdd;

    private ConnectorFactory() {
    }

    public static ConnectorFactory get() {
        return new ConnectorFactory();
    }

    public ConnectorFactory componentToAdd(GraphNode componentToAdd) {
        this.componentToAdd = componentToAdd;
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
        checkNotNull(componentToAdd, "componentToAdd");

        String fullyQualifiedName = componentToAdd.component().getFullyQualifiedName();
        Class<? extends ConnectorBuilder> builderClazz = CONNECTOR_BUILDER.getOrDefault(fullyQualifiedName, GENERIC_BUILDER);
        return instantiateBuilder(builderClazz).build(module, graph, componentToAdd);
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
