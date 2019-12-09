package com.reedelk.plugin.component.domain;

import java.util.List;

public class AutoCompleteContributorDefinition {

    private final boolean error;
    private final boolean message;
    private final boolean context;
    private final List<String> contributions;

    public AutoCompleteContributorDefinition(boolean message, boolean error, boolean context, List<String> contributions) {
        this.message = message;
        this.context = context;
        this.error = error;
        this.contributions = contributions;
    }

    // Used by Custom Functions definition
    public AutoCompleteContributorDefinition(List<String> contributions) {
        this(false, false, false, contributions);
    }

    public boolean isMessage() {
        return message;
    }

    public boolean isContext() {
        return context;
    }

    public boolean isError() {
        return error;
    }

    public List<String> getContributions() {
        return contributions;
    }
}
