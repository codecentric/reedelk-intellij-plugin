package de.codecentric.reedelk.plugin.graph.node;

import de.codecentric.reedelk.plugin.component.type.flowreference.FlowReferenceNode;
import de.codecentric.reedelk.plugin.component.type.foreach.ForEachNode;
import de.codecentric.reedelk.plugin.component.type.generic.GenericComponentNode;
import de.codecentric.reedelk.plugin.component.type.placeholder.PlaceholderNode;
import de.codecentric.reedelk.plugin.component.type.trycatch.TryCatchNode;
import de.codecentric.reedelk.plugin.component.type.unknown.Unknown;
import de.codecentric.reedelk.plugin.component.type.unknown.UnknownNode;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDescriptor;
import de.codecentric.reedelk.plugin.commons.ImplementsInterface;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.component.ComponentDescriptionDecorator;
import de.codecentric.reedelk.plugin.component.type.fork.ForkNode;
import de.codecentric.reedelk.plugin.component.type.router.RouterNode;
import de.codecentric.reedelk.plugin.component.type.stop.StopNode;
import de.codecentric.reedelk.plugin.service.module.PlatformModuleService;
import de.codecentric.reedelk.runtime.component.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class GraphNodeFactory {

    private static final Class<? extends GraphNode> DEFAULT = GenericComponentNode.class;
    private static final Map<String, Class<? extends GraphNode>> COMPONENT_DRAWABLE_MAP;

    static {
        Map<String, Class<? extends GraphNode>> tmp = new HashMap<>();
        tmp.put(Stop.class.getName(), StopNode.class);
        tmp.put(Fork.class.getName(), ForkNode.class);
        tmp.put(Router.class.getName(), RouterNode.class);
        tmp.put(Unknown.class.getName(), UnknownNode.class);
        tmp.put(ForEach.class.getName(), ForEachNode.class);
        tmp.put(TryCatch.class.getName(), TryCatchNode.class);
        tmp.put(Placeholder.class.getName(), PlaceholderNode.class);
        tmp.put(FlowReference.class.getName(), FlowReferenceNode.class);
        COMPONENT_DRAWABLE_MAP = tmp;
    }

    private GraphNodeFactory() {
    }

    public static <T extends GraphNode> T get(Module module, String componentName) {
        ComponentDescriptor componentDescriptor =
                PlatformModuleService.getInstance(module).componentDescriptorOf(componentName);
        return GraphNodeFactory.get(componentDescriptor);
    }

    @SuppressWarnings("unchecked")
    public static <T extends GraphNode> T get(ComponentDescriptor descriptor) {

        String componentFullyQualifiedName = descriptor.getFullyQualifiedName();

        Class<? extends GraphNode> componentNodeClazz =
                COMPONENT_DRAWABLE_MAP.getOrDefault(componentFullyQualifiedName, DEFAULT);

        ComponentData componentData = createComponentData(componentNodeClazz, descriptor);

        try {
            return (T) componentNodeClazz.getConstructor(ComponentData.class).newInstance(componentData);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private static ComponentData createComponentData(Class<? extends GraphNode> componentNodeClazz, ComponentDescriptor descriptor) {
        if (ImplementsInterface.by(componentNodeClazz, ScopedGraphNode.class)) {
            // Scoped Nodes do NOT have a description associated with them
            return new ComponentData(descriptor);
        } else {
            // Normal Nodes have a description associated with them
            ComponentDescriptionDecorator componentDescriptionDecorator = new ComponentDescriptionDecorator(descriptor);
            return new ComponentData(componentDescriptionDecorator);
        }
    }
}
