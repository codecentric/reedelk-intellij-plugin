package com.esb.plugin.service.application.runtime;

import com.intellij.openapi.components.ServiceManager;

import java.util.Collection;

public interface ESBRuntimeService {

    static ESBRuntimeService getInstance() {
        return ServiceManager.getService(ESBRuntimeService.class);
    }

    Collection<ESBRuntime> listRuntimes();

    void addRuntime(ESBRuntime runtime);

    boolean contains(ESBRuntime runtime);
}
