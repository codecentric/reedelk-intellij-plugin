package com.reedelk.plugin.service.module.impl.component.discovery;

import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;

public abstract class AbstractDiscoveryStrategy implements DiscoveryStrategy {

    protected final PlatformComponentServiceImpl componentService;

    public AbstractDiscoveryStrategy(PlatformComponentServiceImpl componentService) {
        this.componentService = componentService;
    }
}
