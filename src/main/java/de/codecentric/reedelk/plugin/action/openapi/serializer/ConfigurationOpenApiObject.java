package de.codecentric.reedelk.plugin.action.openapi.serializer;

import de.codecentric.reedelk.openapi.OpenApiModel;
import de.codecentric.reedelk.openapi.v3.model.ComponentsObject;
import de.codecentric.reedelk.openapi.v3.model.InfoObject;
import de.codecentric.reedelk.openapi.v3.model.ServerObject;

import java.util.List;

// Object encapsulating data structure for REST Listener Configuration.
public class ConfigurationOpenApiObject implements OpenApiModel {

    private InfoObject info;
    private List<ServerObject> servers;
    private ComponentsObject components;

    public ConfigurationOpenApiObject(InfoObject info, List<ServerObject> servers, ComponentsObject components) {
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
