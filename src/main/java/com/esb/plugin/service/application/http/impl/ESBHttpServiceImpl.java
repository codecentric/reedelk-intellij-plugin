package com.esb.plugin.service.application.http.impl;

import com.esb.plugin.service.application.http.ESBHttpService;
import okhttp3.*;

import java.io.IOException;

public class ESBHttpServiceImpl implements ESBHttpService {

    private OkHttpClient client;

    public ESBHttpServiceImpl() {
        this.client = new OkHttpClient();
    }

    @Override
    public String post(String url, String payload, MediaType mediaType) throws IOException {
        RequestBody body = RequestBody.create(mediaType, payload);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return make(request);
    }

    @Override
    public String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return make(request);
    }

    @Override
    public String delete(String url, String payload, MediaType mediaType) throws IOException {
        RequestBody body = RequestBody.create(mediaType, payload);
        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .build();
        return make(request);
    }

    private String make(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            return response.body() != null ? response.body().string() : null;
        }
    }
}
