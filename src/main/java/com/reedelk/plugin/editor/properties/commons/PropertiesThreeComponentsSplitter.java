package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.module.Module;
import com.intellij.ui.JBColor;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.Sizes;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.FlowSnapshot;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import static com.intellij.util.ui.JBUI.Borders.customLine;

public class PropertiesThreeComponentsSplitter extends DisposableThreeComponentsSplitter {

    private static final boolean HORIZONTAL = false;
    private ContainerContext context;

    public PropertiesThreeComponentsSplitter(@NotNull Module module,
                                             @NotNull ContainerContext context,
                                             @NotNull String componentFullyQualifiedName,
                                             @NotNull FlowSnapshot snapshot,
                                             @NotNull JComponent properties) {
        super(HORIZONTAL);
        this.context = context;

        Border outside = customLine(JBColor.WHITE, 0, 0, 0, 0);
        Border inside = customLine(JBColor.LIGHT_GRAY, 0, 0, 0, 1);
        Border border = new CompoundBorder(outside, inside);
        properties.setBorder(border);

        PropertiesPanelIOContainer componentIO = new PropertiesPanelIOContainer(module, snapshot, componentFullyQualifiedName);

        setLastSize(Sizes.PropertiesPane.COMPONENT_OUTPUT_INFO_WIDTH);
        setInnerComponent(properties);
        setLastComponent(componentIO);
        setDividerWidth(0);
        setDividerMouseZoneSize(10);
    }

    @Override
    public void dispose() {
        super.dispose();
        DisposableUtils.dispose(context);
        this.context = null;
    }
}
