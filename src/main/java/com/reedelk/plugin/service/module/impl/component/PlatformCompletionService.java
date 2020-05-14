package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.ModuleDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class PlatformCompletionService implements PlatformModuleService {

    // GLOBAL MODULE TYPES MAPs
    private final Trie flowControlModuleGlobalTypes = new TrieImpl();
    private final Trie mavenModulesGlobalTypes = new TrieImpl();
    private final Trie currentModuleGlobalTypes = new TrieImpl();
    private final TrieMultipleWrapper allGlobalTypes =
            new TrieMultipleWrapper(flowControlModuleGlobalTypes, mavenModulesGlobalTypes, currentModuleGlobalTypes);

    // LOCAL MODULE TYPES MAPs
    private final Map<String, Trie> flowControlTypes = new HashMap<>(); // Fully qualified name of the module.
    private final Map<String, Trie> mavenModulesTypes = new HashMap<>();
    private final Map<String, Trie> currentModuleTypes = new HashMap<>();
    private final TypeAndTries allTypes =
            new TypeAndTries(flowControlTypes, mavenModulesTypes, currentModuleTypes);

    // COMPONENT SCRIPT PROPERTY SIGNATURE -> TYPES MAPs (maps to a Trie: signature variables for the property are the roots)
    private final Map<String, Trie> flowControlSignatureTypes = new HashMap<>();
    private final Map<String, Trie> mavenModulesSignatureTypes = new HashMap<>();
    private final Map<String, Trie> currentModuleSignatureTypes = new HashMap<>();

    private final CompletionFinder completionFinder;
    private final PlatformComponentMetadataService componentMetadataService;

    public PlatformCompletionService(Module module, PlatformComponentService componentTracker) {

        this.completionFinder = new CompletionFinder(allTypes);
        this.componentMetadataService = new PlatformComponentMetadataService(module, completionFinder, allTypes, componentTracker);
    }

    @Override
    public Collection<Suggestion> suggestionsOf(@NotNull ComponentContext context, @NotNull String componentPropertyPath, String[] tokens) {
        ComponentOutputDescriptor previousComponentOutput = componentMetadataService.componentOutputOf(context);
        // A suggestion for a property is computed as follows:
        // Get signature for component property path from either flow control, maven modules or current module.
        // if does not exists use the default.
        Trie trie = flowControlSignatureTypes.get(componentPropertyPath);
        if (trie == null) trie = mavenModulesSignatureTypes.get(componentPropertyPath);
        if (trie == null) trie = currentModuleSignatureTypes.get(componentPropertyPath);
        if (trie == null) trie = Default.TRIE;

        Collection<Suggestion> globalSuggestions = completionFinder.find(allGlobalTypes, tokens, previousComponentOutput);
        Collection<Suggestion> localSuggestions = completionFinder.find(trie, tokens, previousComponentOutput);
        globalSuggestions.addAll(localSuggestions);
        return globalSuggestions;
    }

    @Override
    public Collection<Suggestion> variablesOf(@NotNull ComponentContext context, @NotNull String componentPropertyPath) {
        return suggestionsOf(context, componentPropertyPath, new String[]{StringUtils.EMPTY});
    }

    @Override
    public void componentMetadataOf(@NotNull ComponentContext context) {
        componentMetadataService.componentMetadataOf(context);
    }

    public void clearMaven() {
        mavenModulesSignatureTypes.clear();
        mavenModulesGlobalTypes.clear();
        mavenModulesTypes.clear();
    }

    public void clearCurrent() {
        currentModuleSignatureTypes.clear();
        currentModuleGlobalTypes.clear();
        currentModuleTypes.clear();
    }

    public void registerCurrent(ModuleDescriptor moduleDescriptor) {
        SuggestionProcessor processor = new SuggestionProcessor(
                allTypes,
                currentModuleGlobalTypes,
                currentModuleTypes,
                currentModuleSignatureTypes);
        processor.populate(moduleDescriptor);
    }

    public void registerMaven(ModuleDescriptor moduleDescriptor) {
        SuggestionProcessor processor = new SuggestionProcessor(
                allTypes,
                mavenModulesGlobalTypes,
                mavenModulesTypes,
                mavenModulesSignatureTypes);
        processor.populate(moduleDescriptor);
    }

    public void registerFlowControl(ModuleDescriptor moduleDescriptor) {
        // Init Language Core types such as List, Map, ArrayList, HashMap and so on.
        Default.Types.register(flowControlTypes);

        SuggestionProcessor processor = new SuggestionProcessor(
                allTypes,
                flowControlModuleGlobalTypes,
                flowControlTypes,
                flowControlSignatureTypes);
        processor.populate(moduleDescriptor);
    }
}
