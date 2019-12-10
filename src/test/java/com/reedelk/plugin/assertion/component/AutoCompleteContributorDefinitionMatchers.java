package com.reedelk.plugin.assertion.component;

import com.reedelk.plugin.component.domain.AutoCompleteContributorDefinition;

import java.util.List;

public class AutoCompleteContributorDefinitionMatchers {

    public interface AutoCompleteContributorDefinitionMatcher {
        boolean matches(AutoCompleteContributorDefinition actual);
    }

    public static AutoCompleteContributorDefinitionMatcher with(boolean isMessage, boolean isContext, boolean isError, List<String> customContributions) {
        return actual -> {
            boolean actualIsMessage = actual.isMessage();
            boolean actualIsContext = actual.isContext();
            boolean actualIsError = actual.isError();
            List<String> actualContributions = actual.getContributions();
            return isMessage == actualIsMessage &&
                    isContext == actualIsContext &&
                    isError == actualIsError &&
                    customContributions.equals(actualContributions);
        };
    }
}
