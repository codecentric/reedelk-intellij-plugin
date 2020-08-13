package com.reedelk.plugin.action.openapi;

import com.reedelk.openapi.OpenApi;
import com.reedelk.openapi.v3.model.OpenApiObject;
import com.reedelk.openapi.v3.model.PathsObject;
import com.reedelk.openapi.v3.model.ServerObject;
import com.reedelk.plugin.action.openapi.handler.Handlers;
import com.reedelk.plugin.action.openapi.reader.FileReader;
import com.reedelk.plugin.action.openapi.reader.Readers;
import com.reedelk.plugin.action.openapi.serializer.ConfigOpenApiObject;
import com.reedelk.plugin.action.openapi.serializer.Serializer;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;


public class OpenApiImporter {

    private static final int DEFAULT_PORT = 8484;
    private static final String LOCALHOST = "localhost";
    private static final String ANY_ADDRESS = "0.0.0.0";

    private final OpenApiImporterContext context;

    public OpenApiImporter(@NotNull OpenApiImporterContext context) {
        this.context = context;
    }

    public void processImport() throws OpenApiException {
        // Read the OpenAPI file as string.
        String content = createOpenApiReader().read();

        context.setSchemaFormat(content);

        // Deserialize the content into the OpenAPI Model
        OpenApiObject openApiObject = OpenApi.from(content);

        // The logic should be: if the openapi object contains a server on localhost with a port,
        // if does not exist a config using that port, then we can create it, otherwise we come up with a free port.

        String title = "Open API Config";
        int listenerPort = findListenerPort(openApiObject);

        ConfigOpenApiObject configOpenApiObject = new ConfigOpenApiObject(openApiObject);

        String configOpenApiObjectJson = Serializer.toJson(configOpenApiObject, context);
        String configFileName = OpenApiUtils.configFileNameOf(openApiObject);
        context.createRestListenerConfig(configFileName, title, configOpenApiObjectJson, LOCALHOST, listenerPort);

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

    private int findListenerPort(OpenApiObject openApiObject) {
        return openApiObject.getServers().stream()
                .filter(this::isLocalhost)
                .map(this::getPortOrDefault)
                .findFirst().orElse(DEFAULT_PORT);
    }

    private int getPortOrDefault(ServerObject serverObject) {
        try {
            URL url = new URL(serverObject.getUrl());
            return url.getPort();
        } catch (MalformedURLException e) {
            return OpenApiImporter.DEFAULT_PORT;
        }
    }

    private boolean isLocalhost(ServerObject serverObject) {
        try {
            URL url = new URL(serverObject.getUrl());
            String host = url.getHost();
            return LOCALHOST.equals(host) ||
                    ANY_ADDRESS.equals(host);
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
