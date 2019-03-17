package com.esb.plugin.service.application.http.impl;

import com.esb.plugin.service.application.http.ESBHttpService;
import com.esb.plugin.service.application.http.HttpResponse;
import okhttp3.*;

import java.io.IOException;

public class ESBHttpServiceImpl implements ESBHttpService {

    private OkHttpClient client;

    public ESBHttpServiceImpl() {
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
