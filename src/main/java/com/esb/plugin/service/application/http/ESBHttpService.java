package com.esb.plugin.service.application.http;

import com.intellij.openapi.components.ServiceManager;
import okhttp3.MediaType;

import java.io.IOException;


public interface ESBHttpService {

    MediaType JSON = MediaType.get("application/json; charset=utf-8");

    static ESBHttpService getInstance() {
        return ServiceManager.getService(ESBHttpService.class);
    }

    HttpResponse post(String url, String payload, MediaType mediaType) throws IOException;

    HttpResponse get(String url) throws IOException;

    HttpResponse delete(String url, String payload, MediaType mediaType) throws IOException;
}
