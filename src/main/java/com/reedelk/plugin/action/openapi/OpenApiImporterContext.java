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
import com.reedelk.plugin.template.OpenAPIRESTListenerConfig;
import com.reedelk.plugin.template.OpenAPIRESTListenerWithPayloadSet;
import com.reedelk.plugin.template.OpenAPIRESTListenerWithResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.template.Template.Buildable;
import static com.reedelk.plugin.template.Template.OpenAPI.*;
import static com.reedelk.runtime.api.commons.StringUtils.isBlank;
import static com.reedelk.runtime.commons.ModuleProperties.Assets.RESOURCE_DIRECTORY;

public class OpenApiImporterContext {

    private final String basePath;
    private final Project project;
    private final String apiFileUrl;
    private final Integer openApiPort;
    private final String targetDirectory;
    private final String openApiFilePath;
    private final String importModuleName;
    private final String restListenerConfigId;

    private OpenApiSchemaFormat schemaFormat;

    private Map<String, String> schemaIdAndPathMap = new HashMap<>();
    private Map<String, RequestBodyObject> requestBodyIdAndData = new HashMap<>();

    public OpenApiImporterContext(@NotNull Project project,
                                  @Nullable String openAPIFilePath,
                                  @NotNull String importModuleName,
                                  @Nullable String targetDirectory,
                                  @Nullable String apiFileUrl,
                                  @Nullable Integer openApiPort,
                                  @Nullable String basePath) {
        this.project = project;
        this.basePath = basePath;
        this.apiFileUrl = apiFileUrl;
        this.openApiPort = openApiPort;
        this.targetDirectory = targetDirectory;
        this.openApiFilePath = openAPIFilePath;
        this.importModuleName = importModuleName;
        this.restListenerConfigId = UUID.randomUUID().toString();
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

    public Integer getOpenApiPort() {
        return openApiPort;
    }

    public String getBasePath() {
        return basePath;
    }

    public void registerRequestBody(String requestBodyId, RequestBodyObject requestBodyMediaType) {
        requestBodyIdAndData.put(requestBodyId, requestBodyMediaType);
    }

    public RequestBodyObject getRequestBodyById(String requestBodyId) {
        return requestBodyIdAndData.get(requestBodyId);
    }

    public void register(String schemaId, String schemaAssetPath) {
        schemaIdAndPathMap.put(schemaId, schemaAssetPath);
    }

    public Optional<String> assetFrom(String $ref) {
        for (Map.Entry<String, String> schemaIdAndPath : schemaIdAndPathMap.entrySet()) {
            String schemaId = schemaIdAndPath.getKey();
            if ($ref.endsWith(schemaId)) {
                return Optional.of(schemaIdAndPath.getValue());
            }
        }
        return Optional.empty();
    }

    public void setSchemaFormat(String content) {
        try {
            new JSONObject(content);
            this.schemaFormat = OpenApiSchemaFormat.JSON;
        } catch (Exception e) {
            // not JSON, but YAML
            this.schemaFormat = OpenApiSchemaFormat.YAML;
        }
    }

    public OpenApiSchemaFormat getSchemaFormat() {
        return schemaFormat;
    }

    public String createAsset(String fileName, AssetProperties properties) {
        Module module = getImportModule();
        Optional<String> assetsDirectory = PluginModuleUtils.getAssetsDirectory(module);
        assetsDirectory.ifPresent(directory ->
                createBuildable(ASSET, properties, fileName, directory, false));
        return assetResource(fileName);
    }

    public void createRestListenerFlowWithExample(String fileName, OpenAPIRESTListenerWithResource properties) {
        Module module = getImportModule();
        Optional<String> flowsDirectory = PluginModuleUtils.getFlowsDirectory(module);
        flowsDirectory.ifPresent(directory ->
                createBuildable(FLOW_WITH_REST_LISTENER_AND_RESOURCE, properties, fileName, directory, true));
    }

    public void createRestListenerFlow(String fileName, OpenAPIRESTListenerWithPayloadSet properties) {
        Module module = getImportModule();
        Optional<String> flowsDirectory = PluginModuleUtils.getFlowsDirectory(module);
        flowsDirectory.ifPresent(directory ->
                createBuildable(FLOW_WITH_REST_LISTENER_AND_PAYLOAD_SET, properties, fileName, directory, true));
    }

    public void createRestListenerConfig(String configFileName, OpenAPIRESTListenerConfig properties) {
        Module module = getImportModule();
        PluginModuleUtils.getConfigsDirectory(module)
                .ifPresent(configsDirectory ->
                        createBuildable(REST_LISTENER_CONFIG, properties, configFileName, configsDirectory, false));
    }

    private String assetResource(String fileName) {
        String assetResourcePath = isBlank(targetDirectory) ?
                RESOURCE_DIRECTORY.substring(1) + File.separator + fileName :
                RESOURCE_DIRECTORY.substring(1) + File.separator + targetDirectory + File.separator + fileName;
        // We remove the first '/' from the directory
        return removeFrontSlashIfNeeded(assetResourcePath);
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

    // TODO: Refactor this one extract htis logic:
    private void createDirectoryIfMissing(Path targetDirectory) throws PluginException {
        try {
            VfsUtil.createDirectoryIfMissing(targetDirectory.toString());
        } catch (IOException exception) {
            String message = message("openapi.importer.create.directory.error", targetDirectory.toString(), exception.getMessage());
            throw new PluginException(message);
        }
    }

    private Module getImportModule() {
        return ModuleManager.getInstance(project).findModuleByName(importModuleName);
    }

    private String removeFrontSlashIfNeeded(String path) {
        return path.startsWith("/") ?  path.substring(1) : path;
    }
}
