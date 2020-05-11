package com.reedelk.plugin.service.module.impl.component.metadata;

public interface OnComponentMetadata {

    void onComponentMetadata(ComponentMetadata componentMetadata);

    void onComponentMetadataError(String message);

}
