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
import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;


public class OpenApiImporter {

    private final OpenApiImporterContext context;
    private String host = Defaults.LOCALHOST;
    private int port;
    private String apiTitle = message("openapi.importer.config.default.file.title");

    public OpenApiImporter(@NotNull OpenApiImporterContext context) {
        this.context = context;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getApiTitle() {
        return apiTitle;
    }

    public void processImport() throws OpenApiException {
        // Read the OpenAPI file as string.
        String content = createOpenApiReader().read();

        context.setSchemaFormat(content);

        // Deserialize the content into the OpenAPI Model
        OpenApiObject openApiObject = OpenApi.from(content);

        // The logic should be: if the openapi object contains a server on localhost with a port,
        // if does not exist a config using that port, then we can create it, otherwise we come up with a free port.

        if (openApiObject.getInfo() != null) {
            apiTitle = Optional.ofNullable(openApiObject.getInfo().getTitle())
                    .orElse(message("openapi.importer.config.default.file.title"));
        }

        port = findListenerPort(openApiObject);

        ConfigOpenApiObject configOpenApiObject = new ConfigOpenApiObject(openApiObject);

        String configOpenApiObjectJson = Serializer.toJson(configOpenApiObject, context);

        String title = OpenApiUtils.configTitleOf(openApiObject);
        String configFileName = OpenApiUtils.configFileNameOf(openApiObject);
        context.createRestListenerConfig(configFileName, title, configOpenApiObjectJson, host, port);

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
        if (context.getOpenApiPort() != null) {
            return context.getOpenApiPort();
        }
        return openApiObject.getServers().stream()
                .filter(this::isLocalhost)
                .map(this::getPortOrDefault)
                .findFirst().orElse(Defaults.HTTP_PORT);
    }

    private int getPortOrDefault(ServerObject serverObject) {
        try {
            URL url = new URL(serverObject.getUrl());
            return url.getPort();
        } catch (MalformedURLException e) {
            return Defaults.HTTP_PORT;
        }
    }

    private boolean isLocalhost(ServerObject serverObject) {
        try {
            URL url = new URL(serverObject.getUrl());
            String host = url.getHost();
            return Defaults.LOCALHOST.equals(host) ||
                    Defaults.ANY_ADDRESS.equals(host);
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
