package com.reedelk.plugin.service.project.impl.completion;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.executor.PluginExecutor;
import com.reedelk.plugin.message.SuggestionsBundle;
import com.reedelk.plugin.service.project.CompletionService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenImportListener;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CompletionServiceImpl implements CompletionService, MavenImportListener, CompilationStatusListener {

    private final Trie trie;

    private boolean initializing;

    public CompletionServiceImpl(@NotNull Project project) {

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

    private void initialize() {
        tokensFrom("message");
        tokensFrom("context");
    }

    private void tokensFrom(String key) {
        String[] tokens = SuggestionsBundle.message(key).split(",");
        Arrays.stream(tokens).forEach(suggestionTokenDefinition -> CompletionService.parseSuggestionToken(suggestionTokenDefinition).ifPresent(parsed ->
                trie.insert(parsed.getMiddle(), parsed.getRight(), parsed.getLeft())));
    }
}