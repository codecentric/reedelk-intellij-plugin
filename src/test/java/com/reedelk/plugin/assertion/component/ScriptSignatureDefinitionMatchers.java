package com.reedelk.plugin.assertion.component;

import com.reedelk.plugin.component.domain.ScriptSignatureDefinition;

import java.util.List;

public class ScriptSignatureDefinitionMatchers {

    public interface ScriptSignatureDefinitionMatcher {
        boolean matches(ScriptSignatureDefinition actual);
    }

    public static ScriptSignatureDefinitionMatcher with(List<String> expectedArguments) {
        return actual -> expectedArguments.equals(actual.getArguments());
    }
}
