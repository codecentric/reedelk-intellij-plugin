package com.reedelk.plugin.service.module.impl.completion;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.module.descriptor.ModuleDescriptor;
import com.reedelk.module.descriptor.model.AutocompleteVariableDescriptor;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.service.module.AutocompleteService;
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
import static java.util.stream.Collectors.toList;

public class AutocompleteServiceImpl implements AutocompleteService, CompilationStatusListener, ComponentListUpdateNotifier {

    private final Module module;
    private final MessageBus messageBus;

    private final Map<String, Trie<Suggestion>> typeTriesMap = new HashMap<>();

    // This tree contains the Functions and Types registered by each module.
    private final SuggestionTree<Suggestion> moduleSuggestions = new SuggestionTree<>(typeTriesMap);
    private final SuggestionTree<Suggestion> defaultComponentSuggestions = new SuggestionTree<>(typeTriesMap);
    private final Map<String, SuggestionTree<Suggestion>> componentQualifiedNameSuggestionsMap = new HashMap<>();


    // Custom Functions are global so they are always present.
    // Need to define default script suggestions and specific suggestions for
    // Component by module, and component fully qualified name and property.
    public AutocompleteServiceImpl(Project project, Module module) {
        this.module = module;
        this.messageBus = project.getMessageBus();

        MessageBusConnection connection = messageBus.connect();
        connection.subscribe(CompilerTopics.COMPILATION_STATUS, this);
        connection.subscribe(ReedelkTopics.COMPONENTS_UPDATE_EVENTS, this);

        DefaultComponentScriptSuggestions.get().forEach(defaultComponentSuggestions::add);

        initialize(module);
    }

    @Override
    public List<Suggestion> contextVariablesOf(String componentFullyQualifiedName) {
        return autocompleteSuggestionOf(componentFullyQualifiedName, StringUtils.EMPTY);
    }

    @Override
    public List<Suggestion> autocompleteSuggestionOf(String componentFullyQualifiedName, String token) {
        SuggestionTree<Suggestion> defaultSuggestion = componentQualifiedNameSuggestionsMap.getOrDefault(componentFullyQualifiedName, defaultComponentSuggestions);
        List<TrieResult<Suggestion>> specific = defaultSuggestion.autocomplete(token);
        List<TrieResult<Suggestion>> autocomplete = moduleSuggestions.autocomplete(token);
        specific.addAll(autocomplete);
        return specific.stream()
                .map(TrieResult::getTypeAware)
                .collect(toList());
    }

    @Override
    public void onComponentListUpdate(Collection<ModuleDescriptor> components) {
        PluginExecutors.run(module,
                message("module.completion.update.suggestions.task.title"),
                indicator -> updateAutocomplete(components));
    }

    void updateAutocomplete(Collection<ModuleDescriptor> descriptors) {
        // Add all autocomplete items registered by the module.
        descriptors.stream()
                .map(ModuleDescriptor::getAutocompleteItems)
                .map(autocompleteItemDescriptors ->
                        autocompleteItemDescriptors.stream()
                                .map(Suggestion::create)
                                .collect(toList()))
                .forEach(moduleSuggestions::add);

        // Add autocomplete for each component.
        descriptors.forEach(descriptor ->
                descriptor.getComponents().forEach(componentDescriptor -> {
                    String componentFullyQualifiedName = componentDescriptor.getFullyQualifiedName();
                    updateAutocomplete(componentFullyQualifiedName, componentDescriptor.getProperties());
                }));

        // Fire update event done.
        fireCompletionsUpdatedEvent();
    }

    void fireCompletionsUpdatedEvent() {
        messageBus.syncPublisher(COMPLETION_EVENT_TOPIC).onCompletionsUpdated();
    }

    ComponentService componentService() {
        return ComponentService.getInstance(module);
    }

    private void updateAutocomplete(String fullyQualifiedName, List<PropertyDescriptor> propertyDescriptors) {
        propertyDescriptors.forEach(descriptor -> {
            // If the property type is TypeObject, we must recursively add the autocomplete tokens.
            if (descriptor.getType() instanceof TypeObjectDescriptor) {
                TypeObjectDescriptor typeObjectDescriptor = descriptor.getType();
                String typeFullyQualifiedName = typeObjectDescriptor.getTypeFullyQualifiedName();
                List<PropertyDescriptor> properties = typeObjectDescriptor.getObjectProperties();
                updateAutocomplete(typeFullyQualifiedName, properties);

            } else {
                // Use fully qualified name and autocomplete to process and create trees.
                List<AutocompleteVariableDescriptor> autocomplete = descriptor.getAutocompleteVariables();
                if (!autocomplete.isEmpty()) {
                    SuggestionTree<Suggestion> componentSuggestionTree = new SuggestionTree<>(moduleSuggestions.getTypeTriesMap());
                    List<Suggestion> componentSuggestions = autocomplete
                            .stream()
                            .map(Suggestion::create)
                            .collect(toList());
                    componentSuggestions.forEach(componentSuggestionTree::add);
                    componentQualifiedNameSuggestionsMap.put(fullyQualifiedName, componentSuggestionTree);
                }
            }
        });
    }

    private void initialize(Module module) {
        PluginExecutors.run(module,
                message("module.completion.init.suggestions.task.title"),
                indicator -> {
                    Collection<ModuleDescriptor> allModules = componentService().getAllModuleComponents();
                    updateAutocomplete(allModules);
                });
    }
}