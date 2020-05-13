package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.metadata.ComponentMetadataDTO;
import com.reedelk.plugin.service.module.impl.component.module.ModuleDTO;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface PlatformModuleService {

    static PlatformModuleService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, PlatformModuleService.class);
    }

    default ComponentDescriptor componentDescriptorOf(@NotNull String componentFullyQualifiedName) {
        throw new UnsupportedOperationException();
    }

    default Collection<ModuleDTO> listModules() {
        throw new UnsupportedOperationException();
    }

    default Collection<Suggestion> suggestionsOf(@NotNull ComponentContext context, @NotNull String componentPropertyPath, String[] tokens) {
        throw new UnsupportedOperationException();
    }

    default Collection<Suggestion> variablesOf(@NotNull ComponentContext context, @NotNull String componentPropertyPath) {
        throw new UnsupportedOperationException();
    }

    default void componentMetadataOf(@NotNull ComponentContext context) {
        throw new UnsupportedOperationException();
    }

    interface OnComponentMetadataEvent {
        void onComponentMetadataUpdated(ComponentMetadataDTO componentMetadataDTO);
        void onComponentMetadataError(Exception exception);
    }

    interface OnCompletionEvent {
        void onCompletionUpdated();
    }

    interface OnModuleEvent {
        void onModuleUpdated(Collection<ModuleDTO> modules);
    }
}
