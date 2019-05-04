package com.esb.plugin.designer.properties;

import com.esb.api.exception.ESBException;
import com.esb.component.Choice;
import com.esb.component.FlowReference;
import com.esb.component.Fork;
import com.esb.component.Stop;
import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.choice.ChoicePropertiesRenderer;
import com.esb.plugin.component.flowreference.FlowReferencePropertyRenderer;
import com.esb.plugin.component.forkjoin.ForkJoinPropertyRenderer;
import com.esb.plugin.component.generic.GenericComponentPropertyRenderer;
import com.esb.plugin.component.stop.StopPropertyRenderer;
import com.esb.plugin.graph.FlowGraph;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class PropertyRendererFactory {

    private static final Class<? extends PropertyRenderer> GENERIC_RENDERER = GenericComponentPropertyRenderer.class;
    private static final Map<String, Class<? extends PropertyRenderer>> RENDERER;
    static {
        Map<String, Class<? extends PropertyRenderer>> tmp = new HashMap<>();
        tmp.put(Stop.class.getName(), StopPropertyRenderer.class);
        tmp.put(Fork.class.getName(), ForkJoinPropertyRenderer.class);
        tmp.put(Choice.class.getName(), ChoicePropertiesRenderer.class);
        tmp.put(FlowReference.class.getName(), FlowReferencePropertyRenderer.class);
        RENDERER = Collections.unmodifiableMap(tmp);
    }

    private Module module;
    private FlowGraph graph;
    private VirtualFile file;
    private ComponentData componentData;

    private PropertyRendererFactory() {
    }

    public static PropertyRendererFactory get() {
        return new PropertyRendererFactory();
    }

    public PropertyRendererFactory graph(FlowGraph graph) {
        this.graph = graph;
        return this;
    }

    public PropertyRendererFactory module(Module module) {
        this.module = module;
        return this;
    }

    public PropertyRendererFactory file(VirtualFile file) {
        this.file = file;
        return this;
    }

    public PropertyRendererFactory component(ComponentData componentData) {
        this.componentData = componentData;
        return this;
    }

    public PropertyRenderer build() {
        checkNotNull(file, "file");
        checkNotNull(graph, "graph");
        checkNotNull(module, "module");
        checkNotNull(componentData, "componentData");

        String fullyQualifiedName = componentData.getFullyQualifiedName();
        Class<? extends PropertyRenderer> rendererClazz = RENDERER.getOrDefault(fullyQualifiedName, GENERIC_RENDERER);
        return instantiateRenderer(rendererClazz);
    }

    private PropertyRenderer instantiateRenderer(Class<? extends PropertyRenderer> rendererClazz) {
        try {
            return rendererClazz
                    .getConstructor(Module.class, FlowGraph.class, VirtualFile.class)
                    .newInstance(module, graph, file);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new ESBException(e);
        }
    }

}
