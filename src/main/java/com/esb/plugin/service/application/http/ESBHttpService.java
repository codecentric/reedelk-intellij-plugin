package com.esb.plugin.service.application.http;

import com.intellij.openapi.components.ServiceManager;
import okhttp3.MediaType;

import java.io.IOException;


public interface ESBHttpService {

    MediaType JSON = MediaType.get("application/json; charset=utf-8");

    static ESBHttpService getInstance() {
        return ServiceManager.getService(ESBHttpService.class);
    }

    String post(String url, String payload, MediaType mediaType) throws IOException;

    String get(String url) throws IOException;

    String delete(String url, String payload, MediaType mediaType) throws IOException;
}
