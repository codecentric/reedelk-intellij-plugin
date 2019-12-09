package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.reedelk.plugin.component.domain.AutoCompleteContributorDefinition;
import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.plugin.service.module.impl.component.ComponentsPackage;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ComponentService {

    static ComponentService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, ComponentService.class);
    }

    ComponentDescriptor componentDescriptorByName(String componentFullyQualifiedName);

    Collection<ComponentsPackage> getModulesDescriptors();

    Collection<AutoCompleteContributorDefinition> getAutoCompleteContributorDefinition();

}
