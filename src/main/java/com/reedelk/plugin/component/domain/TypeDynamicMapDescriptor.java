package com.reedelk.plugin.component.domain;

import com.reedelk.runtime.api.script.DynamicMap;

public class TypeDynamicMapDescriptor extends TypeMapDescriptor {

    public TypeDynamicMapDescriptor(String tabGroup) {
        super(tabGroup);
    }

    @Override
    public Class<?> type() {
        return DynamicMap.class;
    }

}
