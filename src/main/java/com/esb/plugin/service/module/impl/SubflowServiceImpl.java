package com.esb.plugin.service.module.impl;

import com.esb.internal.commons.FileExtension;
import com.esb.internal.commons.FileUtils;
import com.esb.internal.commons.JsonParser;
import com.esb.plugin.service.module.SubflowService;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubflowServiceImpl implements SubflowService {

    private final Module module;

    public SubflowServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public List<SubflowMetadata> listSubflows() {
        List<SubflowMetadata> subflows = new ArrayList<>();
        getResourcesFolder().ifPresent(resourceFolder ->
                getSubflowFiles(resourceFolder).forEach(subFlowFile ->
                        getMetadataFrom(subFlowFile).ifPresent(subflows::add)));
        return subflows;
    }

    private Optional<SubflowMetadata> getMetadataFrom(String subflowFile) {
        try {
            String json = FileUtils.readFrom(subflowFile);
            JSONObject subFlowDefinition = new JSONObject(json);
            String id = JsonParser.Subflow.id(subFlowDefinition);
            String title = JsonParser.Subflow.title(subFlowDefinition);
            return Optional.of(new SubflowMetadata(id, title));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private List<String> getSubflowFiles(String rootDirectory) {
        try {
            return Files.walk(Paths.get(rootDirectory))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith("." + FileExtension.SUBFLOW.value()))
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private Optional<String> getResourcesFolder() {
        String[] urls = ModuleRootManager.getInstance(module)
                .orderEntries()
                .withoutLibraries()
                .withoutSdk()
                .sources()
                .getUrls();
        return Arrays.stream(urls)
                .filter(sourcesUrls -> sourcesUrls.endsWith("resources"))
                .map(s -> {
                    try {
                        URI uri = new URL(s).toURI();
                        return new File(uri).getAbsolutePath();
                    } catch (MalformedURLException | URISyntaxException e) {
                        return "";
                    }
                })
                .findFirst();
    }
}
