package com.reedelk.plugin.service.application;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.components.ServiceManager;

import java.util.List;

public interface CompletionService {
    static CompletionService getInstance() {
        return ServiceManager.getService(CompletionService.class);
    }

    List<LookupElement> completionTokensOf(String token);
}
