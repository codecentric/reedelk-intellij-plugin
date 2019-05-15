package com.esb.plugin.designer.properties.renderer;

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

public class PropertiesRendererFactory {

    private static final Class<? extends PropertiesRenderer> GENERIC_RENDERER = GenericComponentPropertiesRenderer.class;
    private static final Map<String, Class<? extends PropertiesRenderer>> RENDERER;
    static {
        Map<String, Class<? extends PropertiesRenderer>> tmp = new HashMap<>();
        tmp.put(Stop.class.getName(), StopPropertiesRenderer.class);
        tmp.put(Fork.class.getName(), ForkPropertiesRenderer.class);
        tmp.put(Choice.class.getName(), ChoicePropertiesRenderer.class);
        tmp.put(FlowReference.class.getName(), FlowReferencePropertiesRenderer.class);
        RENDERER = Collections.unmodifiableMap(tmp);
    }

    private GraphSnapshot snapshot;
    private ComponentData componentData;

    private PropertiesRendererFactory() {
    }

    public static PropertiesRendererFactory get() {
        return new PropertiesRendererFactory();
    }

    public PropertiesRendererFactory snapshot(GraphSnapshot snapshot) {
        this.snapshot = snapshot;
        return this;
    }

    public PropertiesRendererFactory component(ComponentData componentData) {
        this.componentData = componentData;
        return this;
    }

    public PropertiesRenderer build() {
        checkNotNull(snapshot, "snapshot");
        checkNotNull(componentData, "componentData");

        String fullyQualifiedName = componentData.getFullyQualifiedName();
        Class<? extends PropertiesRenderer> rendererClazz = RENDERER.getOrDefault(fullyQualifiedName, GENERIC_RENDERER);
        return instantiateRenderer(rendererClazz);
    }

    private PropertiesRenderer instantiateRenderer(Class<? extends PropertiesRenderer> rendererClazz) {
        try {
            return rendererClazz
                    .getConstructor(GraphSnapshot.class)
                    .newInstance(snapshot);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new ESBException(e);
        }
    }

}
