package com.esb.plugin.service.module;

import com.esb.plugin.component.ComponentDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Consumer;

public interface ComponentService {

    static ComponentService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, ComponentService.class);
    }

    void asyncFindAllComponents(Consumer<Collection<ComponentDescriptor>> callback);

    ComponentDescriptor componentDescriptorByName(String componentFullyQualifiedName);

}
