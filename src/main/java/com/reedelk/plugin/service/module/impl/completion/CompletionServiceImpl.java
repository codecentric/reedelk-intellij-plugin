package com.reedelk.plugin.service.module.impl.completion;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentInputDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.editor.properties.context.ComponentPropertyPath;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.service.module.CompletionService;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.service.module.impl.component.ComponentListUpdateNotifier;
import com.reedelk.plugin.service.module.impl.component.ModuleDTO;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;

import java.util.*;

import static com.reedelk.plugin.commons.Topics.COMPLETION_EVENT_TOPIC;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.service.module.impl.completion.Suggestion.Type.PROPERTY;

// TODO: work on this.
public class CompletionServiceImpl implements CompletionService, ComponentListUpdateNotifier {

    private final Module module;
    private final MessageBus messageBus;

    // Key is : componentQualifiedName#property1#subproperty1#subsubproperty1
    private final Map<String, Trie> componentPropertyAndTrie = new HashMap<>();
    private final Map<String, TypeInfo> typeAndAndTries = new HashMap<>();

    private final Trie global = new Trie();
    private final Trie defaultVariables = new Trie();


    private ComponentInputProcessor processor;
    private OnComponentIO onComponentIO;
    // TODO: Take care of current module being developed suggestions.

    // Custom Functions are global so they are always present.
    // Need to define default script suggestions and specific suggestions for
    // Component by module, and component fully qualified name and property.
    public CompletionServiceImpl(Project project, Module module) {
        this.module = module;
        this.messageBus = project.getMessageBus();
        this.processor = new ComponentInputProcessor(typeAndAndTries);

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
        connection.subscribe(Topics.COMPONENTS_UPDATE_EVENTS, this);
        onComponentIO = messageBus.syncPublisher(Topics.ON_COMPONENT_IO);

        // Init strategy:
        // 1. Should not initialize here, but when the first one is asked and init is false.
        // 1. Init first system components, they cannot be changed and they are final.
        // 2. Init all global types
        // 2. Cache all the trees for the types belonging to a component and not for all the components.
        initialize();
    }

    @Override
    public synchronized List<Suggestion> contextVariablesOf(String componentPropertyPath) {
        return autocompleteSuggestionOf(componentPropertyPath, new String[]{StringUtils.EMPTY});
    }

    @Override
    public synchronized List<Suggestion> autocompleteSuggestionOf(String componentPropertyPath, String[] tokens) {
        // If there is only one token, you must not search in global because you will never find a match
        // for a token starting with ["message", ""].
        List<Suggestion> suggestions;
        if (tokens.length <= 1) {
            suggestions = CompletionFinder.find(global, typeAndAndTries, tokens);
        } else {
            suggestions = new ArrayList<>();
        }

        Trie root = componentPropertyAndTrie.getOrDefault(componentPropertyPath, defaultVariables);
        List<Suggestion> propertySuggestions = CompletionFinder.find(root, typeAndAndTries, tokens);

        suggestions.addAll(propertySuggestions);
        return suggestions;
    }

    @Override
    public void loadComponentIO(String inputFQCN, String outputFQCN) {
        PluginExecutors.run(module, "Fetching IO", indicator -> {

            ComponentDescriptor componentDescriptorBy = componentService().getComponentDescriptor(inputFQCN);

            ComponentInputDescriptor input = componentDescriptorBy.getInput();
            ComponentOutputDescriptor output = componentDescriptorBy.getOutput();

            Optional<ComponentIO> maybeComponentIO = processor.inputFrom(output);
            if (maybeComponentIO.isPresent()) {
                onComponentIO.onComponentIO(inputFQCN, outputFQCN, maybeComponentIO.get());
            } else {
                onComponentIO.onComponentIONotFound(inputFQCN, outputFQCN);
            }
        });
    }

    @Override
    public void onComponentListUpdate(Collection<ModuleDTO> moduleDescriptors) {
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
            Collection<ModuleDTO> allModules = componentService().getModules();
            updateAutocomplete(allModules);
        });
    }

    synchronized void updateAutocomplete(Collection<ModuleDTO> moduleDescriptors) {

        moduleDescriptors.forEach(moduleDescriptor -> {
            try {
                /**
                // We only register global types, since they are the most used ones.
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
                moduleDescriptor.getComponents().forEach(componentDescriptor -> {
                    // dunno these property types are not really needed, are they?
                    // Properties
                    componentDescriptor.getProperties().forEach(propertyDescriptor -> {
                        String parent = componentDescriptor.getFullyQualifiedName();
                        register(propertyDescriptor, parent);
                    });
                });*/
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
