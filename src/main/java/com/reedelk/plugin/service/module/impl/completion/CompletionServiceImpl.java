package com.reedelk.plugin.service.module.impl.completion;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.module.descriptor.model.ModuleDescriptor;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.module.descriptor.model.type.TypeDescriptor;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.service.module.CompletionService;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.service.module.impl.component.ComponentListUpdateNotifier;
import com.reedelk.plugin.topic.ReedelkTopics;
import com.reedelk.runtime.api.commons.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.topic.ReedelkTopics.COMPLETION_EVENT_TOPIC;

public class CompletionServiceImpl implements CompletionService, CompilationStatusListener, ComponentListUpdateNotifier {

    private final Module module;
    private final MessageBus messageBus;
    private final Map<String, Trie> typeTriesMap = new HashMap<>();
    private final SuggestionTree defaultComponentSuggestions = new SuggestionTree(typeTriesMap);
    private final Map<String, SuggestionTree> componentQualifiedNameSuggestionsMap = new HashMap<>();

    private final Trie globalTypes = new Trie();

    // This tree contains the Functions and Types registered by each module.
    private SuggestionTree moduleSuggestions = new SuggestionTree(typeTriesMap);


    // Custom Functions are global so they are always present.
    // Need to define default script suggestions and specific suggestions for
    // Component by module, and component fully qualified name and property.
    public CompletionServiceImpl(Project project, Module module) {
        this.module = module;
        this.messageBus = project.getMessageBus();

        MessageBusConnection connection = messageBus.connect();
        connection.subscribe(CompilerTopics.COMPILATION_STATUS, this);
        connection.subscribe(ReedelkTopics.COMPONENTS_UPDATE_EVENTS, this);


        initialize();
    }

    @Override
    public synchronized List<Suggestion> contextVariablesOf(String componentFullyQualifiedName) {
        return autocompleteSuggestionOf(componentFullyQualifiedName, new String[]{StringUtils.EMPTY});
    }

    @Override
    public synchronized List<Suggestion> autocompleteSuggestionOf(String componentFullyQualifiedName, String[] tokens) {
        SuggestionTree componentSuggestionTree =
                componentQualifiedNameSuggestionsMap.getOrDefault(componentFullyQualifiedName, defaultComponentSuggestions);
        List<Suggestion> componentSuggestions = componentSuggestionTree.autocomplete(tokens);
        List<Suggestion> autocomplete = moduleSuggestions.autocomplete(tokens);
        componentSuggestions.addAll(autocomplete);
        return componentSuggestions;
    }

    @Override
    public void onComponentListUpdate(Collection<ModuleDescriptor> components) {
        PluginExecutors.run(module, message("module.completion.update.suggestions.task.title"),
                indicator -> updateAutocomplete(components));
    }

    void fireCompletionsUpdatedEvent() {
        messageBus.syncPublisher(COMPLETION_EVENT_TOPIC).onCompletionsUpdated();
    }

    ComponentService componentService() {
        return ComponentService.getInstance(module);
    }

    void initialize() {
        PluginExecutors.run(module, message("module.completion.init.suggestions.task.title"), indicator -> {
            Collection<ModuleDescriptor> allModules = componentService().getAllModuleComponents();
            updateAutocomplete(allModules);
        });
    }

    synchronized void updateAutocomplete(Collection<ModuleDescriptor> descriptors) {
        // Add all global types
//TODO: fihish me
        for (ModuleDescriptor descriptor : descriptors) {
            List<TypeDescriptor> types = descriptor.getTypes();
            for (TypeDescriptor type : types) {

            }
        }

        // Fire update event done.
        fireCompletionsUpdatedEvent();

    }

    private void updateAutocomplete(String fullyQualifiedName, List<PropertyDescriptor> propertyDescriptors) {
        propertyDescriptors.forEach(descriptor -> {
            if (descriptor.getType() instanceof ObjectDescriptor) {
                // If the property type is TypeObject, we must recursively add the
                // autocomplete tokens for each nested object type.
                ObjectDescriptor typeObjectDescriptor = descriptor.getType();
                String typeFullyQualifiedName = typeObjectDescriptor.getTypeFullyQualifiedName();
                List<PropertyDescriptor> properties = typeObjectDescriptor.getObjectProperties();
                updateAutocomplete(typeFullyQualifiedName, properties);
            } else {
                updateAutocomplete(fullyQualifiedName, descriptor);
            }
        });
    }

    // TODO: THIS IS WRONG BECAUSE THE SUGGESTION SHOULD BE COMPONENT
    //  QUALIFIED NAME BUT ALSO BY GIVEN PROPERTY! For the same qualified name there MIGHT
    //  BE TWO DIFFERENT PROPERTIES WITH DIFFERENT AUTOCOMPLETE!!!!
    private void updateAutocomplete(String fullyQualifiedName, PropertyDescriptor descriptor) {
        /**
        List<AutocompleteVariableDescriptor> autocomplete = descriptor.getScriptSignature().getAutocompleteVariables();
        if (!autocomplete.isEmpty()) {
            SuggestionTree componentSuggestionTree = new SuggestionTree(moduleSuggestions.getTypeTriesMap());
            List<Suggestion> componentSuggestions = autocomplete
                    .stream()
                    .map(Suggestion::create)
                    .collect(toList());
            componentSuggestions.forEach(componentSuggestionTree::add);
            componentQualifiedNameSuggestionsMap.put(fullyQualifiedName, componentSuggestionTree);
        }*/
    }
}
