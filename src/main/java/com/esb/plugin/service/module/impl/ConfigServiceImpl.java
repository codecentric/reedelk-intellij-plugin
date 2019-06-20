package com.esb.plugin.service.module.impl;

import com.esb.internal.commons.FileExtension;
import com.esb.plugin.service.module.ConfigService;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.esb.internal.commons.JsonParser.Config;
import static com.esb.internal.commons.JsonParser.Implementor;
import static java.util.stream.Collectors.toList;

public class ConfigServiceImpl implements ConfigService {

    private final Module module;

    public ConfigServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public List<ConfigMetadata> listConfigs(String fullyQualifiedName) {
        return listConfigs()
                .stream()
                .filter(configMetadata ->
                        configMetadata.getFullyQualifiedName().equals(fullyQualifiedName))
                .collect(toList());
    }

    @Override
    public void saveConfig(ConfigMetadata selectedMetadata) {
        VirtualFile virtualFile = selectedMetadata.getVirtualFile();
        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);

        //VfsUtil.createChildSequent()
        String serializedConfig = serialize(selectedMetadata);
        document.setText(serializedConfig);
    }

    private String serialize(ConfigMetadata selectedMetadata) {
        return null;
    }

    private List<ConfigMetadata> listConfigs() {
        List<ConfigMetadata> configs = new ArrayList<>();
        ModuleRootManager.getInstance(module).getFileIndex().iterateContent(fileOrDir -> {
            if (FileExtension.FLOW_CONFIG.value().equals(fileOrDir.getExtension())) {
                getMetadataFrom(fileOrDir).ifPresent(configs::add);
            }
            return true;
        });
        return configs;
    }

    private Optional<ConfigMetadata> getMetadataFrom(VirtualFile virtualFile) {
        try {
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
            if (document != null) {
                String json = document.getText();
                ConfigMetadata metadata = createMetadata(virtualFile, json);
                return Optional.of(metadata);
            }
        } catch (Exception e) {
            // nothing to do.
        }
        return Optional.empty();
    }

    private ConfigMetadata createMetadata(VirtualFile virtualFile, String json) {
        JSONObject configDefinition = new JSONObject(json);
        String id = Config.id(configDefinition);
        String title = Config.title(configDefinition);
        String fullyQualifiedName = Implementor.name(configDefinition);
        return new ConfigMetadata(fullyQualifiedName, id, title, virtualFile, configDefinition);
    }
}