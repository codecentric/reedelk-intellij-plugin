package com.reedelk.plugin.service.module.impl.component.completion;

public interface OnComponentIO {

    void onComponentIO(String inputFQCN, String outputFQCN, ComponentIO componentIO);

    void onComponentIONotFound(String inputFQCN, String outputFQCN);
}
