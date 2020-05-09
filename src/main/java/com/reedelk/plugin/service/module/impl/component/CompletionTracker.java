package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.reedelk.module.descriptor.model.ModuleDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.editor.properties.context.ComponentPropertyPath;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;

import java.util.*;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;

public class CompletionTracker implements ComponentService {

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

    private OnComponentIO onComponentIO;
    private ComponentInputProcessor processor;
    private OnCompletionEvent onCompletionEvent;

    private final Module module;
    private final ComponentTracker componentTracker;

    public CompletionTracker(Project project, Module module, ComponentTracker componentTracker) {
        this.module = module;
        this.componentTracker = componentTracker;
        this.processor = new ComponentInputProcessor(typesMap);

        onCompletionEvent = project.getMessageBus().syncPublisher(Topics.COMPLETION_EVENT_TOPIC);
        onComponentIO = project.getMessageBus().syncPublisher(Topics.ON_COMPONENT_IO);
    }

    @Override
    public Collection<Suggestion> suggestionsOf(String inputFullyQualifiedName, String componentPropertyPath, String[] tokens) {
        ComponentDescriptor inputComponentDescriptor = componentTracker.componentDescriptorFrom(inputFullyQualifiedName);
        ComponentOutputDescriptor previousComponentOutput =
                inputComponentDescriptor == null ? null : inputComponentDescriptor.getOutput();

        // A suggestion for a property is computed as follows:
        // Get signature for component property path from either flow control, maven modules or current module.
        // if does not exists use the default.
        Trie trie = flowControlSignatureTypes.get(componentPropertyPath);
        if (trie == null) trie = mavenModulesSignatureTypes.get(componentPropertyPath);
        if (trie == null) trie = currentModuleSignatureTypes.get(componentPropertyPath);
        if (trie == null) trie = defaultSignatureTypes;

        Collection<Suggestion> globalSuggestions = CompletionFinder.find(globalTypes, typesMap, previousComponentOutput, tokens);
        Collection<Suggestion> propertySuggestions = CompletionFinder.find(trie, typesMap, previousComponentOutput, tokens);
        globalSuggestions.addAll(propertySuggestions);
        return globalSuggestions;
    }

    @Override
    public Collection<Suggestion> variablesOf(String inputFullyQualifiedName, String componentPropertyPath) {
        return suggestionsOf(inputFullyQualifiedName, componentPropertyPath, new String[]{StringUtils.EMPTY});
    }

    @Override
    public void inputOutputOf(ContainerContext context, String outputFullyQualifiedName) {
        PluginExecutors.run(module, "Fetching IO", indicator -> {
            String predecessorFQCN = context.predecessor();
            ComponentDescriptor componentDescriptorBy = componentTracker.componentDescriptorFrom(predecessorFQCN);
            ComponentOutputDescriptor output = componentDescriptorBy.getOutput();


            ComponentIO.IOTypeDescriptor outputAttributes = processor.outputAttributesFrom(output);
            List<ComponentIO.IOTypeDescriptor> outputPayload = processor.outputPayloadFrom(output);
            ComponentIO componentIO = new ComponentIO(outputAttributes, outputPayload);
            onComponentIO.onComponentIO(predecessorFQCN, outputFullyQualifiedName, componentIO);
        });
    }

    void fireCompletionsUpdatedEvent() {
        // TODO: Check where this one is used and if it is needed.
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
        registerBuiltInFunctions();
        fireCompletionsUpdatedEvent();
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
                                .build()).forEach(trie::insert);
                signatureTypesMap.put(componentPropertyPath, trie);
            });
        }
    }


    private void processModuleDescriptor(ModuleDescriptor moduleDescriptor, Trie globalTypes, Map<String, Trie> localTypes, Map<String, Trie> signatureTypes) {
        moduleDescriptor.getTypes().forEach(type -> {
            try {
                TrieUtils.populate(globalTypes, localTypes, type);
            } catch (Exception e) {
                e.printStackTrace(); // TODO: Add log
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

    private void registerBuiltInFunctions() {
        // Init async
        Trie trie = new TrieDefault();
        trie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                .withLookupString("each { it }")
                .withPresentableText("each")
                .withCursorOffset(2)
                .build());
        trie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                .withLookupString("eachWithIndex { it, i ->  }")
                .withPresentableText("eachWithIndex")
                .withCursorOffset(2)
                .build());
        trie.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                .withLookupString("collect { it }")
                .withPresentableText("collect")
                .withCursorOffset(2)
                .build());
        flowControlTypes.put(ArrayList.class.getName(), trie);
    }

    // Default script signature is message and context.
    private static final TrieDefault defaultSignatureTypes = new TrieDefault();
    static {
        Suggestion message = Suggestion.create(PROPERTY)
                .withLookupString("message")
                .withType(Message.class.getName())
                .build();
        Suggestion context = Suggestion.create(PROPERTY)
                .withLookupString("context")
                .withType(FlowContext.class.getName())
                .build();
        defaultSignatureTypes.insert(message);
        defaultSignatureTypes.insert(context);
    }
}
