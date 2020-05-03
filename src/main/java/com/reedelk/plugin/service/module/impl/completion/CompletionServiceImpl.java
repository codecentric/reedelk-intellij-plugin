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
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.editor.properties.context.ComponentPropertyPath;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.service.module.CompletionService;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.service.module.impl.component.ComponentListUpdateNotifier;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;

import java.util.*;

import static com.reedelk.plugin.commons.Topics.COMPLETION_EVENT_TOPIC;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.service.module.impl.completion.Suggestion.Type.PROPERTY;

// TODO: work on this.
public class CompletionServiceImpl implements CompletionService, CompilationStatusListener, ComponentListUpdateNotifier {

    private final Module module;
    private final MessageBus messageBus;

    // Key is : componentQualifiedName#property1#subproperty1#subsubproperty1
    private final Map<String, Trie> componentPropertyAndTrie = new HashMap<>();
    private final Map<String, Trie> typeAndAndTries = new HashMap<>();
    private final Trie global = new Trie();
    private final Trie defaultVariables = new Trie();


    // TODO: Take care of current module being developed suggestions.

    // Custom Functions are global so they are always present.
    // Need to define default script suggestions and specific suggestions for
    // Component by module, and component fully qualified name and property.
    public CompletionServiceImpl(Project project, Module module) {
        this.module = module;
        this.messageBus = project.getMessageBus();

        Suggestion message = Suggestion.create(PROPERTY)
                .withLookupString("message")
                .withType(Message.class.getName())
                .build();
        Suggestion context = Suggestion.create(PROPERTY)
                .withLookupString("context")
                .withType(FlowContext.class.getName())
                .build();
        defaultVariables.insert(message);
        defaultVariables.insert(context);

        MessageBusConnection connection = messageBus.connect();
        connection.subscribe(CompilerTopics.COMPILATION_STATUS, this);
        connection.subscribe(Topics.COMPONENTS_UPDATE_EVENTS, this);
        initialize();
    }

    @Override
    public synchronized List<Suggestion> contextVariablesOf(String componentFullyQualifiedName) {
        return autocompleteSuggestionOf(componentFullyQualifiedName, new String[]{StringUtils.EMPTY});
    }

    @Override
    public synchronized List<Suggestion> autocompleteSuggestionOf(String componentPropertyPath, String[] tokens) {
        List<Suggestion> suggestions = CompletionFinder.find(global, typeAndAndTries, tokens);

        Trie root = componentPropertyAndTrie.getOrDefault(componentPropertyPath, defaultVariables);
        List<Suggestion> propertySuggestions = CompletionFinder.find(root, typeAndAndTries, tokens);

        suggestions.addAll(propertySuggestions);
        return suggestions;
    }

    @Override
    public void onComponentListUpdate(Collection<ModuleDescriptor> moduleDescriptors) {
        PluginExecutors.run(module,
                message("module.completion.update.suggestions.task.title"),
                indicator -> updateAutocomplete(moduleDescriptors));
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

    synchronized void updateAutocomplete(Collection<ModuleDescriptor> moduleDescriptors) {
        moduleDescriptors.forEach(moduleDescriptor -> {

            try {
                // Add all global types
                List<TypeDescriptor> types = moduleDescriptor.getTypes();
                for (TypeDescriptor type : types) {
                    try {
                        TrieUtils.populate(global, typeAndAndTries, type);
                    } catch (Exception e) {
                        // TODO: Add log
                        e.printStackTrace();
                    }
                }

                // Add component -> property types
                moduleDescriptor.getComponents().forEach(componentDescriptor ->
                        componentDescriptor.getProperties().forEach(propertyDescriptor -> {
                            String parent = componentDescriptor.getFullyQualifiedName();
                            register(propertyDescriptor, parent);
                        }));
            } catch (Exception e) {
                //ToDO:
                e.printStackTrace();
            }
        });

        // Fire update event done.
        fireCompletionsUpdatedEvent();
    }

    private void register(PropertyDescriptor propertyDescriptor, String parent) {
        if (propertyDescriptor.getType() instanceof ObjectDescriptor) {
            // If the property type is TypeObject, we must recursively add the suggestions for each nested property.
            String name = propertyDescriptor.getName();
            String componentPropertyPath = ComponentPropertyPath.join(parent, name);
            ObjectDescriptor typeObjectDescriptor = propertyDescriptor.getType();
            typeObjectDescriptor.getObjectProperties()
                    .forEach(subProperty -> register(subProperty, componentPropertyPath));

        } else {
            String name = propertyDescriptor.getName();
            String componentPropertyPath = ComponentPropertyPath.join(parent, name);
            Optional.ofNullable(propertyDescriptor.getScriptSignature()).ifPresent(scriptSignature -> {
                Trie trie = new Trie();
                scriptSignature.getArguments().stream()
                        .map(argument -> Suggestion.create(PROPERTY)
                                .withLookupString(argument.getArgumentName())
                                .withType(argument.getArgumentType())
                                .build()).forEach(trie::insert);
                componentPropertyAndTrie.put(componentPropertyPath, trie);
            });
        }
    }
}
