package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.OpenApiModel;
import com.reedelk.openapi.v3.model.ComponentsObject;
import com.reedelk.openapi.v3.model.InfoObject;
import com.reedelk.openapi.v3.model.ServerObject;

import java.util.List;

// Object encapsulating data structure for REST Listener Configuration.
public class ConfigOpenApiObject implements OpenApiModel {

    private InfoObject info;
    private List<ServerObject> servers;
    private ComponentsObject components;

    public ConfigOpenApiObject(InfoObject info, List<ServerObject> servers, ComponentsObject components) {
        this.info = info;
        this.servers = servers;
        this.components = components;
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
