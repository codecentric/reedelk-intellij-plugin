package com.reedelk.plugin.template;

import com.reedelk.runtime.api.commons.ScriptUtils;
import com.reedelk.runtime.api.message.content.MimeType;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;
import java.util.UUID;

@SuppressWarnings("FieldCanBeLocal")
public class OpenAPIRESTListenerWithPayloadSet extends Properties {

    private final String jsonTemplate = ScriptUtils.asScript("'{\\\"example\\\": \\\"%s\\\"}'");
    private final String xmlTemplate =  ScriptUtils.asScript("'<example>%s</example>'");
    private final String textTemplate = "%s";

    public OpenAPIRESTListenerWithPayloadSet(@NotNull String configId,
                                             @NotNull String flowTitle,
                                             @NotNull String flowDescription,
                                             @NotNull String restListenerDescription,
                                             @NotNull String restPath,
                                             @NotNull String restMethod,
                                             @NotNull MimeType responseMimeType,
                                             @NotNull String openApiOperationObject) {
        put("id", UUID.randomUUID().toString());
        put("title", flowTitle);
        put("restPath", restPath);
        put("restConfigId", configId);
        put("restMethod", restMethod);
        put("payloadData", getResponseExampleFrom(responseMimeType, flowTitle));
        put("description", flowDescription);
        put("payloadMimeType", responseMimeType.toString());
        put("restDescription", restListenerDescription);
        put("openApiOperationObject", openApiOperationObject);
    }

    private String getResponseExampleFrom(MimeType mimeType, String flowTitle) {
        if (MimeType.APPLICATION_JSON.equals(mimeType) || MimeType.TEXT_JSON.equals(mimeType)) {
            return String.format(jsonTemplate, flowTitle);
        } else if (MimeType.APPLICATION_XML.equals(mimeType) || MimeType.TEXT_XML.equals(mimeType)) {
            return String.format(xmlTemplate, flowTitle);
        } else {
            return String.format(textTemplate, flowTitle);
        }
    }
}
