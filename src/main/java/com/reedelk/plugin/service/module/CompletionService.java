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

    List<Suggestion> contextVariablesOf(String componentPropertyPath);

    List<Suggestion> autocompleteSuggestionOf(String componentPropertyPath, String[] tokens);

    void loadComponentIO(String inputFQCN, String outputFQCN);

    interface OnCompletionEvent {
        void onCompletionsUpdated();
    }
}
