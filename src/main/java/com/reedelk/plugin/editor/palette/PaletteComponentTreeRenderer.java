package com.reedelk.plugin.editor.palette;

import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.util.Optional;

public class PaletteComponentTreeRenderer implements TreeCellRenderer {

    private JPanel renderer;
    private JLabel value;

    private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

    PaletteComponentTreeRenderer() {
        this.value = new JLabel();
        this.value.setAlignmentY(JLabel.CENTER_ALIGNMENT);

        this.renderer = new JPanel();
        this.renderer.setLayout(new BorderLayout());

        DisposablePanel valueContainerLeftAligned = ContainerFactory.pushLeft(value);
        valueContainerLeftAligned.setBackground(renderer.getBackground());
        valueContainerLeftAligned.setOpaque(false);

        this.renderer.add(valueContainerLeftAligned, BorderLayout.WEST);

        this.defaultRenderer.setOpenIcon(Icons.Module);
        this.defaultRenderer.setClosedIcon(Icons.Module);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof DefaultMutableTreeNode) {

            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

            if (userObject instanceof ComponentDescriptor) {
                ComponentDescriptor descriptor = (ComponentDescriptor) userObject;

                this.value.setText(descriptor.getDisplayName());

                Icon componentIcon = Optional.ofNullable(descriptor.getIcon()).orElse(Icons.Component.Default);
                this.value.setIcon(componentIcon);
                this.value.setForeground(selected ?
                        Colors.PALETTE_TEXT_SELECTED :
                        Colors.PALETTE_TEXT_UNSELECTED);
                return renderer;
            }
        }

        return defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }
}
