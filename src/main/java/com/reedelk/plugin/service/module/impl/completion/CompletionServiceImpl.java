package com.reedelk.plugin.service.module.impl.completion;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.ModuleInfo;
import com.reedelk.plugin.commons.SuggestionDefinitionMatcher;
import com.reedelk.plugin.executor.PluginExecutor;
import com.reedelk.plugin.maven.MavenUtils;
import com.reedelk.plugin.message.SuggestionsBundle;
import com.reedelk.plugin.service.module.CompletionService;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.service.module.impl.completion.scanner.AutoCompleteContributorScanner;
import com.reedelk.plugin.service.module.impl.component.ComponentsPackage;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentListUpdateNotifier;
import com.reedelk.plugin.topic.ReedelkTopics;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class CompletionServiceImpl implements CompletionService, CompilationStatusListener, ComponentListUpdateNotifier {

    private final Trie defaultComponentTrie;
    private final Trie customFunctionsTrie;
    private final Module module;

    private final Map<String, Trie> componentTriesMap = new HashMap<>();

    private boolean initInProgress = false;
    private boolean updateInProgress = false;

    private AutoCompleteContributorScanner scanner = new AutoCompleteContributorScanner();


    // Custom Functions are global so they are always present.
    // Need to define default script suggestions and specific suggestions for
    // Component by module, and component fully qualified name and property.
    public CompletionServiceImpl(Project project, Module module) {
        this.module = module;

        MessageBus messageBus = project.getMessageBus();

        MessageBusConnection connection = messageBus.connect();
        connection.subscribe(CompilerTopics.COMPILATION_STATUS, this);
        connection.subscribe(ReedelkTopics.COMPONENTS_UPDATE_EVENTS, this);

        this.defaultComponentTrie = new Trie();
        this.customFunctionsTrie = new Trie();
        PluginExecutor.getInstance().submit(this::initialize);
    }

    @Override
    public List<Suggestion> completionTokensOf(String componentFullyQualifiedName, String token) {
        synchronized (this) {
            if (initInProgress || updateInProgress) return Collections.emptyList();
        }

        Optional<List<Suggestion>> componentSuggestions =
                componentTriesMap.getOrDefault(componentFullyQualifiedName, defaultComponentTrie).findByPrefix(token);
        Optional<List<Suggestion>> customFunctionsSuggestions =
                customFunctionsTrie.findByPrefix(token);
        List<Suggestion> results = new ArrayList<>();
        componentSuggestions.ifPresent(results::addAll);
        customFunctionsSuggestions.ifPresent(results::addAll);
        return results;
    }

    @Override
    public void onComponentListUpdate(Module module) {
        updateComponents(module);
    }

    private void updateComponents(Module module) {
        // Add suggestions from
        PluginExecutor.getInstance().submit(() -> {
            synchronized (this) {
                updateInProgress = true;
            }
            internalUpdateComponents(module);

            synchronized (this) {
                updateInProgress = false;
            }
        });
    }

    private void internalUpdateComponents(Module module) {
        Collection<ComponentsPackage> componentsPackages = ComponentService.getInstance(module).getModulesDescriptors();
        componentsPackages.forEach(componentsPackage -> componentsPackage.getModuleComponents()
                .forEach(descriptor -> descriptor.getPropertiesDescriptors().forEach(propertyDescriptor -> {
                    propertyDescriptor.getAutoCompleteContributorDefinition().ifPresent(definition -> {
                        String fullyQualifiedName = descriptor.getFullyQualifiedName();
                        boolean context = definition.isContext();
                        boolean message = definition.isMessage();
                        List<String> contributions = definition.getContributions();

                        final Trie trie = new Trie();
                        if (message) {
                            registerDefaultSuggestionContribution(trie, "message");
                        }
                        if (context) {
                            registerDefaultSuggestionContribution(trie, "context");
                        }
                        contributions.forEach(customSuggestion ->
                                SuggestionDefinitionMatcher.of(customSuggestion).ifPresent(parsed ->
                                        trie.insert(parsed.getMiddle(), parsed.getRight(), parsed.getLeft())));

                        componentTriesMap.put(fullyQualifiedName, trie);
                    });
                })));
    }

    private void registerDefaultSuggestionContribution(Trie trie, String suggestionContributor) {
        String[] tokens = SuggestionsBundle.message(suggestionContributor).split(",");
        Arrays.stream(tokens).forEach(suggestionTokenDefinition ->
                SuggestionDefinitionMatcher.of(suggestionTokenDefinition).ifPresent(parsed ->
                        trie.insert(parsed.getMiddle(), parsed.getRight(), parsed.getLeft())));
    }

    private void initialize() {
        synchronized (this) {
            initInProgress = true;
        }

        PluginExecutor.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                registerDefaultSuggestionContribution(defaultComponentTrie, "message");
                registerDefaultSuggestionContribution(defaultComponentTrie, "context");

                MavenUtils.getMavenProject(module.getProject(), module.getName()).ifPresent(mavenProject ->
                        mavenProject.getDependencies().stream()
                                .filter(artifact -> ModuleInfo.isModule(artifact.getFile()))
                                .map(artifact -> artifact.getFile().getPath()).collect(toList())
                                .forEach(jarFilePath -> {
                                    scanner.from(jarFilePath).forEach(contribution ->
                                            SuggestionDefinitionMatcher.of(contribution).ifPresent(parsed ->
                                                    customFunctionsTrie.insert(parsed.getMiddle(), parsed.getRight(), parsed.getLeft())));
                                }));

                internalUpdateComponents(module);

                synchronized (this) {
                    initInProgress = false;
                }
            }
        });

    }
}