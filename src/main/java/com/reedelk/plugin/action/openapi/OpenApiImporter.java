package com.reedelk.plugin.action.openapi;

import com.reedelk.openapi.OpenApi;
import com.reedelk.openapi.v3.model.OpenApiObject;
import com.reedelk.openapi.v3.model.PathsObject;
import com.reedelk.plugin.action.openapi.handler.Handlers;
import com.reedelk.plugin.action.openapi.reader.FileReader;
import com.reedelk.plugin.action.openapi.reader.Readers;
import com.reedelk.plugin.action.openapi.serializer.ConfigOpenApiObject;
import com.reedelk.plugin.action.openapi.serializer.Serializer;
import org.jetbrains.annotations.NotNull;

import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;


public class OpenApiImporter {

    private final OpenApiImporterContext context;

    public OpenApiImporter(@NotNull OpenApiImporterContext context) {
        this.context = context;
    }

    public void processImport() throws OpenApiException {
        // Read the OpenAPI file as string.
        String content = createOpenApiReader().read();

        // Deserialize the content into the OpenAPI Model
        OpenApiObject openApiObject = OpenApi.from(content);

        // The logic should be: if the openapi object contains a server on localhost with a port,
        // if does not exist a config using that port, then we can create it, otherwise we come up with a free port.

        String title = "Open API Config";
        String configFileName = OpenApiUtils.configFileNameOf(openApiObject);
        ConfigOpenApiObject configOpenApiObject = new ConfigOpenApiObject(openApiObject);
        String configOpenApiObjectJson = Serializer.toJson(configOpenApiObject, context);
        context.createRestListenerConfig(configFileName, title, configOpenApiObjectJson, "localhost", 8282);

        // Generate REST flows from paths
        PathsObject paths = openApiObject.getPaths();
        paths.getPaths().forEach((pathEntry, pathItem) ->
                Handlers.handle(context, pathEntry, pathItem));
    }

    private FileReader createOpenApiReader() {
        return isNotBlank(context.getOpenApiFilePath()) ?
                new Readers.FileSystemFileReader(context.getOpenApiFilePath()) :
                new Readers.RemoteFileReader(context.getApiFileUrl());
    }
}
