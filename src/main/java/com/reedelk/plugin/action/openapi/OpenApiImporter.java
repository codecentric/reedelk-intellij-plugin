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
import com.reedelk.plugin.template.OpenAPIRESTListenerConfig;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

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
        ConfigOpenApiObject configOpenApiObject = new ConfigOpenApiObject(
                openApiObject.getInfo(),
                openApiObject.getServers(),
                openApiObject.getComponents());
        String configOpenApiObjectJson = Serializer.toJson(configOpenApiObject, context);
        String title = OpenApiUtils.restListenerConfigTitleFrom(openApiObject);
        String configFileName = OpenApiUtils.restListenerConfigFileNameFrom(openApiObject);

        OpenAPIRESTListenerConfig properties = new OpenAPIRESTListenerConfig();
        properties.setOpenApiObject(configOpenApiObjectJson);
        properties.setId(context.getRestListenerConfigId());
        properties.setBasePath(apiBasePath);
        properties.setPort(apiPort);
        properties.setTitle(title);
        properties.setHost(host);
        context.createRestListenerConfig(configFileName, properties);

        // Generate REST flows from paths
        PathsObject paths = openApiObject.getPaths();
        paths.getPaths().forEach((pathEntry, pathItem) -> Handlers.handle(context, pathEntry, pathItem));
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

    private int getPortOrDefault(ServerObject serverObject) {
        String serverUrl = appendProtocolIfNotExists(serverObject);
        try {
            URL url = new URL(serverUrl);
            return url.getPort() > 0 ? url.getPort() : DefaultConstants.OpenApi.HTTP_PORT;
        } catch (MalformedURLException exception) {
            return DefaultConstants.OpenApi.HTTP_PORT;
        }
    }

    private boolean isLocalhost(ServerObject serverObject) {
        String serverUrl = appendProtocolIfNotExists(serverObject);
        try {
            URL url = new URL(serverUrl);
            String urlHost = url.getHost();
            return DefaultConstants.OpenApi.LOCALHOST.equals(urlHost) ||
                    DefaultConstants.OpenApi.ANY_ADDRESS.equals(urlHost);
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private String extractBasePath(ServerObject serverObject) {
        String serverUrl = appendProtocolIfNotExists(serverObject);
        try {
            URL url = new URL(serverUrl);
            String path = url.getPath();
            String[] pathSegments = path.split("/");
            if (pathSegments.length > 1) {
                // If there are more segments, http://myhost.com/api/v35 we expect: /api/v35.
                String[] subSegments = Arrays.copyOfRange(pathSegments, 1, pathSegments.length);
                return "/" + String.join("/", subSegments);
            } else {
                return "/";
            }
        } catch (MalformedURLException e) {
            return DefaultConstants.OpenApi.BASE_PATH;
        }
    }

    // localhost:8282
    // localhost
    // http://localhost:8282
    // http://localhost
    // We just add the protocol in order to let the java.net object to correctly parse it.
    @NotNull
    private String appendProtocolIfNotExists(ServerObject serverObject) {
        String serverUrl = serverObject.getUrl();
        if (!(serverUrl.startsWith(PROTOCOL_HTTP) || serverUrl.startsWith(PROTOCOL_HTTPS))) {
            serverUrl = PROTOCOL_HTTP + serverUrl;
        }
        return serverUrl;
    }

    private static final String PROTOCOL_HTTP = "http://";
    private static final String PROTOCOL_HTTPS = "https://";
}
