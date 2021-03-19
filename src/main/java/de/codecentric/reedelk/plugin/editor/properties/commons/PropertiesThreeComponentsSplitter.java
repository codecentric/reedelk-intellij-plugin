package de.codecentric.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.ui.JBColor;
import de.codecentric.reedelk.plugin.commons.DisposableUtils;
import de.codecentric.reedelk.plugin.commons.Sizes;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.metadata.MetadataPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;

import static com.intellij.util.ui.JBUI.Borders.customLine;

public class PropertiesThreeComponentsSplitter extends DisposableThreeComponentsSplitter {

    private static final boolean HORIZONTAL = false;
    private transient ContainerContext context;

    public PropertiesThreeComponentsSplitter(@NotNull Module module,
                                             @NotNull ContainerContext context,
                                             @NotNull JComponent properties,
                                             @NotNull Disposable disposable) {
        super(HORIZONTAL, disposable);
        this.context = context;

        Border rightLine = customLine(JBColor.LIGHT_GRAY, 0, 0, 0, 1);
        properties.setBorder(rightLine);

        MetadataPanel metadataPanelPanel = new MetadataPanel(module, context);

        setLastSize(Sizes.PropertiesPane.COMPONENT_OUTPUT_INFO_WIDTH);
        setInnerComponent(properties);
        setLastComponent(metadataPanelPanel);
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
