package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.module.ModuleDTO;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface PlatformModuleService {

    static PlatformModuleService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, PlatformModuleService.class);
    }

    default ComponentDescriptor componentDescriptorOf(String componentFullyQualifiedName) {
        throw new UnsupportedOperationException();
    }

    default Collection<ModuleDTO> listModules() {
        throw new UnsupportedOperationException();
    }

    default Collection<Suggestion> suggestionsOf(ComponentContext context, String[] tokens) {
        throw new UnsupportedOperationException();
    }

    default Collection<Suggestion> variablesOf(ComponentContext context) {
        throw new UnsupportedOperationException();
    }

    default void componentMetadataOf(ComponentContext context) {
        throw new UnsupportedOperationException();
    }

    interface OnCompletionEvent {
        void onCompletionsUpdated();
    }

    interface ModuleChangeNotifier {
        void onModuleChange(Collection<ModuleDTO> modules);
    }
}
