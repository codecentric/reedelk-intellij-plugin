package com.esb.plugin.designer.properties.renderer.node;

import com.esb.api.exception.ESBException;
import com.esb.component.Choice;
import com.esb.component.FlowReference;
import com.esb.component.Fork;
import com.esb.component.Stop;
import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.choice.ChoicePropertiesRenderer;
import com.esb.plugin.component.flowreference.FlowReferencePropertiesRenderer;
import com.esb.plugin.component.fork.ForkPropertiesRenderer;
import com.esb.plugin.component.generic.GenericComponentPropertiesRenderer;
import com.esb.plugin.component.stop.StopPropertiesRenderer;
import com.esb.plugin.graph.GraphSnapshot;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class NodePropertiesRendererFactory {

    private static final Class<? extends NodePropertiesRenderer> GENERIC_RENDERER = GenericComponentPropertiesRenderer.class;
    private static final Map<String, Class<? extends NodePropertiesRenderer>> RENDERER;
    static {
        Map<String, Class<? extends NodePropertiesRenderer>> tmp = new HashMap<>();
        tmp.put(Stop.class.getName(), StopPropertiesRenderer.class);
        tmp.put(Fork.class.getName(), ForkPropertiesRenderer.class);
        tmp.put(Choice.class.getName(), ChoicePropertiesRenderer.class);
        tmp.put(FlowReference.class.getName(), FlowReferencePropertiesRenderer.class);
        RENDERER = Collections.unmodifiableMap(tmp);
    }

    private GraphSnapshot snapshot;
    private ComponentData componentData;

    private NodePropertiesRendererFactory() {
    }

    public static NodePropertiesRendererFactory get() {
        return new NodePropertiesRendererFactory();
    }

    public NodePropertiesRendererFactory snapshot(GraphSnapshot snapshot) {
        this.snapshot = snapshot;
        return this;
    }

    public NodePropertiesRendererFactory component(ComponentData componentData) {
        this.componentData = componentData;
        return this;
    }

    public NodePropertiesRenderer build() {
        checkNotNull(snapshot, "snapshot");
        checkNotNull(componentData, "componentData");

        String fullyQualifiedName = componentData.getFullyQualifiedName();
        Class<? extends NodePropertiesRenderer> rendererClazz = RENDERER.getOrDefault(fullyQualifiedName, GENERIC_RENDERER);
        return instantiateRenderer(rendererClazz);
    }

    private NodePropertiesRenderer instantiateRenderer(Class<? extends NodePropertiesRenderer> rendererClazz) {
        try {
            return rendererClazz
                    .getConstructor(GraphSnapshot.class)
                    .newInstance(snapshot);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new ESBException(e);
        }
    }

}