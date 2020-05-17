package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;

public class TypeDynamicUtils {

    private TypeDynamicUtils() {
    }

    public static boolean is(Suggestion suggestion) {
        return MessagePayload.class.getName().equals(suggestion.getReturnType()) ||
                MessageAttributes.class.getName().equals(suggestion.getReturnType());
    }
}
