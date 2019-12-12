package com.reedelk.plugin.component.domain;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.Arrays.asList;

public class ScriptSignatureDefinition {

    public static final ScriptSignatureDefinition DEFAULT =
            new ScriptSignatureDefinition(asList("context", "message"));

    private final List<String> arguments;

    public ScriptSignatureDefinition(@NotNull List<String> arguments) {
        this.arguments = arguments;
    }

    public List<String> getArguments() {
        return arguments;
    }
}
