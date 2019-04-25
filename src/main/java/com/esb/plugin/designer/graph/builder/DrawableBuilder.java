package com.esb.plugin.designer.graph.builder;

import com.esb.api.exception.ESBException;
import com.esb.component.Choice;
import com.esb.component.FlowReference;
import com.esb.component.Fork;
import com.esb.internal.commons.JsonParser;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DrawableBuilder {

    private FlowGraph graph;
    private Drawable parent;
    private BuilderContext context;
    private JSONObject componentDefinition;

    private static final Class<? extends Builder> GENERIC_HANDLER = GenericDrawableBuilder.class;

    private static final Map<String, Class<? extends Builder>> COMPONENT_NAME_HANDLER;

    static {
        Map<String, Class<? extends Builder>> tmp = new HashMap<>();
        tmp.put(Fork.class.getName(), ForkJoinDrawableBuilder.class);
        tmp.put(Choice.class.getName(), ChoiceDrawableBuilder.class);
        tmp.put(FlowReference.class.getName(), FlowReferenceDrawableBuilder.class);
        COMPONENT_NAME_HANDLER = Collections.unmodifiableMap(tmp);
    }

    private DrawableBuilder() {
    }

    public DrawableBuilder context(BuilderContext context) {
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

    public DrawableBuilder parent(Drawable parent) {
        this.parent = parent;
        return this;
    }

    public DrawableBuilder componentDefinition(JSONObject componentDefinition) {
        this.componentDefinition = componentDefinition;
        return this;
    }

    public static DrawableBuilder get() {
        return new DrawableBuilder();
    }

    public Drawable build() {
        String componentName = JsonParser.Implementor.name(componentDefinition);
        Class<? extends Builder> builderClazz = COMPONENT_NAME_HANDLER.getOrDefault(componentName, GENERIC_HANDLER);

        return instantiateBuilder(graph, context, builderClazz).build(parent, componentDefinition);
    }

    public DrawableBuilder graph(FlowGraph graph) {
        this.graph = graph;
        return this;
    }

}
