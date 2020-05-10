package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.module.Module;
import com.intellij.ui.JBColor;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.Sizes;
import com.reedelk.plugin.editor.properties.componentio.ComponentInput;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;

import static com.intellij.util.ui.JBUI.Borders.customLine;

public class PropertiesThreeComponentsSplitter extends DisposableThreeComponentsSplitter {

    private static final boolean HORIZONTAL = false;
    private ContainerContext context;

    public PropertiesThreeComponentsSplitter(@NotNull Module module,
                                             @NotNull ContainerContext context,
                                             @NotNull String componentFullyQualifiedName,
                                             @NotNull JComponent properties) {
        super(HORIZONTAL);
        this.context = context;

        Border rightLine = customLine(JBColor.LIGHT_GRAY, 0, 0, 0, 1);
        properties.setBorder(rightLine);

        ComponentInput componentInputPanel = new ComponentInput(module, context, componentFullyQualifiedName);

        setLastSize(Sizes.PropertiesPane.COMPONENT_OUTPUT_INFO_WIDTH);
        setInnerComponent(properties);
        setLastComponent(componentInputPanel);
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
