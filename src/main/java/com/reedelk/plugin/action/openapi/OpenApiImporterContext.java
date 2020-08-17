package com.reedelk.plugin.action.openapi;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.reedelk.openapi.v3.model.RequestBodyObject;
import com.reedelk.plugin.commons.PluginModuleUtils;
import com.reedelk.plugin.template.AssetProperties;
import com.reedelk.plugin.template.OpenAPIRESTListenerConfig;
import com.reedelk.plugin.template.OpenAPIRESTListenerWithPayloadSet;
import com.reedelk.plugin.template.OpenAPIRESTListenerWithResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.yaml.snakeyaml.Yaml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.reedelk.plugin.commons.FileUtils.createDirectory;
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

    public Optional<RequestBodyObject> getRequestBodyById(String requestBodyId) {
        return Optional.ofNullable(requestBodyIdAndData.get(requestBodyId));
    }

    public void registerAssetPath(String schemaId, String schemaAssetPath) {
        schemaIdAndPathMap.put(schemaId, schemaAssetPath);
    }

    public Optional<String> getAssetFrom(String wantedSchemaId) {
        for (Map.Entry<String, String> schemaIdAndPath : schemaIdAndPathMap.entrySet()) {
            String schemaId = schemaIdAndPath.getKey();
            if (wantedSchemaId.endsWith(schemaId)) {
                return Optional.of(schemaIdAndPath.getValue());
            }
        }
        return Optional.empty();
    }

    public void setSchemaFormat(String content) throws OpenApiException {
        if (isJson(content)) {
            this.schemaFormat = OpenApiSchemaFormat.JSON;
        } else if (isYaml(content)){
            this.schemaFormat = OpenApiSchemaFormat.YAML;
        } else {
            throw new OpenApiException("The OpenApi schema is not a valid JSON or YAML. " +
                    "Please make sure that the OpenApi definition is a valid JSON or YAML.");
        }
    }

    public OpenApiExampleFormat exampleFormatOf(String content) {
        if (isJson(content)) return OpenApiExampleFormat.JSON;
        if (isXml(content)) return OpenApiExampleFormat.XML;
        return OpenApiExampleFormat.PLAIN_TEXT;
    }

    public OpenApiSchemaFormat getSchemaFormat() {
        return schemaFormat;
    }

    public String createAsset(String fileName, AssetProperties properties) {
        Module module = targetImportModule();
        Optional<String> assetsDirectory = PluginModuleUtils.getAssetsDirectory(module);
        assetsDirectory.ifPresent(directory ->
                createBuildable(ASSET, properties, fileName, directory, false));
        return assetResource(fileName);
    }

    public void createRestListenerFlowWithExample(String fileName, OpenAPIRESTListenerWithResource properties) {
        Module module = targetImportModule();
        Optional<String> flowsDirectory = PluginModuleUtils.getFlowsDirectory(module);
        flowsDirectory.ifPresent(directory ->
                createBuildable(FLOW_WITH_REST_LISTENER_AND_RESOURCE, properties, fileName, directory, true));
    }

    public void createRestListenerFlow(String fileName, OpenAPIRESTListenerWithPayloadSet properties) {
        Module module = targetImportModule();
        Optional<String> flowsDirectory = PluginModuleUtils.getFlowsDirectory(module);
        flowsDirectory.ifPresent(directory ->
                createBuildable(FLOW_WITH_REST_LISTENER_AND_PAYLOAD_SET, properties, fileName, directory, true));
    }

    public void createRestListenerConfig(String configFileName, OpenAPIRESTListenerConfig properties) {
        Module module = targetImportModule();
        PluginModuleUtils.getConfigsDirectory(module)
                .ifPresent(configsDirectory ->
                        createBuildable(REST_LISTENER_CONFIG, properties, configFileName, configsDirectory, false));
    }

    private String assetResource(String fileName) {
        String assetResourcePath = isBlank(targetDirectory) ?
                RESOURCE_DIRECTORY.substring(1) + File.separator + fileName :
                RESOURCE_DIRECTORY.substring(1) + File.separator + targetDirectory + File.separator + fileName;
        return removeFrontSlashIfNeeded(assetResourcePath);
    }

    private void createBuildable(Buildable buildable, Properties properties, String finalFileName, String directory, boolean openFile) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            Path finalTargetDirectory = Paths.get(directory, this.targetDirectory);
            createDirectory(finalTargetDirectory).flatMap(targedDirectoryVf ->
                    buildable.create(project, properties, targedDirectoryVf, finalFileName))
                    .ifPresent(virtualFile -> {
                        if (openFile) FileEditorManager.getInstance(project).openFile(virtualFile, false);
            });
        });
    }

    private Module targetImportModule() {
        return ModuleManager.getInstance(project).findModuleByName(importModuleName);
    }

    // We remove the first '/' from the directory
    // By convention an asset resource does not start with a front slash.
    private String removeFrontSlashIfNeeded(String path) {
        return path.startsWith("/") ?  path.substring(1) : path;
    }

    private boolean isXml(String content) {
        try {
            InputSource is = new InputSource(new StringReader(content));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            builder.parse(is);
            return true;
        } catch (Exception exception) {
            // not xml
        }
        return false;
    }

    private boolean isYaml(String content) {
        try {
            new Yaml().load(content);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private boolean isJson(String content) {
        try {
            new JSONObject(content);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
