package com.esb.plugin.graph.deserializer;

import com.esb.api.exception.ESBException;
import com.esb.component.Choice;
import com.esb.component.FlowReference;
import com.esb.component.Fork;
import com.esb.component.Stop;
import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.choice.ChoiceNodeBuilder;
import com.esb.plugin.component.flowreference.FlowReferenceNodeBuilder;
import com.esb.plugin.component.forkjoin.ForkJoinNodeBuilder;
import com.esb.plugin.component.generic.GenericComponentNodeBuilder;
import com.esb.plugin.component.stop.StopNodeBuilder;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GraphNodeBuilder {

    private FlowGraph graph;
    private GraphNode parent;
    private BuilderContext context;
    private JSONObject componentDefinition;

    private static final Class<? extends Builder> GENERIC_HANDLER = GenericComponentNodeBuilder.class;

    private static final Map<String, Class<? extends Builder>> COMPONENT_NAME_HANDLER;

    static {
        Map<String, Class<? extends Builder>> tmp = new HashMap<>();
        tmp.put(Stop.class.getName(), StopNodeBuilder.class);
        tmp.put(Fork.class.getName(), ForkJoinNodeBuilder.class);
        tmp.put(Choice.class.getName(), ChoiceNodeBuilder.class);
        tmp.put(FlowReference.class.getName(), FlowReferenceNodeBuilder.class);
        COMPONENT_NAME_HANDLER = Collections.unmodifiableMap(tmp);
    }

    private GraphNodeBuilder() {
    }

    public GraphNodeBuilder context(BuilderContext context) {
        this.context = context;
        return this;
    }

    private static Builder instantiateBuilder(FlowGraph graph, BuilderContext context, Class<? extends Builder> builderClazz) {
        try {
            return builderClazz
                    .getDeclaredConstructor(FlowGraph.class, BuilderContext.class)
                    .newInstance(graph, context);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ESBException(e);
        }
    }

    public GraphNodeBuilder parent(GraphNode parent) {
        this.parent = parent;
        return this;
    }

    public GraphNodeBuilder componentDefinition(JSONObject componentDefinition) {
        this.componentDefinition = componentDefinition;
        return this;
    }

    public static GraphNodeBuilder get() {
        return new GraphNodeBuilder();
    }

    public GraphNode build() {
        String componentName = JsonParser.Implementor.name(componentDefinition);
        Class<? extends Builder> builderClazz = COMPONENT_NAME_HANDLER.getOrDefault(componentName, GENERIC_HANDLER);
        return instantiateBuilder(graph, context, builderClazz).build(parent, componentDefinition);
    }

    public GraphNodeBuilder graph(FlowGraph graph) {
        this.graph = graph;
        return this;
    }

}
