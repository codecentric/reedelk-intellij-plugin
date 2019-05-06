package com.esb.plugin.designer.properties;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.designer.properties.widget.PropertyBox;
import com.esb.plugin.graph.GraphSnapshot;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPropertyRenderer implements PropertyRenderer {

    protected final GraphSnapshot snapshot;

    public AbstractPropertyRenderer(GraphSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    @NotNull
    protected PropertyBox createPropertyBox(ComponentData componentData, String propertyName) {
        PropertyBox propertyBox = new PropertyBox(propertyName);
        propertyBox.setText((String) componentData.get(propertyName.toLowerCase()));
        propertyBox.addListener(newText -> {
            componentData.set(propertyName.toLowerCase(), newText);
            snapshot.onDataChange();
        });
        return propertyBox;
    }

}
