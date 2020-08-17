package com.reedelk.plugin.template;

import org.jetbrains.annotations.NotNull;

public class OpenAPIRESTListenerWithResource extends OpenAPIRESTListenerWithPayloadSet {

    public OpenAPIRESTListenerWithResource(@NotNull String configId,
                                           @NotNull String flowTitle,
                                           @NotNull String flowDescription,
                                           @NotNull String restListenerDescription,
                                           @NotNull String restPath,
                                           @NotNull String restMethod,
                                           @NotNull String openApiOperationObject,
                                           @NotNull String exampleResourceFile,
                                           @NotNull String exampleMimeType) {
        super(configId, flowTitle, flowDescription, restListenerDescription, restPath, restMethod, openApiOperationObject);
        put("exampleResourceFile", exampleResourceFile);
        put("exampleMimeType", exampleMimeType);
    }
}
