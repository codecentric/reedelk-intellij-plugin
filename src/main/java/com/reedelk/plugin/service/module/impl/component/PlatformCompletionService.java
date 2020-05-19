package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.ModuleDescriptor;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.reedelk.plugin.message.ReedelkBundle.message;

class PlatformCompletionService implements PlatformModuleService {

    private static final Logger LOG = Logger.getInstance(PlatformCompletionService.class);

    // Global module types map
    private final Trie flowControlModuleGlobalTypes = new TrieDefault();
    private final Trie mavenModulesGlobalTypes = new TrieDefault();
    private final Trie currentModuleGlobalTypes = new TrieDefault();
    private final TrieMultipleWrapper allGlobalTypes =
            new TrieMultipleWrapper(flowControlModuleGlobalTypes, mavenModulesGlobalTypes, currentModuleGlobalTypes);

    // Local module types map: the keys are the fully qualified name of the type
    private final Map<String, Trie> flowControlTypes = new HashMap<>();
    private final Map<String, Trie> mavenModulesTypes = new HashMap<>();
    private final Map<String, Trie> currentModuleTypes = new HashMap<>();
    private final TypeAndTries allTypes =
            new TypeAndTries(flowControlTypes, mavenModulesTypes, currentModuleTypes);

    // Component script property signatures -> types Maps
    // The value of a map is a trie where the input variables of the script are the roots.
    private final Map<String, Trie> flowControlSignatureTypes = new HashMap<>();
    private final Map<String, Trie> mavenModulesSignatureTypes = new HashMap<>();
    private final Map<String, Trie> currentModuleSignatureTypes = new HashMap<>();

    private final CompletionFinder completionFinder;
    private final PlatformComponentMetadataService componentMetadataService;

    public PlatformCompletionService(Module module, PlatformModuleService moduleService) {
        this.completionFinder = new CompletionFinder(allTypes);
        this.componentMetadataService = new PlatformComponentMetadataService(module, moduleService, completionFinder, allTypes);
    }

    @Override
    public Collection<Suggestion> suggestionsOf(@NotNull ComponentContext context, @NotNull String componentPropertyPath, String[] tokens) {
        // TODO: You don't need the previous component OUTPUT EVERY TIME, only if it is a dynamic type!!
        //  this logic slows down suggestion for nothing here, you should fetch the previous component output
        //  if and only if the suggestion refers to message.payload() or message.attributes().
        PreviousComponentOutput previousComponentOutput = componentMetadataService.componentOutputOf(context);
        // A suggestion for a property is computed as follows:
        // Get signature for component property path from either flow control, maven modules or current module.
        // if does not exists use the default.
        Trie trie = flowControlSignatureTypes.get(componentPropertyPath);
        if (trie == null) trie = mavenModulesSignatureTypes.get(componentPropertyPath);
        if (trie == null) trie = currentModuleSignatureTypes.get(componentPropertyPath);
        if (trie == null) trie = TypeDefault.MESSAGE_AND_CONTEXT;

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
        try {
            SuggestionProcessor processor = new SuggestionProcessor(
                    allTypes,
                    currentModuleGlobalTypes,
                    currentModuleTypes,
                    currentModuleSignatureTypes);
            processor.populate(moduleDescriptor);
        } catch (Exception exception) {
            String error = message("module.completion.suggestion.processor.module.error",
                    moduleDescriptor.getName(),
                    moduleDescriptor.getDisplayName());
            LOG.warn(error, exception);
            // Exception is not rethrown on purpose (we want to still be able to register the other modules).
        }
    }

    public void registerMaven(ModuleDescriptor moduleDescriptor) {
        try {
            SuggestionProcessor processor = new SuggestionProcessor(
                    allTypes,
                    mavenModulesGlobalTypes,
                    mavenModulesTypes,
                    mavenModulesSignatureTypes);
            processor.populate(moduleDescriptor);
        } catch (Exception exception) {
            String error = message("module.completion.suggestion.processor.module.error",
                    moduleDescriptor.getName(),
                    moduleDescriptor.getDisplayName());
            LOG.warn(error, exception);
            // Exception is not rethrown on purpose (we want to still be able to register the other modules).
        }
    }

    public void registerFlowControl(ModuleDescriptor moduleDescriptor) {
        try {
            // Init Language Core types such as List, Map, ArrayList, HashMap and so on.
            TypeDefault.Types.register(allTypes, flowControlTypes);

            SuggestionProcessor processor = new SuggestionProcessor(
                    allTypes,
                    flowControlModuleGlobalTypes,
                    flowControlTypes,
                    flowControlSignatureTypes);
            processor.populate(moduleDescriptor);
        } catch (Exception exception) {
            String error = message("module.completion.suggestion.processor.module.error",
                    moduleDescriptor.getName(),
                    moduleDescriptor.getDisplayName());
            LOG.error(error, exception);
            // This is a fatal error and it must be fixed.
            throw exception;
        }
    }
}
