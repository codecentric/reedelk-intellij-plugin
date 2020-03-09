package com.reedelk.plugin.service.module.impl.http;

import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class RestClientProvider {

    private static final int READ_TIMEOUT = 30;

    private RestClientProvider() {
    }

    private static class LazyProvider {

        private LazyProvider() {
        }

        private static final OkHttpClient INSTANCE = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    public static OkHttpClient getInstance() {
        return LazyProvider.INSTANCE;
    }
}
