package com.reedelk.plugin.component.domain;

import java.util.List;

public class AutoCompleteContributorDefinition {

    private final boolean message;
    private final boolean context;
    private final List<String> contributions;

    public AutoCompleteContributorDefinition(boolean message, boolean context, List<String> contributions) {
        this.message = message;
        this.context = context;
        this.contributions = contributions;
    }

    public boolean isMessage() {
        return message;
    }

    public boolean isContext() {
        return context;
    }

    public List<String> getContributions() {
        return contributions;
    }
}
