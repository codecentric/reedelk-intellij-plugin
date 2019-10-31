package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.type.flowreference.FlowReferencePropertiesRenderer;
import com.reedelk.plugin.component.type.fork.ForkPropertiesRenderer;
import com.reedelk.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.reedelk.plugin.component.type.placeholder.PlaceholderPropertiesRenderer;
import com.reedelk.plugin.component.type.router.RouterPropertiesRenderer;
import com.reedelk.plugin.component.type.stop.StopPropertiesRenderer;
import com.reedelk.plugin.component.type.trycatch.TryCatchPropertiesRenderer;
import com.reedelk.plugin.component.type.unknown.UnknownPropertiesRenderer;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.component.*;

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
        tmp.put(Router.class.getName(), RouterPropertiesRenderer.class);
        tmp.put(Unknown.class.getName(), UnknownPropertiesRenderer.class);
        tmp.put(TryCatch.class.getName(), TryCatchPropertiesRenderer.class);
        tmp.put(Placeholder.class.getName(), PlaceholderPropertiesRenderer.class);
        tmp.put(FlowReference.class.getName(), FlowReferencePropertiesRenderer.class);
        RENDERER = Collections.unmodifiableMap(tmp);
    }

    private Module module;
    private FlowSnapshot snapshot;
    private ComponentData componentData;

    private PropertiesRendererFactory() {
    }

    public static PropertiesRendererFactory get() {
        return new PropertiesRendererFactory();
    }

    public PropertiesRendererFactory snapshot(FlowSnapshot snapshot) {
        this.snapshot = snapshot;
        return this;
    }

    public PropertiesRendererFactory component(ComponentData componentData) {
        this.componentData = componentData;
        return this;
    }

    public PropertiesRendererFactory module(Module module) {
        this.module = module;
        return this;
    }

    public PropertiesRenderer build() {
        checkNotNull(module, "module");
        checkNotNull(snapshot, "snapshot");
        checkNotNull(componentData, "componentData");

        String fullyQualifiedName = componentData.getFullyQualifiedName();
        Class<? extends PropertiesRenderer> rendererClazz = RENDERER.getOrDefault(fullyQualifiedName, GENERIC_RENDERER);
        return instantiateRenderer(rendererClazz);
    }

    private PropertiesRenderer instantiateRenderer(Class<? extends PropertiesRenderer> rendererClazz) {
        try {
            return rendererClazz
                    .getConstructor(FlowSnapshot.class, Module.class)
                    .newInstance(snapshot, module);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new ESBException(e);
        }
    }
}
