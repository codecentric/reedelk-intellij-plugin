package com.reedelk.plugin.service.module.impl.configuration;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.plugin.commons.FileUtils;
import com.reedelk.plugin.commons.PluginModuleUtils;
import com.reedelk.plugin.component.deserializer.ConfigurationDeserializer;
import com.reedelk.plugin.component.serializer.ConfigurationSerializer;
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.service.module.ConfigurationService;
import com.reedelk.runtime.commons.FileExtension;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.reedelk.plugin.commons.Topics.TOPIC_CONFIG_CHANGE;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public class ConfigurationServiceImpl implements ConfigurationService {

    private final Module module;
    private final ConfigChangeListener publisher;

    public ConfigurationServiceImpl(Module module) {
        this.module = module;
        this.publisher = module.getMessageBus().syncPublisher(TOPIC_CONFIG_CHANGE);
    }

    @Override
    public void loadConfigurationsBy(ObjectDescriptor typeObjectDescriptor) {
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
            // We must publish event by padding the fully qualified name of the object type these
            // configurations refer to. This is because there might be multiple input field subscribing
            // to this publisher event.
            String typeObjectFullyQualifiedName = typeObjectDescriptor.getTypeFullyQualifiedName();
            publisher.onConfigurationsReady(typeObjectFullyQualifiedName, configs);
        });
    }

    @Override
    public void saveConfiguration(@NotNull ConfigMetadata updatedConfig) {
        String configTypeFullyQualifiedName = updatedConfig.getFullyQualifiedName();

        WriteCommandAction.runWriteCommandAction(module.getProject(), () -> {
            VirtualFile file = VfsUtil.findFile(Paths.get(updatedConfig.getConfigFile()), true);
            if (file == null) {
                PluginException exception = new PluginException(message("config.error.save.file.not.found",
                                updatedConfig.getId(),
                                updatedConfig.getConfigFile()));
                publisher.onSaveError(configTypeFullyQualifiedName, exception);
                return;
            }

            Document document = FileDocumentManager.getInstance().getDocument(file);
            if (document == null) {
                PluginException exception = new PluginException(message("config.error.save.document.not.found",
                                updatedConfig.getId(),
                                updatedConfig.getConfigFile()));
                publisher.onSaveError(configTypeFullyQualifiedName, exception);
            } else {
                try {
                    String serializedConfig = ConfigurationSerializer.serialize(updatedConfig);
                    document.setText(serializedConfig);
                } catch (Exception thrown) {
                    publisher.onSaveError(configTypeFullyQualifiedName, thrown);
                }
            }
        });
    }

    @Override
    public void addConfiguration(@NotNull ConfigMetadata newConfig) {
        String configTypeFullyQualifiedName = newConfig.getFullyQualifiedName();

        PluginModuleUtils.getConfigsFolder(module).ifPresent(configsFolder ->
                WriteCommandAction.runWriteCommandAction(module.getProject(), () -> {

                    String finalFileName = FileUtils.appendExtensionToFileName(newConfig.getFileName(), FileExtension.CONFIG);
                    final VirtualFile configFile = VfsUtil.findFile(Paths.get(configsFolder, finalFileName), true);
                    if (configFile != null) {
                        PluginException exception = new PluginException(message("config.error.add.file.exists.already", finalFileName));
                        publisher.onAddError(configTypeFullyQualifiedName, exception);
                        return;
                    }

                    try {
                        VirtualFile configDirectoryVf = VfsUtil.createDirectoryIfMissing(configsFolder);
                        if (configDirectoryVf == null) {
                            PluginException exception = new PluginException(message("config.error.add.config.dir.not.created", finalFileName));
                            publisher.onAddError(configTypeFullyQualifiedName, exception);

                        } else {
                            // Create new config file.
                            VirtualFile childData = configDirectoryVf.createChildData(null, finalFileName);

                            // Serialize the config
                            String serializedConfig = ConfigurationSerializer.serialize(newConfig);
                            VfsUtil.saveText(childData, serializedConfig);
                            publisher.onAddSuccess(configTypeFullyQualifiedName, newConfig);
                        }

                    } catch (IOException exception) {
                        publisher.onAddError(configTypeFullyQualifiedName, exception);
                    }
                }));
    }

    @Override
    public void removeConfiguration(@NotNull ConfigMetadata toRemove) {
        String configTypeFullyQualifiedName = toRemove.getFullyQualifiedName();
        WriteCommandAction.runWriteCommandAction(module.getProject(), () -> {
            final VirtualFile file = VfsUtil.findFile(Paths.get(toRemove.getConfigFile()), true);
            if (file == null) {
                // The config file to be removed does not exists.
                // We consider it successful...
                publisher.onRemoveSuccess(configTypeFullyQualifiedName);
            } else {
                try {
                    file.delete(null);
                    publisher.onRemoveSuccess(configTypeFullyQualifiedName);
                } catch (IOException exception) {
                    String errorMessage = message("config.error.remove", toRemove.getId(), exception.getMessage());
                    publisher.onRemoveError(configTypeFullyQualifiedName, new PluginException(errorMessage, exception));
                }
            }
        });
    }

    private Optional<? extends ConfigMetadata> getConfigurationFrom(VirtualFile virtualFile, ObjectDescriptor configTypeDescriptor) {
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

    public interface ConfigChangeListener {

        void onConfigurationsReady(String typeObjectFullyQualifiedName, Collection<ConfigMetadata> configurations);

        void onAddSuccess(String typeObjectFullyQualifiedName, ConfigMetadata configuration);

        void onAddError(String typeObjectFullyQualifiedName, Exception exception);

        void onSaveError(String typeObjectFullyQualifiedName, Exception exception);

        void onRemoveSuccess(String typeObjectFullyQualifiedName);

        void onRemoveError(String typeObjectFullyQualifiedName, Exception exception);
    }
}
