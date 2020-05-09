package com.reedelk.plugin.service.module.impl.component.completion.commons;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

public class DynamicType {

    private DynamicType() {
    }

    // Resolves the dynamic type from the output descriptor
    @SuppressWarnings("UnnecessaryLocalVariable")
    public static List<String> from(Suggestion suggestion, ComponentOutputDescriptor descriptor) {
        String suggestionType = suggestion.typeText();
        if (MessageAttributes.class.getName().equals(suggestionType)) {
            String attributeDynamicType = descriptor != null ?
                    descriptor.getAttributes() : MessageAttributes.class.getName();
            return Collections.singletonList(attributeDynamicType);

        } else if (MessagePayload.class.getName().equals(suggestionType)) {
            List<String> payloadDynamicTypes = descriptor != null ?
                    descriptor.getPayload() : singletonList(Object.class.getName());
            return payloadDynamicTypes;

        } else {
            return Collections.singletonList(Object.class.getName());
        }
    }
}
