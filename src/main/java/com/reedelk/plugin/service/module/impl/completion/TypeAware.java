package com.reedelk.plugin.service.module.impl.completion;

public interface TypeAware {

    boolean isGlobal();

    String getToken();

    String getType();

    String getReturnType();

}
