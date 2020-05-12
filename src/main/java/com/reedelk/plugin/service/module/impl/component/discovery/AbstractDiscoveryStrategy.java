package com.reedelk.plugin.service.module.impl.component.discovery;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;

public abstract class AbstractDiscoveryStrategy implements DiscoveryStrategy {

    protected final PlatformComponentServiceImpl componentService;
    protected final Module module;

    public AbstractDiscoveryStrategy(Module module, PlatformComponentServiceImpl componentService) {
        this.componentService = componentService;
        this.module = module;
    }
}
