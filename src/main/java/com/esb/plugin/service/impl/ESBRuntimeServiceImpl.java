package com.esb.plugin.service.impl;

import com.esb.plugin.service.ESBRuntime;
import com.esb.plugin.service.ESBRuntimeService;
import com.esb.plugin.service.ESBRuntimes;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@State(name = "ESBRuntimesService", storages = @Storage("esb-runtimes.xml"))
public class ESBRuntimeServiceImpl implements ESBRuntimeService, PersistentStateComponent<ESBRuntimes> {

    @Override
    public Collection<ESBRuntime> listRuntimes() {
        List<ESBRuntime> runtimes = new ArrayList<>();

        ESBRuntime runtime1 = new ESBRuntime();
        runtime1.name = "Runtime1";
        runtime1.path = "/Users/lorenzo/runtime1";
        ESBRuntime runtime2 = new ESBRuntime();
        runtime2.name = "Runtime2";
        runtime2.path = "/Users/lorenzo/runtime2";

        runtimes.add(runtime1);
        runtimes.add(runtime2);
        return runtimes;
    }

    @Nullable
    @Override
    public ESBRuntimes getState() {
        return new ESBRuntimes();
    }

    @Override
    public void loadState(@NotNull ESBRuntimes state) {

    }
}
