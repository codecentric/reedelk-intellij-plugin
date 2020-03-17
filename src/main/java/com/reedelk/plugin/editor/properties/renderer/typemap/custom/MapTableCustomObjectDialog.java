package com.reedelk.plugin.editor.properties.renderer.typemap.custom;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.DisposableScrollPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class MapTableCustomObjectDialog extends DialogWrapper implements Disposable {

    private static final int MINIMUM_PANEL_WIDTH = 500;
    private static final int MINIMUM_PANEL_HEIGHT = 100;

    private final TypeObjectDescriptor typeObjectDescriptor;
    private final ComponentDataHolder componentDataHolder;
    private final Module module;
    private DisposableScrollPane panel;

    public MapTableCustomObjectDialog(Module module, String title, TypeObjectDescriptor typeObjectDescriptor, ComponentDataHolder value) {
        super(module.getProject(), true);
        setTitle(title);
        this.typeObjectDescriptor = typeObjectDescriptor;
        this.componentDataHolder = value;
        this.module = module;
        init();
    }

    @NotNull
    @Override
    protected Action getOKAction() {
        Action okAction = super.getOKAction();
        okAction.putValue(Action.NAME, "Done");
        return okAction;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        DisposablePanel propertiesPanel = new MapTableCustomObjectPanel(module, componentDataHolder, typeObjectDescriptor);
        this.panel = ContainerFactory.makeItScrollable(propertiesPanel);
        this.panel.setMinimumSize(new Dimension(MINIMUM_PANEL_WIDTH, MINIMUM_PANEL_HEIGHT));
        this.setCrossClosesWindow(true);
        return panel;
    }

    @Override
    public void dispose() {
        super.dispose();
        DisposableUtils.dispose(panel);
    }
}
