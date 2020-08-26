package com.reedelk.plugin.action.openapi.template;

import com.reedelk.plugin.template.RESTListenerConfigProperties;

public class OpenApiRESTListenerConfigProperties extends RESTListenerConfigProperties {

    public void setBasePath(String basePath) {
        setProperty("basePath", basePath);
    }

    public void setOpenApiObject(String openApiObject) {
        setProperty("openApiObject", openApiObject);
    }
}
