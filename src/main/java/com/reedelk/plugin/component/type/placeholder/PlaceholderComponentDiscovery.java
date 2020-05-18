package com.reedelk.plugin.component.type.placeholder;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.type.generic.GenericComponentDiscovery;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;

import java.util.Optional;

public class PlaceholderComponentDiscovery extends GenericComponentDiscovery {

    public PlaceholderComponentDiscovery(Module module, PlatformModuleService moduleService, TypeAndTries typeAndAndTries) {
        super(module, moduleService, typeAndAndTries);
    }

    @Override
    public Optional<PreviousComponentOutput> computeForScope(ComponentContext context, GraphNode currentNode) {
        return Optional.empty();
    }
}
