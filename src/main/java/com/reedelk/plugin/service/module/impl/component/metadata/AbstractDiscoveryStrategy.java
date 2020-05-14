package com.reedelk.plugin.service.module.impl.component.metadata;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;

import java.util.Optional;

public abstract class AbstractDiscoveryStrategy implements DiscoveryStrategy {

    protected final PlatformModuleService moduleService;
    protected final TypeAndTries typeAndAndTries;
    protected final Module module;

    public AbstractDiscoveryStrategy(Module module, PlatformModuleService moduleService, TypeAndTries typeAndAndTries) {
        this.moduleService = moduleService;
        this.typeAndAndTries = typeAndAndTries;
        this.module = module;
    }

    protected Optional<? extends ComponentOutputDescriptor> discover(ComponentContext context, GraphNode target) {
        return DiscoveryStrategyFactory.get(module, moduleService, typeAndAndTries, context, target);
    }
}
