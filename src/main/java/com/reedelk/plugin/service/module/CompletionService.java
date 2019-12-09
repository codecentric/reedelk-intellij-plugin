package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.reedelk.plugin.service.module.impl.completion.Suggestion;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CompletionService {

    static CompletionService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, CompletionService.class);
    }

    List<Suggestion> contextVariablesOf(String componentFullyQualifiedName);

    List<Suggestion> completionTokensOf(String componentFullyQualifiedName, String token);

    interface OnCompletionEvent {
        void onCompletionsUpdated();
    }

}
