package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.ModuleDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.editor.properties.context.ComponentPropertyPath;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.runtime.api.commons.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;

class PlatformCompletionService implements PlatformModuleService {

    // GLOBAL MODULE TYPES MAPs
    private final Trie flowControlModuleGlobalTypes = new TrieDefault();
    private final Trie mavenModulesGlobalTypes = new TrieDefault();
    private final Trie currentModuleGlobalTypes = new TrieDefault();
    private final TrieWrapper globalTypes =
            new TrieWrapper(flowControlModuleGlobalTypes, mavenModulesGlobalTypes, currentModuleGlobalTypes);

    // LOCAL MODULE TYPES MAPs
    private final Map<String, Trie> flowControlTypes = new HashMap<>(); // Fully qualified name of the module.
    private final Map<String, Trie> mavenModulesTypes = new HashMap<>();
    private final Map<String, Trie> currentModuleTypes = new HashMap<>();
    private final TrieMapWrapper typesMap =
            new TrieMapWrapper(flowControlTypes, mavenModulesTypes, currentModuleTypes);

    // COMPONENT SCRIPT PROPERTY SIGNATURE -> TYPES MAPs (maps to a Trie: signature variables for the property are the roots)
    private final Map<String, Trie> flowControlSignatureTypes = new HashMap<>();
    private final Map<String, Trie> mavenModulesSignatureTypes = new HashMap<>();
    private final Map<String, Trie> currentModuleSignatureTypes = new HashMap<>();

    private final CompletionFinder completionFinder;
    private final OnCompletionEvent onCompletionEvent;
    private final PlatformComponentMetadataService componentMetadataService;

    public PlatformCompletionService(Module module, PlatformComponentService componentTracker) {
        this.completionFinder = new CompletionFinder(typesMap);
        this.componentMetadataService = new PlatformComponentMetadataService(module, completionFinder, typesMap, componentTracker);
        this.onCompletionEvent = module.getProject().getMessageBus().syncPublisher(Topics.COMPLETION_EVENT_TOPIC);
    }

    @Override
    public Collection<Suggestion> suggestionsOf(ComponentContext context, String componentPropertyPath, String[] tokens) {
        ComponentOutputDescriptor previousComponentOutput = componentMetadataService.componentOutputOf(context);
        // A suggestion for a property is computed as follows:
        // Get signature for component property path from either flow control, maven modules or current module.
        // if does not exists use the default.
        Trie trie = flowControlSignatureTypes.get(componentPropertyPath);
        if (trie == null) trie = mavenModulesSignatureTypes.get(componentPropertyPath);
        if (trie == null) trie = currentModuleSignatureTypes.get(componentPropertyPath);
        if (trie == null) trie = Default.TRIE;

        Collection<Suggestion> globalSuggestions = completionFinder.find(globalTypes, tokens, previousComponentOutput);
        Collection<Suggestion> localSuggestions = completionFinder.find(trie, tokens, previousComponentOutput);
        globalSuggestions.addAll(localSuggestions);
        return globalSuggestions;
    }

    @Override
    public Collection<Suggestion> variablesOf(ComponentContext context, String componentPropertyPath) {
        return suggestionsOf(context, componentPropertyPath, new String[]{StringUtils.EMPTY});
    }

    @Override
    public void componentMetadataOf(ComponentContext context) {
        componentMetadataService.componentMetadataOf(context);
    }

    void fireCompletionsUpdatedEvent() {
        onCompletionEvent.onCompletionsUpdated();
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
        processModuleDescriptor(moduleDescriptor, currentModuleGlobalTypes, currentModuleTypes, currentModuleSignatureTypes);
        fireCompletionsUpdatedEvent();
    }

    public void registerMaven(ModuleDescriptor moduleDescriptor) {
        processModuleDescriptor(moduleDescriptor, mavenModulesGlobalTypes, mavenModulesTypes, mavenModulesSignatureTypes);
        fireCompletionsUpdatedEvent();
    }

    public void registerFlowControl(ModuleDescriptor moduleDescriptor) {
        processModuleDescriptor(moduleDescriptor, flowControlModuleGlobalTypes, flowControlTypes, flowControlSignatureTypes);
        // Init core
        Default.Types.register(flowControlTypes);
        fireCompletionsUpdatedEvent();
    }

    private void processModuleDescriptor(ModuleDescriptor moduleDescriptor, Trie globalTypes, Map<String, Trie> localTypes, Map<String, Trie> signatureTypes) {
        moduleDescriptor.getTypes().forEach(type -> {
            try {
                TrieUtils.populate(globalTypes, localTypes, type);
            } catch (Exception exception) {
                exception.printStackTrace(); // TODO: Add log
            }
        });

        // Signatures by component's properties
        moduleDescriptor.getComponents().forEach(componentDescriptor -> {
            componentDescriptor.getProperties().forEach(propertyDescriptor -> {
                String parent = componentDescriptor.getFullyQualifiedName();
                register(propertyDescriptor, parent, signatureTypes);
            });
        });
    }

    private void register(PropertyDescriptor propertyDescriptor, String parent, Map<String, Trie> signatureTypesMap) {
        if (propertyDescriptor.getType() instanceof ObjectDescriptor) {
            // If the property type is TypeObject, we must recursively add the suggestions for each nested property.
            String name = propertyDescriptor.getName();
            String componentPropertyPath = ComponentPropertyPath.join(parent, name);
            ObjectDescriptor typeObjectDescriptor = propertyDescriptor.getType();
            typeObjectDescriptor.getObjectProperties()
                    .forEach(subProperty -> register(subProperty, componentPropertyPath, signatureTypesMap));

        } else {
            String name = propertyDescriptor.getName();
            String componentPropertyPath = ComponentPropertyPath.join(parent, name);
            Optional.ofNullable(propertyDescriptor.getScriptSignature()).ifPresent(scriptSignature -> {
                TrieDefault trie = new TrieDefault();
                scriptSignature.getArguments().stream()
                        .map(argument -> Suggestion.create(PROPERTY)
                                .withLookupString(argument.getArgumentName())
                                .withType(argument.getArgumentType())
                                .build())
                        .forEach(trie::insert);
                signatureTypesMap.put(componentPropertyPath, trie);
            });
        }
    }
}
