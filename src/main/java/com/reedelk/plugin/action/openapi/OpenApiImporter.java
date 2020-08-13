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
import com.reedelk.plugin.commons.DefaultConstants;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;


public class OpenApiImporter {

    private final OpenApiImporterContext context;
    private String host = DefaultConstants.OpenApi.LOCALHOST;
    private String basePath;
    private int port;
    private String apiTitle = message("openapi.importer.config.default.file.title");

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

        if (openApiObject.getInfo() != null) {
            apiTitle = Optional.ofNullable(openApiObject.getInfo().getTitle())
                    .orElse(message("openapi.importer.config.default.file.title"));
        }

        basePath = getBasePath(openApiObject);
        port = findListenerPort(openApiObject);

        ConfigOpenApiObject configOpenApiObject = new ConfigOpenApiObject(openApiObject);

        String configOpenApiObjectJson = Serializer.toJson(configOpenApiObject, context);

        String title = OpenApiUtils.restListenerConfigTitleOf(openApiObject);
        String configFileName = OpenApiUtils.restListenerConfigFileNameOf(openApiObject);
        context.createRestListenerConfig(configFileName, title, configOpenApiObjectJson, host, port, basePath);

        // Generate REST flows from paths
        PathsObject paths = openApiObject.getPaths();
        paths.getPaths().forEach((pathEntry, pathItem) ->
                Handlers.handle(context, pathEntry, pathItem));
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

    public String getBasePath() {
        return basePath;
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
                .findFirst().orElse(DefaultConstants.OpenApi.HTTP_PORT);
    }

    private String getBasePath(OpenApiObject openApiObject) {
        if (isNotBlank(context.getBasePath())) {
            return context.getBasePath();
        }
        return openApiObject.getServers()
                .stream()
                .findFirst()
                .map(this::extractBasePath)
                .orElse(DefaultConstants.OpenApi.BASE_PATH);
    }

    private int getPortOrDefault(ServerObject serverObject) {
        try {
            URL url = new URL(serverObject.getUrl());
            return url.getPort();
        } catch (MalformedURLException e) {
            return DefaultConstants.OpenApi.HTTP_PORT;
        }
    }

    private boolean isLocalhost(ServerObject serverObject) {
        try {
            URL url = new URL(serverObject.getUrl());
            String host = url.getHost();
            return DefaultConstants.OpenApi.LOCALHOST.equals(host) ||
                    DefaultConstants.OpenApi.ANY_ADDRESS.equals(host);
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private String extractBasePath(ServerObject serverObject) {
        try {
            URL url = new URL(serverObject.getUrl());
            String path = url.getPath();
            String[] pathSegments = path.split("/");
            return pathSegments.length > 1 ? "/" + pathSegments[1] : "/";
        } catch (MalformedURLException e) {
            return DefaultConstants.OpenApi.BASE_PATH;
        }
    }
}
