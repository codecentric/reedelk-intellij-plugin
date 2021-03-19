package de.codecentric.reedelk.plugin.action.openapi;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import de.codecentric.reedelk.openapi.commons.DataFormat;
import de.codecentric.reedelk.openapi.v3.model.RequestBodyObject;
import de.codecentric.reedelk.plugin.action.openapi.template.OpenApiRESTListenerConfigProperties;
import de.codecentric.reedelk.plugin.action.openapi.template.OpenApiRESTListenerWithPayloadSet;
import de.codecentric.reedelk.plugin.action.openapi.template.OpenApiRESTListenerWithResource;
import de.codecentric.reedelk.plugin.commons.PluginModuleUtils;
import de.codecentric.reedelk.plugin.template.AssetProperties;
import de.codecentric.reedelk.plugin.template.TemplateWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static de.codecentric.reedelk.plugin.action.openapi.OpenApiConstants.PROJECT_RESOURCE_SEPARATOR;
import static de.codecentric.reedelk.plugin.action.openapi.template.TemplateOpenApi.*;
import static de.codecentric.reedelk.plugin.commons.FileUtils.createDirectory;
import static de.codecentric.reedelk.runtime.api.commons.StringUtils.isBlank;
import static de.codecentric.reedelk.runtime.commons.ModuleProperties.Assets.RESOURCE_DIRECTORY;

public class OpenApiImporterContext {

    private final String basePath;
    private final String apiFileUrl;
    private final String targetDirectory;
    private final String openApiFilePath;
    private final String importModuleName;
    private final String restListenerConfigId;
    private final Integer openApiPort;

    private final Project project;
    private DataFormat schemaFormat;
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
        if (DataFormat.JSON.is(content)) {
            this.schemaFormat = DataFormat.JSON;
        } else if (DataFormat.YAML.is(content)){
            this.schemaFormat = DataFormat.YAML;
        } else {
            throw new OpenApiException("The OpenApi schema is not a valid JSON or YAML. " +
                    "Please make sure that the OpenApi definition is a valid JSON or YAML.");
        }
    }

    public OpenApiExampleFormat exampleFormatOf(String content) {
        if (DataFormat.JSON.is(content)) return OpenApiExampleFormat.JSON;
        if (isXml(content)) return OpenApiExampleFormat.XML;
        return OpenApiExampleFormat.PLAIN_TEXT;
    }

    public DataFormat getSchemaFormat() {
        return schemaFormat;
    }

    public String createAsset(String fileName, AssetProperties properties) {
        Module module = targetImportModule();
        Optional<String> assetsDirectory = PluginModuleUtils.getAssetsDirectory(module);
        assetsDirectory.ifPresent(directory ->
                writeTemplate(ASSET, properties, fileName, directory, false));
        return assetResource(fileName);
    }

    public void createRestListenerFlowWithExample(String fileName, OpenApiRESTListenerWithResource properties) {
        Module module = targetImportModule();
        Optional<String> flowsDirectory = PluginModuleUtils.getFlowsDirectory(module);
        flowsDirectory.ifPresent(directory ->
                writeTemplate(FLOW_WITH_REST_LISTENER_AND_RESOURCE, properties, fileName, directory, true));
    }

    public void createRestListenerFlowWithPayload(String fileName, OpenApiRESTListenerWithPayloadSet properties) {
        Module module = targetImportModule();
        Optional<String> flowsDirectory = PluginModuleUtils.getFlowsDirectory(module);
        flowsDirectory.ifPresent(directory ->
                writeTemplate(FLOW_WITH_REST_LISTENER_AND_PAYLOAD_SET, properties, fileName, directory, true));
    }

    public void createRestListenerConfig(String configFileName, OpenApiRESTListenerConfigProperties properties) {
        Module module = targetImportModule();
        PluginModuleUtils.getConfigsDirectory(module)
                .ifPresent(configsDirectory ->
                        writeTemplate(REST_LISTENER_CONFIG, properties, configFileName, configsDirectory, false));
    }

    private String assetResource(String fileName) {
        String assetResourcePath = isBlank(targetDirectory) ?
                RESOURCE_DIRECTORY.substring(1) + PROJECT_RESOURCE_SEPARATOR + fileName :
                RESOURCE_DIRECTORY.substring(1) + PROJECT_RESOURCE_SEPARATOR + targetDirectory + PROJECT_RESOURCE_SEPARATOR + fileName;
        return removeFrontSlashIfNeeded(assetResourcePath);
    }

    private void writeTemplate(TemplateWriter templateWriter, Properties properties, String finalFileName, String directory, boolean openFile) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            Path finalTargetDirectory = Paths.get(directory, this.targetDirectory);
            createDirectory(finalTargetDirectory).flatMap(targetDirectoryVf ->
                    templateWriter.create(project, properties, targetDirectoryVf, finalFileName))
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

    private static boolean isXml(String dataAsString) {
        try {
            InputSource is = new InputSource(new StringReader(dataAsString));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            builder.parse(is);
            return true;
        } catch (Exception exception) {
            // not xml
            return false;
        }
    }
}
