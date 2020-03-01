package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.reedelk.module.descriptor.ModuleDescriptor;
import com.reedelk.module.descriptor.model.ComponentDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ComponentService {

    static ComponentService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, ComponentService.class);
    }

    ComponentDescriptor findComponentDescriptorBy(String componentFullyQualifiedName);

    Collection<ModuleDescriptor> getAllModuleComponents();

}
