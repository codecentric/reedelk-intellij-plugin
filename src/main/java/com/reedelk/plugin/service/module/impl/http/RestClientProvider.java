package com.reedelk.plugin.service.module.impl.http;

import okhttp3.OkHttpClient;

public class RestClientProvider {

    private RestClientProvider() {
    }

    private static class LazyProvider {

        private LazyProvider() {
        }

        private static final OkHttpClient INSTANCE = new OkHttpClient();
    }

    public static OkHttpClient getInstance() {
        return LazyProvider.INSTANCE;
    }
}
