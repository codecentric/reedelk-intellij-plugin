package de.codecentric.reedelk.plugin.editor.properties.renderer;

import de.codecentric.reedelk.plugin.component.type.flowreference.FlowReferencePropertiesRenderer;
import de.codecentric.reedelk.plugin.component.type.foreach.ForEachPropertiesRenderer;
import de.codecentric.reedelk.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import de.codecentric.reedelk.plugin.component.type.placeholder.PlaceholderPropertiesRenderer;
import de.codecentric.reedelk.plugin.component.type.router.RouterPropertiesRenderer;
import de.codecentric.reedelk.plugin.component.type.trycatch.TryCatchPropertiesRenderer;
import de.codecentric.reedelk.plugin.component.type.unknown.Unknown;
import de.codecentric.reedelk.plugin.component.type.unknown.UnknownComponentPropertiesRenderer;
import de.codecentric.reedelk.plugin.exception.PluginException;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.component.type.fork.ForkPropertiesRenderer;
import de.codecentric.reedelk.plugin.component.type.stop.StopPropertiesRenderer;
import de.codecentric.reedelk.runtime.component.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ComponentPropertiesRendererFactory {

    private static final Class<? extends ComponentPropertiesRenderer> GENERIC_RENDERER = GenericComponentPropertiesRenderer.class;
    private static final Map<String, Class<? extends ComponentPropertiesRenderer>> RENDERER;
    static {
        Map<String, Class<? extends ComponentPropertiesRenderer>> tmp = new HashMap<>();
        tmp.put(Stop.class.getName(), StopPropertiesRenderer.class);
        tmp.put(Fork.class.getName(), ForkPropertiesRenderer.class);
        tmp.put(Router.class.getName(), RouterPropertiesRenderer.class);
        tmp.put(ForEach.class.getName(), ForEachPropertiesRenderer.class);
        tmp.put(TryCatch.class.getName(), TryCatchPropertiesRenderer.class);
        tmp.put(Placeholder.class.getName(), PlaceholderPropertiesRenderer.class);
        tmp.put(Unknown.class.getName(), UnknownComponentPropertiesRenderer.class);
        tmp.put(FlowReference.class.getName(), FlowReferencePropertiesRenderer.class);
        RENDERER = Collections.unmodifiableMap(tmp);
    }

    private Module module;
    private FlowSnapshot snapshot;
    private ComponentData componentData;

    private ComponentPropertiesRendererFactory() {
    }

    public static ComponentPropertiesRendererFactory get() {
        return new ComponentPropertiesRendererFactory();
    }

    public ComponentPropertiesRendererFactory snapshot(FlowSnapshot snapshot) {
        this.snapshot = snapshot;
        return this;
    }

    public ComponentPropertiesRendererFactory component(ComponentData componentData) {
        this.componentData = componentData;
        return this;
    }

    public ComponentPropertiesRendererFactory module(Module module) {
        this.module = module;
        return this;
    }

    public ComponentPropertiesRenderer build() {
        checkNotNull(module, "module");
        checkNotNull(snapshot, "snapshot");
        checkNotNull(componentData, "componentData");

        String fullyQualifiedName = componentData.getFullyQualifiedName();
        Class<? extends ComponentPropertiesRenderer> rendererClazz = RENDERER.getOrDefault(fullyQualifiedName, GENERIC_RENDERER);
        return instantiateRenderer(rendererClazz);
    }

    private ComponentPropertiesRenderer instantiateRenderer(Class<? extends ComponentPropertiesRenderer> rendererClazz) {
        try {
            return rendererClazz
                    .getConstructor(FlowSnapshot.class, Module.class)
                    .newInstance(snapshot, module);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException exception) {
            throw new PluginException("Could not instantiate renderer class=" + rendererClazz.getName(), exception);
        }
    }
}
