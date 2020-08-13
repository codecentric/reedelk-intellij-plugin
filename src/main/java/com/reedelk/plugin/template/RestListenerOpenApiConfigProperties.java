package com.reedelk.plugin.template;

public class RestListenerOpenApiConfigProperties extends RestListenerConfigProperties {

    public RestListenerOpenApiConfigProperties(String id, String title, String host, int port, String openApiObject, String basePath) {
        super(id, title, host, port);
        put("basePath", basePath);
        put("openApiObject", openApiObject);
    }
}
