package com.reedelk.plugin.service.module.impl.completion;

import com.reedelk.module.descriptor.model.AutocompleteVariableDescriptor;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;

import java.util.ArrayList;
import java.util.List;

public class DefaultComponentScriptSuggestions {

    private static final List<Suggestion> DEFAULT;
    static {
        DEFAULT = new ArrayList<>();

        AutocompleteVariableDescriptor message = new AutocompleteVariableDescriptor();
        message.setName("message");
        message.setType(Message.class.getSimpleName());
        Suggestion messageSuggestion = Suggestion.create(message);
        DEFAULT.add(messageSuggestion);

        AutocompleteVariableDescriptor context = new AutocompleteVariableDescriptor();
        context.setName("context");
        context.setType(FlowContext.class.getSimpleName());
        Suggestion contextSuggestion = Suggestion.create(context);
        DEFAULT.add(contextSuggestion);
    }

    public static List<Suggestion> get() {
        return DEFAULT;
    }
}
