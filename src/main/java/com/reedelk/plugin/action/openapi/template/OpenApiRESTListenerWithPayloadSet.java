package com.reedelk.plugin.action.openapi.template;

import com.reedelk.plugin.action.openapi.OpenApiUtils;
import com.reedelk.runtime.api.commons.ScriptUtils;
import com.reedelk.runtime.api.message.content.MimeType;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;
import java.util.UUID;

@SuppressWarnings("FieldCanBeLocal")
public class OpenApiRESTListenerWithPayloadSet extends Properties {

    private final String jsonTemplate = ScriptUtils.asScript("'{\\\"example\\\": \\\"%s\\\"}'");
    private final String xmlTemplate =  ScriptUtils.asScript("'<example>%s</example>'");
    private final String textTemplate = "%s";

    public OpenApiRESTListenerWithPayloadSet(@NotNull String configId,
                                             @NotNull String flowTitle,
                                             @NotNull String flowDescription,
                                             @NotNull String restListenerDescription,
                                             @NotNull String restPath,
                                             @NotNull String restMethod,
                                             @NotNull MimeType responseMimeType,
                                             @NotNull String openApiOperationObject) {
        setProperty("id", UUID.randomUUID().toString());
        setProperty("title", flowTitle);
        setProperty("restPath", restPath);
        setProperty("restConfigId", configId);
        setProperty("restMethod", restMethod);
        setProperty("payloadData", getResponseExampleFrom(responseMimeType, flowTitle));
        setProperty("description", flowDescription);
        setProperty("payloadMimeType", responseMimeType.toString());
        setProperty("restDescription", restListenerDescription);
        setProperty("openApiOperationObject", openApiOperationObject);
    }

    private String getResponseExampleFrom(MimeType mimeType, String flowTitle) {
        if (MimeType.APPLICATION_JSON.equals(mimeType) || MimeType.TEXT_JSON.equals(mimeType)) {
            String data = OpenApiUtils.escapeSingleQuotes(flowTitle);
            return String.format(jsonTemplate, data);
        } else if (MimeType.APPLICATION_XML.equals(mimeType) || MimeType.TEXT_XML.equals(mimeType)) {
            return String.format(xmlTemplate, flowTitle);
        } else {
            return String.format(textTemplate, flowTitle);
        }
    }
}
