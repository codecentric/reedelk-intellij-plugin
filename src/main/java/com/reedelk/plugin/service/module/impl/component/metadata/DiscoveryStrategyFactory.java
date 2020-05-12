package com.reedelk.plugin.service.module.impl.component.metadata;

import com.intellij.openapi.module.Module;
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
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentService;
import com.reedelk.plugin.service.module.impl.component.completion.TrieMapWrapper;
import com.reedelk.runtime.component.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.reedelk.runtime.api.commons.Preconditions.checkState;

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

    public static Optional<? extends ComponentOutputDescriptor> get(@NotNull Module module,
                                                                    @NotNull PlatformComponentService componentService,
                                                                    @NotNull TrieMapWrapper typeAndAndTries,
                                                                    @NotNull ContainerContext context,
                                                                    @NotNull GraphNode nodeToFindInputMessage) {

        List<GraphNode> predecessors = context.predecessors(nodeToFindInputMessage);
        if (predecessors.size() == 0) {
            // First node of the flow/subflow (no input here).
            return Optional.empty();
        }

        Optional<ScopedGraphNode> maybeScopedGraphNode = context.joiningScopeOf(nodeToFindInputMessage);
        if (maybeScopedGraphNode.isPresent()) {
            // The current node to find input message is joining a scope.
            // We must use the strategy with multiple predecessor for the scope node it is joining.
            String fullyQualifiedScopeNodeName = maybeScopedGraphNode.get().componentData().getFullyQualifiedName();
            DiscoveryStrategy strategy = get(fullyQualifiedScopeNodeName, module, componentService, typeAndAndTries);
            return strategy.compute(context, predecessors);

        } else {
            // The current node does not join any scope, therefore
            // its predecessors must be at most 1.
            checkState(predecessors.size() == 1,
                    "Found [" + predecessors.size() +"] predecessors but expected only 1.");

            GraphNode predecessor = predecessors.get(0);
            String predecessorFullyQualifiedName = predecessor.componentData().getFullyQualifiedName();
            DiscoveryStrategy strategy = get(predecessorFullyQualifiedName, module, componentService, typeAndAndTries);
            return strategy.compute(context, predecessor);
        }
    }

    private static DiscoveryStrategy get(String componentFullyQualifiedName, Module module, PlatformComponentService componentService, TrieMapWrapper typeAndAndTries) {
        Class<? extends DiscoveryStrategy> rendererClazz =
                DISCOVERY.getOrDefault(componentFullyQualifiedName, GENERIC_DISCOVERY);
        return instantiate(rendererClazz, module, componentService, typeAndAndTries);
    }

    private static DiscoveryStrategy instantiate(Class<? extends DiscoveryStrategy> discoveryClazz, Module module, PlatformComponentService componentService, TrieMapWrapper typeAndAndTries) {
        try {
            return discoveryClazz
                    .getConstructor(Module.class, PlatformComponentService.class, TrieMapWrapper.class)
                    .newInstance(module, componentService, typeAndAndTries);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException exception) {
            throw new PluginException("Could not instantiate discovery strategy class=" + discoveryClazz.getName(), exception);
        }
    }
}
