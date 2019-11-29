package com.reedelk.plugin.service.module.impl;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.Topic;
import com.reedelk.plugin.commons.FileUtils;
import com.reedelk.plugin.commons.ModuleUtils;
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.executor.PluginExecutor;
import com.reedelk.plugin.service.module.ScriptService;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.commons.FileExtension;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.reedelk.plugin.commons.Messages.Misc.COULD_NOT_CREATE_DIRECTORY;
import static com.reedelk.plugin.commons.Messages.Script.ERROR_FILE_NAME_EMPTY;
import static com.reedelk.plugin.commons.Messages.Script.ERROR_REMOVE;

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
        ModuleUtils.getScriptsFolder(module).ifPresent(scriptsFolder -> {
            // We access the index, therefore we must wait to access.
            ReadAction.nonBlocking(() -> {

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

                publisher.onScriptResources(scripts);

            }).submit(PluginExecutor.getInstance());
        });
    }

    @Override
    public void addScript(String scriptFileName) {
        if (StringUtils.isBlank(scriptFileName)) {
            publisher.onAddError(new PluginException(ERROR_FILE_NAME_EMPTY.format()));
            return;
        }

        final String finalScriptFileName = FileUtils.appendExtensionToFileName(scriptFileName, FileExtension.SCRIPT);

        // If the scripts folder is empty, it means that there is no resources folder created
        // in the current project, therefore no action is required.
        ModuleUtils.getScriptsFolder(module).ifPresent(scriptsDirectory ->
                WriteCommandAction.runWriteCommandAction(module.getProject(), () -> {
                    try {
                        // Create the scripts directory if does not exists already.
                        VirtualFile directoryVirtualFile = VfsUtil.createDirectoryIfMissing(scriptsDirectory);
                        if (directoryVirtualFile == null) {
                            PluginException error = new PluginException(COULD_NOT_CREATE_DIRECTORY.format(scriptsDirectory));
                            publisher.onAddError(error);
                            return;
                        }

                        // Create the script file
                        VirtualFile addedScriptVf = directoryVirtualFile.createChildData(null, finalScriptFileName);
                        publisher.onAddSuccess(new ScriptResource(finalScriptFileName, addedScriptVf.getNameWithoutExtension()));
                    } catch (IOException exception) {
                        publisher.onAddError(exception);
                    }
                }));
    }

    @Override
    public void removeScript(String scriptFileName) {
        ModuleUtils.getScriptsFolder(module).ifPresent(scriptsDirectory ->
                WriteCommandAction.runWriteCommandAction(module.getProject(), () -> {
                    final VirtualFile file = VfsUtil.findFile(Paths.get(scriptsDirectory, scriptFileName), true);
                    if (file == null) {
                        // The file to remove does not exists.
                        return;
                    }

                    try {
                        file.delete(null);
                        publisher.onRemoveSuccess();
                    } catch (IOException exception) {
                        String errorMessage = ERROR_REMOVE.format(scriptFileName, exception.getMessage());
                        publisher.onRemoveError(new PluginException(errorMessage, exception));
                    }
                }));
    }


    public interface ScriptResourceChangeListener {
        default void onScriptResources(Collection<ScriptResource> scriptResources) {}
        default void onAddSuccess(ScriptResource resource) {}
        default void onAddError(Exception exception) {}
        default void onRemoveSuccess() {}
        default void onRemoveError(Exception exception) {}
    }

    public static final Topic<ScriptResourceChangeListener> TOPIC_SCRIPT_RESOURCE =
            new Topic<>("Script Resource Change", ScriptResourceChangeListener.class);

}
