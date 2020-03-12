package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ComboActionsPanel;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.renderer.typemap.custom.CustomObjectControlPanel;
import com.reedelk.plugin.editor.properties.renderer.typemap.custom.CustomObjectMetadata;
import com.reedelk.plugin.editor.properties.renderer.typemap.custom.CustomObjectSelectorCombo;

import java.awt.*;

import static java.awt.BorderLayout.*;

public class MapPropertyWithCustomObjectContainer extends DisposablePanel {

    private final CustomObjectSelectorCombo comboSelector;
    private final ComboActionsPanel<CustomObjectMetadata> controlPanel;

    public MapPropertyWithCustomObjectContainer(PropertyAccessor propertyAccessor) {

        // The Config Selector Combo
        this.comboSelector = new CustomObjectSelectorCombo();
        this.controlPanel = new CustomObjectControlPanel();

        DisposablePanel disposablePanel = new DisposablePanel();
        disposablePanel.setLayout(new BorderLayout());
        disposablePanel.add(comboSelector, CENTER);
        disposablePanel.add(controlPanel, EAST);

        setLayout(new BorderLayout());
        add(disposablePanel, WEST);
    }
}
