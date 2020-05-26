package com.reedelk.plugin.component.type.trycatch;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.message.ReedelkBundle;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategyFactory;
import com.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategyOneOfAware;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutputDefault;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;

public class TryCatchComponentDiscovery extends DiscoveryStrategyOneOfAware {

    public TryCatchComponentDiscovery(@NotNull Module module,
                                      @NotNull PlatformModuleService moduleService,
                                      @NotNull TypeAndTries typeAndAndTries) {
        super(module, moduleService, typeAndAndTries);
    }

    @Override
    public Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode nodeWeWantOutputFrom, GraphNode successor) {
        List<GraphNode> successors = context.successors(nodeWeWantOutputFrom);
        // context.node() is wrong, because the context node might be two positions
        // ahead (preceded by payload set)
        if (successors.get(0).equals(successor)) {
            // Try branch (we take the one before the try-catch).
            return discover(context, nodeWeWantOutputFrom);
        } else {
            // We are in the catch
            PreviousComponentOutput descriptor = new PreviousComponentOutputDefault(
                    singletonList(MessageAttributes.class.getName()),
                    singletonList(Exception.class.getName()),
                    ReedelkBundle.message("metadata.trycatch.payload.description"));
            return Optional.of(descriptor);
        }
    }

    Optional<PreviousComponentOutput> discover(ComponentContext context, GraphNode target) {
        return DiscoveryStrategyFactory.get(module, moduleService, typeAndAndTries, context, target);
    }
}
