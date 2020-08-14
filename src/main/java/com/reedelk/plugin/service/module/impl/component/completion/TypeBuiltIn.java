package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.Collection;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class TypeBuiltIn {

    public static final TypeProxy MESSAGE = TypeProxy.create(Message.class);
    public static final TypeProxy FLOW_CONTEXT = TypeProxy.create(FlowContext.class);

    public static final String DEFAULT_PAYLOAD = Object.class.getName();
    public static final String DEFAULT_ATTRIBUTES = MessageAttributes.class.getName();

    public static final Collection<BuiltInType> BUILT_IN_TYPES = unmodifiableList(asList(
            new TypeBuiltInPrimitive(),
            new TypeBuiltInObject(),
            new TypeBuiltInList(),
            new TypeBuiltInMap()));

    // Default script signature is message and context.
    public static final Trie MESSAGE_AND_CONTEXT = new TrieRoot();

    static {
        Suggestion message = Suggestion.create(PROPERTY)
                .returnTypeDisplayValue(Message.class.getSimpleName())
                .returnType(MESSAGE)
                .insertValue("message")
                .build();
        MESSAGE_AND_CONTEXT.insert(message);

        Suggestion context = Suggestion.create(PROPERTY)
                .returnTypeDisplayValue(FlowContext.class.getSimpleName())
                .returnType(FLOW_CONTEXT)
                .insertValue("context")
                .build();
        MESSAGE_AND_CONTEXT.insert(context);
    }

    public interface BuiltInType {
        void register(Map<String, Trie> typeTrieMap, TypeAndTries typeAndTries);
    }
}
