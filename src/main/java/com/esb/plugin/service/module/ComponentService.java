package com.esb.plugin.service.module;

import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.ModuleDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ComponentService {

    static ComponentService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, ComponentService.class);
    }

    ComponentDescriptor componentDescriptorByName(String componentFullyQualifiedName);

    Collection<ModuleDescriptor> getModulesDescriptors();

}
