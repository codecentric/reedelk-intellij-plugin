package com.esb.plugin.component.domain;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonObjectComponentHolderDecorator implements ComponentDataHolder {

    private final JSONObject wrapped;

    public JsonObjectComponentHolderDecorator(JSONObject wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<String> keys() {
        return new ArrayList<>(wrapped.keySet());
    }

    @Override
    public <T> T get(String key) {
        return (T) wrapped.get(key);
    }

    @Override
    public void set(String propertyName, Object propertyValue) {
        wrapped.put(propertyName, propertyValue);
    }

    @Override
    public boolean has(String key) {
        return wrapped.has(key);
    }
}
