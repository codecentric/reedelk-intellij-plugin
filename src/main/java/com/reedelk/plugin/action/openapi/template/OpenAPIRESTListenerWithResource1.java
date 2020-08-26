package com.reedelk.plugin.action.openapi.template;

import com.reedelk.runtime.api.message.content.MimeType;
import org.jetbrains.annotations.NotNull;

public class OpenAPIRESTListenerWithResource1 extends OpenAPIRESTListenerWithPayloadSet1 {

    public OpenAPIRESTListenerWithResource1(@NotNull String configId,
                                            @NotNull String flowTitle,
                                            @NotNull String flowDescription,
                                            @NotNull String restListenerDescription,
                                            @NotNull String restPath,
                                            @NotNull String restMethod,
                                            @NotNull String openApiOperationObject,
                                            @NotNull String exampleResourceFile,
                                            @NotNull String exampleMimeType) {
        super(configId, flowTitle, flowDescription, restListenerDescription, restPath, restMethod,
                MimeType.parse(exampleMimeType), openApiOperationObject);
        setProperty("exampleResourceFile", exampleResourceFile);
        setProperty("exampleMimeType", exampleMimeType);
    }
}
