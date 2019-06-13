package com.esb.plugin.editor.properties.renderer.node;

import com.esb.api.exception.ESBException;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.type.choice.ChoicePropertiesRenderer;
import com.esb.plugin.component.type.flowreference.FlowReferencePropertiesRenderer;
import com.esb.plugin.component.type.fork.ForkPropertiesRenderer;
import com.esb.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.esb.plugin.component.type.placeholder.PlaceholderPropertiesRenderer;
import com.esb.plugin.component.type.stop.StopPropertiesRenderer;
import com.esb.plugin.component.type.unknown.UnknownPropertiesRenderer;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.system.component.*;

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
        tmp.put(Unknown.class.getName(), UnknownPropertiesRenderer.class);
        tmp.put(Placeholder.class.getName(), PlaceholderPropertiesRenderer.class);
        tmp.put(FlowReference.class.getName(), FlowReferencePropertiesRenderer.class);
        RENDERER = Collections.unmodifiableMap(tmp);
    }

    private FlowSnapshot snapshot;
    private ComponentData componentData;

    private NodePropertiesRendererFactory() {
    }

    public static NodePropertiesRendererFactory get() {
        return new NodePropertiesRendererFactory();
    }

    public NodePropertiesRendererFactory snapshot(FlowSnapshot snapshot) {
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
                    .getConstructor(FlowSnapshot.class)
                    .newInstance(snapshot);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new ESBException(e);
        }
    }

}
