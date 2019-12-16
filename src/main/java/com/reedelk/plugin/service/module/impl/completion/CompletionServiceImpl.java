package com.reedelk.plugin.service.module.impl.completion;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.service.module.CompletionService;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.service.module.impl.component.ModuleComponents;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentListUpdateNotifier;
import com.reedelk.plugin.topic.ReedelkTopics;
import com.reedelk.runtime.api.commons.StringUtils;

import java.util.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.message.SuggestionsBundle.DefaultSuggestions;
import static com.reedelk.plugin.topic.ReedelkTopics.COMPLETION_EVENT_TOPIC;
import static java.util.stream.Collectors.toList;

public class CompletionServiceImpl implements CompletionService, CompilationStatusListener, ComponentListUpdateNotifier {

    private final Module module;
    private final Trie defaultComponentTrie = new Trie();
    private final Map<String, Trie> componentTriesMap = new HashMap<>();
    private final MessageBus messageBus;

    private Trie customFunctionsTrie = new Trie();

    // Custom Functions are global so they are always present.
    // Need to define default script suggestions and specific suggestions for
    // Component by module, and component fully qualified name and property.
    public CompletionServiceImpl(Project project, Module module) {
        this.module = module;
        this.messageBus = project.getMessageBus();

        MessageBusConnection connection = messageBus.connect();
        connection.subscribe(CompilerTopics.COMPILATION_STATUS, this);
        connection.subscribe(ReedelkTopics.COMPONENTS_UPDATE_EVENTS, this);

        initializeAsync();
    }

    void initializeAsync() {
        PluginExecutors.run(module,
                message("module.completion.init.suggestions.task.title"),
                indicator -> initializeSuggestions());
    }

    @Override
    public List<Suggestion> contextVariablesOf(String componentFullyQualifiedName) {
        return completionTokensOf(componentFullyQualifiedName, StringUtils.EMPTY).stream()
                .filter(suggestion -> suggestion.getType() == SuggestionType.VARIABLE)
                .collect(toList());
    }

    @Override
    public List<Suggestion> completionTokensOf(String componentFullyQualifiedName, String token) {
        List<Suggestion> componentSuggestions = componentTriesMap.getOrDefault(componentFullyQualifiedName, defaultComponentTrie).findByPrefix(token);
        List<Suggestion> customFunctionsSuggestions = customFunctionsTrie.findByPrefix(token);
        List<Suggestion> results = new ArrayList<>(componentSuggestions);
        results.addAll(customFunctionsSuggestions);
        return results;
    }

    @Override
    public void onComponentListUpdate(Collection<ModuleComponents> components) {
        PluginExecutors.run(module,
                message("module.completion.update.suggestions.task.title"),
                indicator -> updateComponentsSuggestions(components));
    }

    void updateComponentsSuggestions(Collection<ModuleComponents> components) {
        // Since we are updating the suggestion trees for all the components we
        // must empty the current component tree map.
        componentTriesMap.clear();
        components.forEach(componentsPackage -> componentsPackage.getModuleComponents()
                        .forEach(descriptor -> {
                            String fullyQualifiedName = descriptor.getFullyQualifiedName();
                            addSuggestionFrom(fullyQualifiedName, descriptor.getPropertiesDescriptors());
                        }));

        // We throw away the old custom functions trie since again we are updating
        // all the custom functions suggestions for all dependencies of this module.
        customFunctionsTrie = new Trie();
        getComponentService().getAutoCompleteContributorDefinition()
                .forEach(definition -> customFunctionsTrie.insert(definition.getContributions()));

        fireCompletionsUpdatedEvent();
    }

    ComponentService getComponentService() {
        return ComponentService.getInstance(module);
    }

    void initializeSuggestions() {
        insertSuggestions(defaultComponentTrie, DefaultSuggestions.MESSAGE);
        insertSuggestions(defaultComponentTrie, DefaultSuggestions.CONTEXT);
        Collection<ModuleComponents> moduleComponents = ComponentService.getInstance(module).getModuleComponents();
        updateComponentsSuggestions(moduleComponents);
    }

    void fireCompletionsUpdatedEvent() {
        messageBus.syncPublisher(COMPLETION_EVENT_TOPIC).onCompletionsUpdated();
    }

    private void addSuggestionFrom(String fullyQualifiedName, List<ComponentPropertyDescriptor> propertyDescriptors) {
        propertyDescriptors.forEach(descriptor -> {
            if (descriptor.getPropertyType() instanceof TypeObjectDescriptor) {
                // If the property type is TypeObject, we must recursively add the
                // suggestions for all the properties of this object.
                TypeObjectDescriptor typeObjectDescriptor = descriptor.getPropertyType();
                addSuggestionFrom(typeObjectDescriptor.getTypeFullyQualifiedName(), typeObjectDescriptor.getObjectProperties());

            } else {
                descriptor.getAutoCompleteContributorDefinition().ifPresent(definition -> {
                    Trie componentTrie = new Trie();
                    if (definition.isMessage()) insertSuggestions(componentTrie, DefaultSuggestions.MESSAGE);
                    if (definition.isContext()) insertSuggestions(componentTrie, DefaultSuggestions.CONTEXT);
                    if (definition.isError()) insertSuggestions(componentTrie, DefaultSuggestions.ERROR);
                    componentTrie.insert(definition.getContributions());
                    componentTriesMap.put(fullyQualifiedName, componentTrie);
                });
            }
        });
    }

    private void insertSuggestions(Trie trie, DefaultSuggestions defaultSuggestions) {
        String[] allDefaultSuggestionTokens = defaultSuggestions.tokens();
        trie.insert(allDefaultSuggestionTokens);
    }
}