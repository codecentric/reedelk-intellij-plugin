package de.codecentric.reedelk.plugin.service.module.impl.runtimeapi;

import de.codecentric.reedelk.plugin.commons.DefaultConstants;
import de.codecentric.reedelk.plugin.service.module.impl.http.RestClientProvider;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.function.Consumer;

public class RuntimeApiServiceWaitRuntime implements Runnable {

    private final int port;
    private final String address;
    private final Consumer<Void> onRuntimeStartedCallback;

    public RuntimeApiServiceWaitRuntime(int port, String address, Consumer<Void> onRuntimeStartedCallback) {
        this.port = port;
        this.address = address;
        this.onRuntimeStartedCallback = onRuntimeStartedCallback;
    }

    @Override
    public void run() {
        OkHttpClient instance = RestClientProvider.getInstance();
        Request request = new Request.Builder()
                .url(DefaultConstants.RestApi.apiURLOf(address, port, DefaultConstants.RestApi.HEALTH)).get()
                .build();
        int attempts = 0;
        while (attempts < 100) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            try (Response response = instance.newCall(request).execute()) {
                if (response.code() == 200) {
                    if (onRuntimeStartedCallback != null) {
                        onRuntimeStartedCallback.accept(null);
                    }
                    break;
                }
            } catch (Exception e) {
                // Nothing to do, the runtime is not started yet.
            } finally {
                attempts++;
            }
        }
    }
}
