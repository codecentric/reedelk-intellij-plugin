package com.esb.plugin.designer.properties;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.designer.properties.widget.FormBuilder;
import com.esb.plugin.designer.properties.widget.PropertyInput;
import com.esb.plugin.graph.GraphSnapshot;
import com.intellij.ui.components.JBPanel;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractPropertyRenderer implements PropertyRenderer {

    protected final GraphSnapshot snapshot;

    public AbstractPropertyRenderer(GraphSnapshot snapshot) {
        this.snapshot = snapshot;
    }


    protected void addPropertyField(ComponentData componentData, String propertyName, JBPanel parent) {
        PropertyInput input = new PropertyInput();
        input.setText((String) componentData.get(propertyName.toLowerCase()));
        input.addListener(newText -> {
            componentData.set(propertyName.toLowerCase(), newText);
            snapshot.onDataChange();
        });
        FormBuilder.get()
                .addLabel(StringUtils.capitalize(propertyName), parent)
                .addLastField(input, parent);
    }

}
