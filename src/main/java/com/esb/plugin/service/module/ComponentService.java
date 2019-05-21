package com.esb.plugin.service.module;

import com.esb.plugin.component.ComponentDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface ComponentService {

    static ComponentService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, ComponentService.class);
    }

    ComponentDescriptor componentDescriptorByName(String componentFullyQualifiedName);

    Set<ComponentDescriptor> listComponents();

}
