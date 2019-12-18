package com.reedelk.plugin.service.module.impl.http;

import okhttp3.OkHttpClient;

public class RestClientProvider {

    private static class LazyProvider {
        private static final OkHttpClient INSTANCE = new OkHttpClient();
    }

    public static OkHttpClient getInstance() {
        return LazyProvider.INSTANCE;
    }
}
