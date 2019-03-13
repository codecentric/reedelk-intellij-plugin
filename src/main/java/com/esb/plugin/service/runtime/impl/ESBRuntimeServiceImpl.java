package com.esb.plugin.service.runtime.impl;

import com.esb.plugin.service.runtime.ESBRuntime;
import com.esb.plugin.service.runtime.ESBRuntimeService;
import com.esb.plugin.service.runtime.ESBRuntimes;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@State(name = "ESBRuntimesService", storages = @Storage("esb-runtimes.xml"))
public class ESBRuntimeServiceImpl implements ESBRuntimeService, PersistentStateComponent<ESBRuntimes>, BulkFileListener {

    private final List<ESBRuntime> runtimes = new ArrayList<>();
    private final MessageBusConnection connection;

    public ESBRuntimeServiceImpl() {
        this.connection = ApplicationManager.getApplication().getMessageBus().connect();
        connection.subscribe(VirtualFileManager.VFS_CHANGES, this);
    }

    @Override
    public Collection<ESBRuntime> listRuntimes() {
        return Collections.unmodifiableCollection(runtimes);
    }

    @Override
    public void addRuntime(ESBRuntime runtime) {
        this.runtimes.add(runtime);
    }

    @Override
    public boolean contains(ESBRuntime runtime) {
        return runtimes.stream()
                .anyMatch(item -> item.name.equals(runtime.name));
    }

    @Nullable
    @Override
    public ESBRuntimes getState() {
        ESBRuntimes runtimes = new ESBRuntimes();
        runtimes.runtimes.addAll(this.runtimes);
        return runtimes;
    }

    @Override
    public void loadState(@NotNull ESBRuntimes state) {
        this.runtimes.clear();
        this.runtimes.addAll(state.runtimes);
    }

    public void before(@NotNull List<? extends VFileEvent> events) {
        System.out.println("After");
    }

    public void after(@NotNull List<? extends VFileEvent> events) {
        System.out.println("Af");
    }
}
