package com.esb.plugin.designer.graph.connector;

import com.esb.api.exception.ESBException;
import com.esb.component.Choice;
import com.esb.component.FlowReference;
import com.esb.component.Fork;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.GraphNode;
import com.esb.plugin.designer.graph.drawable.ComponentAware;
import com.intellij.openapi.module.Module;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConnectorFactory {

    private Module module;
    private FlowGraph graph;
    private GraphNode componentToAdd;

    private static final Class<? extends ConnectorBuilder> GENERIC_BUILDER = GenericComponentConnectorBuilder.class;

    private static final Map<String, Class<? extends ConnectorBuilder>> CONNECTOR_BUILDER;

    static {
        Map<String, Class<? extends ConnectorBuilder>> tmp = new HashMap<>();
        tmp.put(Fork.class.getName(), ForkJoinDrawableConnectorBuilder.class);
        tmp.put(Choice.class.getName(), ChoiceDrawableConnectorBuilder.class);
        tmp.put(FlowReference.class.getName(), FlowReferenceConnectorBuilder.class);
        CONNECTOR_BUILDER = Collections.unmodifiableMap(tmp);
    }

    private ConnectorFactory() {
    }

    public ConnectorFactory componentToAdd(GraphNode componentToAdd) {
        this.componentToAdd = componentToAdd;
        return this;
    }

    public static ConnectorFactory get() {
        return new ConnectorFactory();
    }

    public Connector build() {
        String fullyQualifiedName = ((ComponentAware) componentToAdd).component().getFullyQualifiedName();
        Class<? extends ConnectorBuilder> builderClazz = CONNECTOR_BUILDER.getOrDefault(fullyQualifiedName, GENERIC_BUILDER);
        return instantiateBuilder(builderClazz).build(module, graph, componentToAdd);
    }

    public ConnectorFactory graph(FlowGraph graph) {
        this.graph = graph;
        return this;
    }

    public ConnectorFactory module(Module module) {
        this.module = module;
        return this;
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
