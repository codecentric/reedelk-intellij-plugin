package com.reedelk.plugin.service.module.impl.http;

import com.reedelk.plugin.service.module.HttpService;
import com.reedelk.runtime.api.message.content.MimeType;
import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class HttpServiceImpl implements HttpService {

    private static final MediaType MEDIA_TYPE_BINARY = MediaType.get(MimeType.APPLICATION_BINARY.toString());

    private OkHttpClient client;

    public HttpServiceImpl() {
        this.client = new OkHttpClient();
    }

    @Override
    public HttpResponse get(String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        return make(request);
    }

    @Override
    public HttpResponse post(String url, String payload, MediaType mediaType) throws IOException {
        RequestBody body = RequestBody.create(mediaType, payload);
        Request request = new Request.Builder().url(url).post(body).build();
        return make(request);
    }

    @Override
    public HttpResponse postMultipart(String url, File file, String partName) throws IOException {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(partName, file.getName(), RequestBody.create(MEDIA_TYPE_BINARY, file))
                .build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return make(request);
    }

    @Override
    public HttpResponse delete(String url, String payload, MediaType mediaType) throws IOException {
        RequestBody body = RequestBody.create(mediaType, payload);
        Request request = new Request.Builder().url(url).delete(body).build();
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