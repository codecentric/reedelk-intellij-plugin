package com.reedelk.plugin.service.module.impl.component.module;

import java.util.List;

public class ModuleDTO {

    private final String name;
    private final List<ModuleComponentDTO> components;

    public ModuleDTO(String name, List<ModuleComponentDTO> components) {
        this.name = name;
        this.components = components;
    }

    public String getName() {
        return name;
    }

    public List<ModuleComponentDTO> getComponents() {
        return components;
    }
}
