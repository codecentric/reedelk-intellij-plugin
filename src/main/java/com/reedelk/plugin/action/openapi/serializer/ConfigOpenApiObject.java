package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.OpenApiModel;
import com.reedelk.openapi.v3.model.ComponentsObject;
import com.reedelk.openapi.v3.model.InfoObject;
import com.reedelk.openapi.v3.model.OpenApiObject;
import com.reedelk.openapi.v3.model.ServerObject;

import java.util.List;

// The rest listener config
public class ConfigOpenApiObject implements OpenApiModel {

    private InfoObject info;
    private List<ServerObject> servers;
    private ComponentsObject components;

    public ConfigOpenApiObject(OpenApiObject object) {
        info = object.getInfo();
        servers = object.getServers();
        components = object.getComponents();
    }

    public InfoObject getInfo() {
        return info;
    }

    public List<ServerObject> getServers() {
        return servers;
    }

    public ComponentsObject getComponents() {
        return components;
    }
}
