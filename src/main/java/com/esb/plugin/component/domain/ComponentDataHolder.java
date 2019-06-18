package com.esb.plugin.component.domain;

import java.util.List;

public interface ComponentDataHolder {

    List<String> keys();

    <T> T get(String key);

    void set(String propertyName, Object propertyValue);

}
