package com.reedelk.plugin.service.application.impl.completion.scanner;

import java.util.List;

public class AutoCompleteContributorDefinition {

    private final boolean message;
    private final boolean context;
    private final List<String> asList;

    public AutoCompleteContributorDefinition(boolean message, boolean context, List<String> asList) {
        this.message = message;
        this.context= context;
        this.asList = asList;
    }

    public boolean isMessage() {
        return message;
    }

    public boolean isContext() {
        return context;
    }

    public List<String> getAsList() {
        return asList;
    }
}
