package com.reedelk.plugin.service.module.impl.completion;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.ModuleInfo;
import com.reedelk.plugin.executor.PluginExecutor;
import com.reedelk.plugin.maven.MavenUtils;
import com.reedelk.plugin.message.SuggestionsBundle;
import com.reedelk.plugin.service.module.CompletionService;
import com.reedelk.plugin.service.module.impl.completion.scanner.AutoCompleteContributorScanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenImportListener;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class CompletionServiceImpl implements CompletionService, MavenImportListener, CompilationStatusListener {

    private final Trie trie;
    private final Project project;
    private final Module module;

    private boolean initializing;

    public CompletionServiceImpl(Project project, Module module) {
        this.project = project;
        this.module = module;

        MessageBus messageBus = project.getMessageBus();

        MessageBusConnection connection = messageBus.connect();
        connection.subscribe(MavenImportListener.TOPIC, this);
        connection.subscribe(CompilerTopics.COMPILATION_STATUS, this);

        this.trie = new Trie();
        PluginExecutor.getInstance().submit(() -> {
            initializing = true;
            initialize();
            initializing = false;
        });
    }

    @Override
    public Optional<List<Suggestion>> completionTokensOf(String token) {
        return initializing ? Optional.empty() : trie.findByPrefix(token);
    }

    @Override
    public void importFinished(@NotNull Collection<MavenProject> importedProjects, @NotNull List<Module> newModules) {

    }

    private AutoCompleteContributorScanner scanner = new AutoCompleteContributorScanner();

    private void initialize() {
        registerSuggestionContribution("message");
        registerSuggestionContribution("context");

        MavenUtils.getMavenProject(module.getProject(), module.getName()).ifPresent(mavenProject -> {
            mavenProject.getDependencies().stream()
                    .filter(artifact -> ModuleInfo.isModule(artifact.getFile()))
                    .map(artifact -> artifact.getFile().getPath()).collect(toList())
                    .forEach(jarFilePath ->
                            scanner.from(jarFilePath).forEach(contribution ->
                                    CompletionService.parseSuggestionToken(contribution).ifPresent(parsed ->
                                            trie.insert(parsed.getMiddle(), parsed.getRight(), parsed.getLeft()))));
        });
    }

    private void registerSuggestionContribution(String suggestionContributor) {
        String[] tokens = SuggestionsBundle.message(suggestionContributor).split(",");
        Arrays.stream(tokens).forEach(suggestionTokenDefinition ->
                CompletionService.parseSuggestionToken(suggestionTokenDefinition).ifPresent(parsed ->
                        trie.insert(parsed.getMiddle(), parsed.getRight(), parsed.getLeft())));
    }
}