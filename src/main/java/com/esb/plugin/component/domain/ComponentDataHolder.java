package com.esb.plugin.component.domain;

public interface ComponentDataHolder {

    <T> T get(String key);

    void set(String propertyName, Object propertyValue);

}
