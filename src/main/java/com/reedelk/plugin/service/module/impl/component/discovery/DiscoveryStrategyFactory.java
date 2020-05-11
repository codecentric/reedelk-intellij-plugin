package com.reedelk.plugin.service.module.impl.component.discovery;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.component.type.flowreference.FlowReferenceComponentDiscovery;
import com.reedelk.plugin.component.type.foreach.ForEachComponentDiscovery;
import com.reedelk.plugin.component.type.fork.ForkComponentDiscovery;
import com.reedelk.plugin.component.type.generic.GenericComponentDiscovery;
import com.reedelk.plugin.component.type.placeholder.PlaceholderComponentDiscovery;
import com.reedelk.plugin.component.type.router.RouterComponentDiscovery;
import com.reedelk.plugin.component.type.stop.StopComponentDiscovery;
import com.reedelk.plugin.component.type.trycatch.TryCatchComponentDiscovery;
import com.reedelk.plugin.component.type.unknown.Unknown;
import com.reedelk.plugin.component.type.unknown.UnknownComponentDiscovery;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;
import com.reedelk.runtime.component.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DiscoveryStrategyFactory {

    private static final Class<? extends DiscoveryStrategy> GENERIC_DISCOVERY = GenericComponentDiscovery.class;
    private static final Map<String, Class<? extends DiscoveryStrategy>> DISCOVERY;
    static {
        Map<String, Class<? extends DiscoveryStrategy>> tmp = new HashMap<>();
        tmp.put(Stop.class.getName(), StopComponentDiscovery.class);
        tmp.put(Fork.class.getName(), ForkComponentDiscovery.class);
        tmp.put(Router.class.getName(), RouterComponentDiscovery.class);
        tmp.put(ForEach.class.getName(), ForEachComponentDiscovery.class);
        tmp.put(TryCatch.class.getName(), TryCatchComponentDiscovery.class);
        tmp.put(Placeholder.class.getName(), PlaceholderComponentDiscovery.class);
        tmp.put(Unknown.class.getName(), UnknownComponentDiscovery.class);
        tmp.put(FlowReference.class.getName(), FlowReferenceComponentDiscovery.class);
        DISCOVERY = Collections.unmodifiableMap(tmp);
    }

    public static Optional<ComponentOutputDescriptor> get(ContainerContext context, PlatformComponentServiceImpl componentService) {
        GraphNode componentGraphNode = context.predecessor();
        String componentFullyQualifiedName =
                componentGraphNode.componentData().getFullyQualifiedName();

        Class<? extends DiscoveryStrategy> rendererClazz =
                DISCOVERY.getOrDefault(componentFullyQualifiedName, GENERIC_DISCOVERY);
        DiscoveryStrategy strategy = instantiate(rendererClazz, componentService);

        List<GraphNode> predecessors = context.predecessors();
        if (predecessors.size() == 0) {
            // TODO: First node.
            return Optional.empty();
        } else if (predecessors.size() == 1) {
            GraphNode predecessor = predecessors.get(0);
            return strategy.compute(context, predecessor);
        } else {
            // More than one
            // Find scope node
            // Use FindJoiningScope.of(graph, node);
            // The joining scope of the predecessors determines the type of output.
            return Optional.empty();
        }
    }

    private static DiscoveryStrategy instantiate(Class<? extends DiscoveryStrategy> discoveryClazz, PlatformComponentServiceImpl componentService) {
        try {
            return discoveryClazz
                    .getConstructor(PlatformComponentServiceImpl.class)
                    .newInstance(componentService);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException exception) {
            throw new PluginException("Could not instantiate discovery strategy class=" + discoveryClazz.getName(), exception);
        }
    }
}
