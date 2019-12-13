package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.reedelk.plugin.service.module.impl.http.HttpResponse;
import okhttp3.MediaType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;


public interface HttpService {

    static HttpService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, HttpService.class);
    }

    HttpResponse post(String url, String payload, MediaType mediaType) throws IOException;

    HttpResponse postMultipart(String url, File file, String partName) throws IOException;

    HttpResponse get(String url) throws IOException;

    HttpResponse delete(String url, String payload, MediaType mediaType) throws IOException;
}
