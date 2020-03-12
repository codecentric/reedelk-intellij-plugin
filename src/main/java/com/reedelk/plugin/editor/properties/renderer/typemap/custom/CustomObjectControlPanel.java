package com.reedelk.plugin.editor.properties.renderer.typemap.custom;

import com.reedelk.plugin.editor.properties.commons.ComboActionsPanel;

public class CustomObjectControlPanel extends ComboActionsPanel<CustomObjectMetadata> {

    public CustomObjectControlPanel() {
        super();
    }

    @Override
    protected void onSelect(CustomObjectMetadata selectedItem) {

    }

    @Override
    protected void onAdd(CustomObjectMetadata item) {
        // Add data to the children.
    }

    @Override
    protected void onEdit(CustomObjectMetadata item) {
        // Edit from to the children.
    }

    @Override
    protected void onDelete(CustomObjectMetadata item) {
        // Remove data from the children.
    }
}
