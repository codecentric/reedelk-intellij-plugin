package com.esb.plugin.service.module.impl;

import com.esb.plugin.service.module.ESBModuleRuntimeService;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;
import org.jetbrains.annotations.NotNull;

public class ESBModuleRuntimeServiceImpl implements ESBModuleRuntimeService, VirtualFileListener {
    public ESBModuleRuntimeServiceImpl(Module project) {
        VirtualFileManager.getInstance().addVirtualFileListener(this);
    }

    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
        System.out.println("Changed");
    }
}
