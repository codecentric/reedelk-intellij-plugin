package com.reedelk.plugin.action.openapi;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.reedelk.openapi.v3.model.RequestBodyObject;
import com.reedelk.plugin.commons.PluginModuleUtils;
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.template.AssetProperties;
import com.reedelk.plugin.template.RestListenerOpenApiConfigProperties;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.commons.ModuleProperties;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.reedelk.plugin.template.Template.Buildable;
import static com.reedelk.plugin.template.Template.OpenAPI;

public class OpenApiImporterContext {

    private final String openApiFilePath;
    private final String importModuleName;
    private final String targetDirectory;
    private final Project project;
    private final String restListenerConfigId;
    private final String apiFileUrl;

    private Map<String, String> schemaIdAndPath = new HashMap<>();
    private Map<String, RequestBodyObject> requestBodyIdAndData = new HashMap<>();

    public OpenApiImporterContext(@NotNull Project project, String openAPIFilePath, String importModuleName, String targetDirectory, String apiFileUrl) {
        this.restListenerConfigId = UUID.randomUUID().toString();
        this.project = project;
        this.apiFileUrl = apiFileUrl;
        this.targetDirectory = targetDirectory;
        this.openApiFilePath = openAPIFilePath;
        this.importModuleName = importModuleName;
    }

    public String getOpenApiFilePath() {
        return openApiFilePath;
    }

    public String getApiFileUrl() {
        return apiFileUrl;
    }

    public String getRestListenerConfigId() {
        return restListenerConfigId;
    }

    public void registerRequestBody(String requestBodyId, RequestBodyObject requestBodyMediaType) {
        requestBodyIdAndData.put(requestBodyId, requestBodyMediaType);
    }

    public RequestBodyObject getRequestBodyById(String requestBodyId) {
        return requestBodyIdAndData.get(requestBodyId);
    }

    public void register(String schemaId, String schemaAssetPath) {
        schemaIdAndPath.put(schemaId, schemaAssetPath);
    }

    public Optional<String> assetFrom(String $ref) {
        for (Map.Entry<String, String> schemaIdAndPath : schemaIdAndPath.entrySet()) {
            String schemaId = schemaIdAndPath.getKey();
            if ($ref.endsWith(schemaId)) {
                return Optional.of(schemaIdAndPath.getValue());
            }
        }
        return Optional.empty();
    }

    // TODO: Can we rely on the extension for file url as well? perhaps the safest bet is to parse it.
    public OpenApiSchemaFormat getSchemaFormat() {
        if (StringUtils.isNotBlank(openApiFilePath)) {
            return OpenApiSchemaFormat.formatOf(openApiFilePath);
        } else {
            return OpenApiSchemaFormat.formatOf(apiFileUrl);
        }
    }

    public String createAsset(String fileName, String data) {
        Module module = getImportModule();
        Properties properties = new AssetProperties(data);
        Optional<String> assetsDirectory = PluginModuleUtils.getAssetsDirectory(module);
        assetsDirectory.ifPresent(directory ->
                createBuildable(OpenAPI.ASSET, properties, fileName, directory, false));
        return assetResource(fileName);
    }

    public void createRestListenerFlow(String fileName, Properties properties) {
        Module module = getImportModule();
        Optional<String> flowsDirectory = PluginModuleUtils.getFlowsDirectory(module);
        flowsDirectory.ifPresent(directory ->
                createBuildable(OpenAPI.FLOW_WITH_REST_LISTENER, properties, fileName, directory, true));
    }

    public void createRestListenerConfig(String configFileName, String configTitle, String configOpenApiObject, String host, int port) {
        Module module = getImportModule();
        RestListenerOpenApiConfigProperties properties =
                new RestListenerOpenApiConfigProperties(restListenerConfigId, configTitle, host, port, configOpenApiObject);
        PluginModuleUtils.getConfigsDirectory(module)
                .ifPresent(configsDirectory ->
                        createBuildable(OpenAPI.REST_LISTENER_CONFIG, properties, configFileName, configsDirectory, false));
    }

    private String assetResource(String fileName) {
        if (StringUtils.isBlank(targetDirectory)) {
            return ModuleProperties.Assets.RESOURCE_DIRECTORY + File.separator + fileName;
        } else {
            return ModuleProperties.Assets.RESOURCE_DIRECTORY +
                    File.separator + targetDirectory + File.separator + fileName;
        }
    }

    private Module getImportModule() {
        return ModuleManager.getInstance(project).findModuleByName(importModuleName);
    }

    private void createBuildable(Buildable buildable, Properties properties, String finalFileName, String directory, boolean openFile) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            Path targetDirectory = Paths.get(directory, this.targetDirectory);
            createDirectoryIfMissing(targetDirectory);
            Optional.ofNullable(VfsUtil.findFile(targetDirectory, true))
                    .flatMap(targetDirectoryVf ->
                            buildable.create(project, properties, targetDirectoryVf, finalFileName))
                    .ifPresent(virtualFile -> {
                        if (openFile) FileEditorManager.getInstance(project).openFile(virtualFile, false);
                    });
        });
    }

    private void createDirectoryIfMissing(Path targetDirectory) throws PluginException {
        try {
            VfsUtil.createDirectoryIfMissing(targetDirectory.toString());
        } catch (IOException e) {
            // TODO: Extract resources
            throw new PluginException("Could not create directory=[%s], cause: " + e.getMessage());
        }
    }
}
