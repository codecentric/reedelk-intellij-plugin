package com.esb.plugin.service;

import com.intellij.openapi.components.ServiceManager;

import java.util.Collection;

public interface ESBRuntimeService {
    static ESBRuntimeService getInstance() {
        return ServiceManager.getService(ESBRuntimeService.class);
    }

    Collection<ESBRuntime> listRuntimes();
}
