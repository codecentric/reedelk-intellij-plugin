package com.reedelk.plugin.template;

import org.jetbrains.annotations.NotNull;

import java.util.Properties;
import java.util.UUID;

public class OpenAPIRESTListenerWithPayloadSet extends Properties {

    public OpenAPIRESTListenerWithPayloadSet(@NotNull String configId,
                                             @NotNull String flowTitle,
                                             @NotNull String flowDescription,
                                             @NotNull String restListenerDescription,
                                             @NotNull String restPath,
                                             @NotNull String restMethod,
                                             @NotNull String openApiOperationObject) {
        put("id", UUID.randomUUID().toString());
        put("title", flowTitle);
        put("description", flowDescription);
        put("restDescription", restListenerDescription);
        put("restConfigId", configId);
        put("restPath", restPath);
        put("restMethod", restMethod);
        put("openApiOperationObject", openApiOperationObject);
    }
}
