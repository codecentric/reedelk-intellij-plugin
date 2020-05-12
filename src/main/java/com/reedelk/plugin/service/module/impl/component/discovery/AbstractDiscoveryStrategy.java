package com.reedelk.plugin.service.module.impl.component.discovery;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;
import com.reedelk.plugin.service.module.impl.component.completion.TrieMapWrapper;

public abstract class AbstractDiscoveryStrategy implements DiscoveryStrategy {

    protected final PlatformComponentServiceImpl componentService;
    protected final TrieMapWrapper typeAndAndTries;
    protected final Module module;

    public AbstractDiscoveryStrategy(Module module, PlatformComponentServiceImpl componentService, TrieMapWrapper typeAndAndTries) {
        this.componentService = componentService;
        this.typeAndAndTries = typeAndAndTries;
        this.module = module;
    }
}
