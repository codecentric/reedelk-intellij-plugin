package com.reedelk.plugin.service.module.impl.script;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.commons.PluginModuleUtils;
import com.reedelk.plugin.commons.ScriptFileNameValidator;
import com.reedelk.plugin.commons.ScriptResourceUtil;
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.service.module.ScriptService;
import com.reedelk.runtime.commons.FileExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.reedelk.plugin.commons.Topics.TOPIC_SCRIPT_RESOURCE;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.Collections.unmodifiableList;

public class ScriptServiceImpl implements ScriptService {

    private final ScriptResourceChangeListener publisher;
    private final Module module;

    public ScriptServiceImpl(Module module) {
        this.module = module;
        this.publisher = module.getMessageBus().syncPublisher(TOPIC_SCRIPT_RESOURCE);
    }

    @Override
    public void fetchScriptResources() {
        // If the scripts folder is empty, it means that there is no resources folder created
        // in the current project, therefore no action is required.
        PluginModuleUtils.getScriptsFolder(module).ifPresent(scriptsFolder -> {
            // We access the index, therefore we must wait to access.
            PluginExecutors.runSmartReadAction(module, () -> {
                List<ScriptResource> scripts = new ArrayList<>();
                ModuleRootManager.getInstance(module).getFileIndex().iterateContent(scriptFile -> {
                    if (FileExtension.SCRIPT.value().equals(scriptFile.getExtension())) {
                        if (scriptFile.getPresentableUrl().startsWith(scriptsFolder)) {
                            // We keep the path from .../resource/scripts to the end.
                            // The script root is therefore /resource/scripts.
                            String substring = scriptFile.getPresentableUrl().substring(scriptsFolder.length() + 1);
                            scripts.add(new ScriptResource(substring, scriptFile.getNameWithoutExtension()));
                        }
                    }
                    return true;
                });
                publisher.onScriptResources(unmodifiableList(scripts));
            });
        });
    }

    @Override
    public void addScript(String scriptFileName, String scriptBody) {

        if (!ScriptFileNameValidator.validate(scriptFileName)) {
            publisher.onAddError(new PluginException(message("script.file.name.validation.error")));
            return;
        }

        // If the scripts folder is empty, it means that there is no resources folder created
        // in the current project, therefore no action is required.
        PluginModuleUtils.getScriptsFolder(module).ifPresent(scriptsDirectory ->
                WriteCommandAction.runWriteCommandAction(module.getProject(), () -> {

                    final String normalizedScriptFile = ScriptResourceUtil.normalize(scriptFileName);
                    final Path normalizedScriptFilePath = Paths.get(normalizedScriptFile);

                    String directoryUpToScriptFile = scriptsDirectory;

                    Path parent = normalizedScriptFilePath.getParent();
                    if (parent != null) {
                        // The script file name is: dir1/dir2/my_script.js
                        // We must concatenate: src/main/resource to dir1/dir2.
                        directoryUpToScriptFile = Paths.get(directoryUpToScriptFile, parent.toString()).toString();
                    }

                    try {
                        // Create the scripts directory if does not exists already.
                        VirtualFile directoryVirtualFile = VfsUtil.createDirectoryIfMissing(directoryUpToScriptFile);
                        if (directoryVirtualFile == null) {
                            PluginException error = new PluginException(message("directory.error.create", scriptsDirectory));
                            publisher.onAddError(error);
                            return;
                        }

                        // Create the script file
                        String scriptFileNameWithExtension = normalizedScriptFilePath.getFileName().toString();
                        VirtualFile addedScriptVirtualFile = directoryVirtualFile.createChildData(null, scriptFileNameWithExtension);
                        VfsUtil.saveText(addedScriptVirtualFile, scriptBody);

                        String scriptResourcePath = addedScriptVirtualFile.getPath().substring(scriptsDirectory.length() + 1);
                        ScriptResource scriptResource = new ScriptResource(scriptResourcePath, addedScriptVirtualFile.getNameWithoutExtension());
                        publisher.onAddSuccess(scriptResource);

                    } catch (IOException exception) {
                        publisher.onAddError(exception);
                    }
                }));
    }

    @Override
    public void removeScript(String scriptFileName) {
        PluginModuleUtils.getScriptsFolder(module).ifPresent(scriptsDirectory ->
                WriteCommandAction.runWriteCommandAction(module.getProject(), () -> {
                    final VirtualFile file = VfsUtil.findFile(Paths.get(scriptsDirectory, scriptFileName), true);
                    if (file == null) {
                        // The script file to be removed does not exists.
                        return;
                    }
                    try {
                        file.delete(null);
                        publisher.onRemoveSuccess();
                    } catch (IOException exception) {
                        String errorMessage = message("script.error.remove", scriptFileName, exception.getMessage());
                        publisher.onRemoveError(new PluginException(errorMessage, exception));
                    }
                }));
    }

    public interface ScriptResourceChangeListener {
        void onScriptResources(Collection<ScriptResource> scriptResources);
        void onAddSuccess(ScriptResource resource);
        void onAddError(Exception exception);
        void onRemoveSuccess();
        void onRemoveError(Exception exception);
    }
}
