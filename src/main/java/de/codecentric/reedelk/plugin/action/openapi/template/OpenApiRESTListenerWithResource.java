package de.codecentric.reedelk.plugin.action.openapi.template;

import de.codecentric.reedelk.runtime.api.message.content.MimeType;
import org.jetbrains.annotations.NotNull;

public class OpenApiRESTListenerWithResource extends OpenApiRESTListenerWithPayloadSet {

    public OpenApiRESTListenerWithResource(@NotNull String configId,
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
