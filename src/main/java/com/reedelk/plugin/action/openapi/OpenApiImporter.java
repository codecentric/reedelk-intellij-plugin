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

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;


public class OpenApiImporter {

    private final OpenApiImporterContext context;
    private String host = DefaultConstants.OpenApi.LOCALHOST;
    private String apiBasePath;
    private int apiPort;
    private String apiTitle = message("openapi.importer.config.default.file.title");

    public OpenApiImporter(@NotNull OpenApiImporterContext context) {
        this.context = context;
    }

    public void openApiImport() throws OpenApiException {

        // Read the OpenAPI definition as string
        String content = createOpenApiReader().read();
        context.setSchemaFormat(content);

        // Deserialize the content into the OpenAPI Model
        OpenApiObject openApiObject = OpenApi.from(content);

        // Discover open api properties
        apiTitle = OpenApiUtils.getApiTitle(openApiObject);
        apiBasePath = getBasePath(openApiObject);
        apiPort = findListenerPort(openApiObject);

        // Generate REST Listener configuration
        ConfigOpenApiObject configOpenApiObject = new ConfigOpenApiObject(openApiObject);
        String configOpenApiObjectJson = Serializer.toJson(configOpenApiObject, context);
        String title = OpenApiUtils.restListenerConfigTitleFrom(openApiObject);
        String configFileName = OpenApiUtils.restListenerConfigFileNameFrom(openApiObject);
        context.createRestListenerConfig(configFileName, title, configOpenApiObjectJson, host, apiPort, apiBasePath);

        // Generate REST flows from paths
        PathsObject paths = openApiObject.getPaths();
        paths.getPaths().forEach((pathEntry, pathItem) ->
                Handlers.handle(context, pathEntry, pathItem));
    }

    public String getHost() {
        return host;
    }

    public int getApiPort() {
        return apiPort;
    }

    public String getApiTitle() {
        return apiTitle;
    }

    public String getApiBasePath() {
        return apiBasePath;
    }

    private FileReader createOpenApiReader() {
        return isNotBlank(context.getOpenApiFilePath()) ?
                new Readers.FileSystemFileReader(context.getOpenApiFilePath()) :
                new Readers.RemoteFileReader(context.getApiFileUrl());
    }

    int findListenerPort(OpenApiObject openApiObject) {
        if (context.getOpenApiPort() != null) {
            return context.getOpenApiPort();
        }
        return openApiObject.getServers().stream()
                .filter(this::isLocalhost)
                .map(this::getPortOrDefault)
                .findFirst()
                .orElse(DefaultConstants.OpenApi.HTTP_PORT);
    }

    String getBasePath(OpenApiObject openApiObject) {
        if (isNotBlank(context.getBasePath())) {
            return context.getBasePath();
        }
        return openApiObject.getServers()
                .stream()
                .findFirst()
                .map(this::extractBasePath)
                .orElse(DefaultConstants.OpenApi.BASE_PATH);
    }

    int getPortOrDefault(ServerObject serverObject) {
        String serverUrl = appendProtocolIfNotExists(serverObject);
        try {
            URL url = new URL(serverUrl);
            return url.getPort() > 0 ? url.getPort() : DefaultConstants.OpenApi.HTTP_PORT;
        } catch (MalformedURLException exception) {
            return DefaultConstants.OpenApi.HTTP_PORT;
        }
    }

    boolean isLocalhost(ServerObject serverObject) {
        String serverUrl = appendProtocolIfNotExists(serverObject);
        try {
            URL url = new URL(serverUrl);
            String host = url.getHost();
            return DefaultConstants.OpenApi.LOCALHOST.equals(host) ||
                    DefaultConstants.OpenApi.ANY_ADDRESS.equals(host);
        } catch (MalformedURLException e) {
            return false;
        }
    }

    String extractBasePath(ServerObject serverObject) {
        String serverUrl = appendProtocolIfNotExists(serverObject);
        try {
            URL url = new URL(serverUrl);
            String path = url.getPath();
            String[] pathSegments = path.split("/");
            return pathSegments.length > 1 ? "/" + pathSegments[1] : "/";
        } catch (MalformedURLException e) {
            return DefaultConstants.OpenApi.BASE_PATH;
        }
    }

    // localhost:8282
    // localhost
    // http://localhost:8282
    // http://localhost
    @NotNull
    private String appendProtocolIfNotExists(ServerObject serverObject) {
        String serverUrl = serverObject.getUrl();
        if (!(serverUrl.startsWith("http://") || serverUrl.startsWith("https://"))) {
            serverUrl = "http://" + serverUrl;
        }
        return serverUrl;
    }
}
