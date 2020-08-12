package com.reedelk.plugin.action.openapi;

import com.intellij.openapi.components.ServiceManager;
import com.reedelk.openapi.OpenApi;
import com.reedelk.openapi.v3.model.OpenApiObject;
import com.reedelk.openapi.v3.model.PathsObject;
import com.reedelk.plugin.action.openapi.handler.Handlers;
import com.reedelk.plugin.action.openapi.serializer.ConfigOpenApiObject;
import com.reedelk.plugin.action.openapi.serializer.Serializer;
import com.reedelk.plugin.commons.FileUtils;
import com.reedelk.plugin.service.module.impl.http.HttpResponse;
import com.reedelk.plugin.service.module.impl.http.HttpService;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


public class OpenApiImporter {

    private final OpenApiImporterContext context;

    public OpenApiImporter(@NotNull OpenApiImporterContext context) {
        this.context = context;
    }

    public void processImport() throws OpenApiException {
        // Read the OpenAPI file as string.
        String content = read();

        // Deserialize the content into the OpenAPI Model
        OpenApiObject openApiObject = OpenApi.from(content);


        // Generate listener config
        String openApiConfigFileName = OpenApiUtils.configFileNameOf(openApiObject);
        ConfigOpenApiObject configOpenApiObject = new ConfigOpenApiObject(openApiObject);
        String configOpenApiObjectJson = Serializer.toJson(configOpenApiObject, context);
        context.createConfig(openApiConfigFileName, configOpenApiObjectJson);

        // Generate REST flows from paths
        PathsObject paths = openApiObject.getPaths();
        paths.getPaths().forEach((pathEntry, pathItem) ->
                Handlers.handle(context, pathEntry, pathItem));
    }

    private String read() throws OpenApiException {
        if (context.getOpenApiFilePath().startsWith("http://") ||
        context.getOpenApiFilePath().startsWith("https://")) {
            return new RemoteFileReader().read(context.getOpenApiFilePath());
        } else {
            return new FileSystemFileReader().read(context.getOpenApiFilePath());
        }
    }

    interface FileReader {
        String read(String openApiResource) throws OpenApiException;
    }

    static class FileSystemFileReader implements FileReader {
        @Override
        public String read(String openApiResource) throws OpenApiException {
            try {
                return FileUtils.readFileToString(openApiResource);
            } catch (IOException exception) {
                throw new OpenApiException("Could not read OpenAPI file", exception);
            }
        }
    }

    static class RemoteFileReader implements FileReader {

        @Override
        public String read(String openApiResource) throws OpenApiException {
            HttpService service = ServiceManager.getService(HttpService.class);
            HttpResponse httpResponse;
            try {
                httpResponse = service.get(openApiResource);
            } catch (IOException e) {
                throw new OpenApiException("Could not read OpenAPI file", e);
            }

            if (httpResponse.getStatus() == 200) {
                return httpResponse.getBody();
            } else {
                throw new OpenApiException("Could not read OpenAPI file");
            }
        }
    }
}
