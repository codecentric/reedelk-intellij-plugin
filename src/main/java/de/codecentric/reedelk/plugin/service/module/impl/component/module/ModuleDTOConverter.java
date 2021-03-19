package de.codecentric.reedelk.plugin.service.module.impl.component.module;

import de.codecentric.reedelk.module.descriptor.model.ModuleDescriptor;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ModuleDTOConverter {

    private ModuleDTOConverter() {
    }

    public static ModuleDTO from(ModuleDescriptor moduleDescriptor) {
        List<ModuleComponentDTO> moduleComponentDTOs = moduleDescriptor.getComponents()
                .stream()
                .map(componentDescriptor -> new ModuleComponentDTO(
                        componentDescriptor.getFullyQualifiedName(),
                        componentDescriptor.getDisplayName(),
                        componentDescriptor.getImage(),
                        componentDescriptor.getIcon(),
                        componentDescriptor.isHidden()))
                .collect(toList());
        return new ModuleDTO(moduleDescriptor.getName(), moduleComponentDTOs);
    }
}
