package com.reedelk.plugin.service.application.impl;

import com.reedelk.plugin.service.application.HttpResponse;
import com.reedelk.plugin.service.application.HttpService;
import okhttp3.*;

import java.io.IOException;

public class HttpServiceImpl implements HttpService {

    private OkHttpClient client;

    public HttpServiceImpl() {
        this.client = new OkHttpClient();
    }

    @Override
    public HttpResponse post(String url, String payload, MediaType mediaType) throws IOException {
        RequestBody body = RequestBody.create(mediaType, payload);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return make(request);
    }

    @Override
    public HttpResponse get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        return make(request);
    }

    @Override
    public HttpResponse delete(String url, String payload, MediaType mediaType) throws IOException {
        RequestBody body = RequestBody.create(mediaType, payload);
        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .build();
        return make(request);
    }

    private HttpResponse make(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            HttpResponse httpResponse = new HttpResponse();
            httpResponse.setBody(response.body() != null ? response.body().string() : null);
            httpResponse.setStatus(response.code());
            return httpResponse;
        }
    }
}
