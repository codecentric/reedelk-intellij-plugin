package com.reedelk.plugin.service.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.reedelk.plugin.service.module.impl.HttpResponse;
import okhttp3.MediaType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


public interface HttpService {

    MediaType JSON = MediaType.get("application/json; charset=utf-8");

    static HttpService getInstance(@NotNull Module module) {
        return ModuleServiceManager.getService(module, HttpService.class);
    }

    HttpResponse post(String url, String payload, MediaType mediaType) throws IOException;

    HttpResponse get(String url) throws IOException;

    HttpResponse delete(String url, String payload, MediaType mediaType) throws IOException;
}
