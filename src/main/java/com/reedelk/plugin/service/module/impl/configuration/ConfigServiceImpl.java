package com.reedelk.plugin.service.module.impl.configuration;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.component.descriptor.TypeObjectDescriptor;
import com.reedelk.plugin.commons.FileUtils;
import com.reedelk.plugin.commons.PluginModuleUtils;
import com.reedelk.plugin.component.deserializer.ConfigurationDeserializer;
import com.reedelk.plugin.component.serializer.ConfigurationSerializer;
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.service.module.ConfigService;
import com.reedelk.runtime.commons.FileExtension;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.topic.ReedelkTopics.TOPIC_CONFIG_CHANGE;

public class ConfigServiceImpl implements ConfigService {

    private final Module module;
    private final ConfigChangeListener publisher;

    public ConfigServiceImpl(Module module) {
        this.module = module;
        this.publisher = module.getMessageBus().syncPublisher(TOPIC_CONFIG_CHANGE);
    }

    @Override
    public void fetchConfigurationsBy(TypeObjectDescriptor typeObjectDescriptor) {
        PluginExecutors.runSmartReadAction(module, () -> {
            List<ConfigMetadata> configs = new ArrayList<>();
            ModuleRootManager.getInstance(module).getFileIndex().iterateContent(fileOrDir -> {
                // We must check that the file is still valid before getting the configuration.
                if (fileOrDir.isValid()) {
                    if (FileExtension.CONFIG.value().equals(fileOrDir.getExtension())) {
                        getConfigurationFrom(fileOrDir, typeObjectDescriptor).ifPresent(configs::add);
                    }
                }
                return true;
            });
            publisher.onConfigs(configs);
        });
    }

    @Override
    public void saveConfig(@NotNull ConfigMetadata updatedConfig) {
        WriteCommandAction.runWriteCommandAction(module.getProject(), () -> {
            VirtualFile file = VfsUtil.findFile(Paths.get(updatedConfig.getConfigFile()), true);
            if (file == null) {
                PluginException exception =
                        new PluginException(message("config.error.save.file.not.found",
                                updatedConfig.getId(),
                                updatedConfig.getConfigFile()));
                publisher.onSaveError(exception);
                return;
            }

            Document document = FileDocumentManager.getInstance().getDocument(file);
            if (document == null) {
                PluginException exception =
                        new PluginException(message("config.error.save.document.not.found",
                                updatedConfig.getId(),
                                updatedConfig.getConfigFile()));
                publisher.onSaveError(exception);
                return;
            }

            try {
                String serializedConfig = ConfigurationSerializer.serialize(updatedConfig);
                document.setText(serializedConfig);
            } catch (Exception thrown) {
                publisher.onSaveError(thrown);
            }
        });
    }

    @Override
    public void addConfig(@NotNull ConfigMetadata newConfig) {
        PluginModuleUtils.getConfigsFolder(module).ifPresent(configsFolder ->
                WriteCommandAction.runWriteCommandAction(module.getProject(), () -> {

                    String finalFileName = FileUtils.appendExtensionToFileName(newConfig.getFileName(), FileExtension.CONFIG);
                    final VirtualFile configFile = VfsUtil.findFile(Paths.get(configsFolder, finalFileName), true);
                    if (configFile != null) {
                        PluginException exception =
                                new PluginException(message("config.error.add.file.exists.already", finalFileName));
                        publisher.onAddError(exception);
                        return;
                    }

                    try {

                        VirtualFile configDirectoryVf = VfsUtil.createDirectoryIfMissing(configsFolder);
                        if (configDirectoryVf == null) {
                            PluginException exception =
                                    new PluginException(message("config.error.add.config.dir.not.created", finalFileName));
                            publisher.onAddError(exception);
                            return;
                        }

                        // Create new config file.
                        VirtualFile childData = configDirectoryVf.createChildData(null, finalFileName);

                        // Serialize the config
                        String serializedConfig = ConfigurationSerializer.serialize(newConfig);
                        VfsUtil.saveText(childData, serializedConfig);
                        publisher.onAddSuccess(newConfig);

                    } catch (IOException exception) {
                        publisher.onAddError(exception);
                    }
                }));
    }

    @Override
    public void removeConfig(@NotNull ConfigMetadata toRemove) {
        WriteCommandAction.runWriteCommandAction(module.getProject(), () -> {
            final VirtualFile file = VfsUtil.findFile(Paths.get(toRemove.getConfigFile()), true);
            if (file == null) {
                // The config file to be removed does not exists.
                return;
            }
            try {
                file.delete(null);
                publisher.onRemoveSuccess();
            } catch (IOException exception) {
                String errorMessage = message("config.error.remove", toRemove.getId(), exception.getMessage());
                publisher.onRemoveError(new PluginException(errorMessage, exception));
            }
        });
    }

    public interface ConfigChangeListener {
        default void onConfigs(Collection<ConfigMetadata> configurations) {}
        default void onAddSuccess(ConfigMetadata configuration) {}
        default void onAddError(Exception exception) {}
        default void onSaveError(Exception exception) {}
        default void onRemoveSuccess() {}
        default void onRemoveError(Exception exception) {}
    }

    private Optional<? extends ConfigMetadata> getConfigurationFrom(VirtualFile virtualFile, TypeObjectDescriptor configTypeDescriptor) {
        Optional<Document> maybeDocument = getDocumentFromFile(virtualFile);
        if (maybeDocument.isPresent()) {
            String json = maybeDocument.get().getText();
            return ConfigurationDeserializer.deserialize(json, configTypeDescriptor)
                    .map(dataHolder -> new ExistingConfigMetadata(virtualFile, dataHolder, configTypeDescriptor));
        }
        return Optional.empty();
    }

    private Optional<Document> getDocumentFromFile(VirtualFile virtualFile) {
        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
        return Optional.ofNullable(document);
    }
}