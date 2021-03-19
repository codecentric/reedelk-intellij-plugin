package de.codecentric.reedelk.plugin.service.module.impl.component.metadata;

import de.codecentric.reedelk.plugin.component.type.flowreference.FlowReferenceComponentDiscovery;
import de.codecentric.reedelk.plugin.component.type.foreach.ForEachComponentDiscovery;
import de.codecentric.reedelk.plugin.component.type.generic.GenericComponentDiscovery;
import de.codecentric.reedelk.plugin.component.type.placeholder.PlaceholderComponentDiscovery;
import de.codecentric.reedelk.plugin.component.type.trycatch.TryCatchComponentDiscovery;
import de.codecentric.reedelk.plugin.component.type.unknown.Unknown;
import de.codecentric.reedelk.plugin.component.type.unknown.UnknownComponentDiscovery;
import de.codecentric.reedelk.plugin.exception.PluginException;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.service.module.PlatformModuleService;
import de.codecentric.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentType;
import de.codecentric.reedelk.plugin.component.type.fork.ForkComponentDiscovery;
import de.codecentric.reedelk.plugin.component.type.router.RouterComponentDiscovery;
import de.codecentric.reedelk.plugin.component.type.stop.StopComponentDiscovery;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import de.codecentric.reedelk.runtime.component.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static de.codecentric.reedelk.runtime.api.commons.Preconditions.checkState;

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

    private DiscoveryStrategyFactory() {
    }

    public static Optional<PreviousComponentOutput> get(@NotNull Module module,
                                                                    @NotNull PlatformModuleService moduleService,
                                                                    @NotNull TypeAndTries typeAndAndTries,
                                                                    @NotNull ComponentContext context,
                                                                    @NotNull GraphNode nodeToFindInputMessage) {

        List<GraphNode> predecessors = context.predecessors(nodeToFindInputMessage);
        if (predecessors.isEmpty()) {
            // There are no predecessors, therefore it must be the first
            // node of a flow or subflow.
            ComponentType componentClass = nodeToFindInputMessage.getComponentType();

            if (ComponentType.INBOUND.equals(componentClass)) {
                // If it is an inbound, then the predecessor is the last component(s) of the flow.
                List<GraphNode> lastNodesOfFlow = context.endNodes();
                if (lastNodesOfFlow.size() == 1) {
                    GraphNode predecessor = lastNodesOfFlow.get(0);
                    String predecessorFullyQualifiedName = lastNodesOfFlow.get(0).componentData().getFullyQualifiedName();
                    DiscoveryStrategy strategy = get(module, moduleService, typeAndAndTries, predecessorFullyQualifiedName);
                    return strategy.compute(context, predecessor, nodeToFindInputMessage);
                } else {
                    // We need to find the outermost scope
                    Optional<ScopedGraphNode> outermostScope = context.outermostScopeOf(lastNodesOfFlow);
                    if (outermostScope.isPresent()) {
                        String fullyQualifiedScopeNodeName = outermostScope.get().componentData().getFullyQualifiedName();
                        DiscoveryStrategy strategy = get(module, moduleService, typeAndAndTries, fullyQualifiedScopeNodeName);
                        return strategy.compute(context, outermostScope.get());
                    } else {
                        return Optional.empty();
                    }
                }
            } else {
                // If it is not an inbound we are in a subflow and we can't know which
                // is the previous component because a subflow might be used in many
                // different flows.
                return Optional.empty();
            }
        }

        Optional<ScopedGraphNode> maybeScopedGraphNode = context.joiningScopeOf(nodeToFindInputMessage);
        if (maybeScopedGraphNode.isPresent()) {
            // The current node to find input message is joining a scope. We must use the strategy
            // with multiple predecessor for the scope node it is joining.
            String fullyQualifiedScopeNodeName = maybeScopedGraphNode.get().componentData().getFullyQualifiedName();
            DiscoveryStrategy strategy = get(module, moduleService, typeAndAndTries, fullyQualifiedScopeNodeName);
            return strategy.compute(context, maybeScopedGraphNode.get());

        } else {
            // The current node does not join any scope, therefore its predecessors must be at most 1.
            checkState(predecessors.size() == 1,
                    "Found [" + predecessors.size() +"] predecessors but expected only 1.");

            GraphNode predecessor = predecessors.get(0);
            String predecessorFullyQualifiedName = predecessor.componentData().getFullyQualifiedName();
            DiscoveryStrategy strategy = get(module, moduleService, typeAndAndTries, predecessorFullyQualifiedName);
            return strategy.compute(context, predecessor, nodeToFindInputMessage);
        }
    }

    public static DiscoveryStrategy get(Module module, PlatformModuleService moduleService, TypeAndTries typeAndAndTries, String componentFullyQualifiedName) {
        Class<? extends DiscoveryStrategy> rendererClazz =
                DISCOVERY.getOrDefault(componentFullyQualifiedName, GENERIC_DISCOVERY);
        return instantiate(rendererClazz, module, moduleService, typeAndAndTries);
    }

    private static DiscoveryStrategy instantiate(Class<? extends DiscoveryStrategy> discoveryClazz, Module module, PlatformModuleService moduleService, TypeAndTries typeAndAndTries) {
        try {
            return discoveryClazz
                    .getConstructor(Module.class, PlatformModuleService.class, TypeAndTries.class)
                    .newInstance(module, moduleService, typeAndAndTries);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException exception) {
            throw new PluginException("Could not instantiate discovery strategy class=" + discoveryClazz.getName(), exception);
        }
    }
}
