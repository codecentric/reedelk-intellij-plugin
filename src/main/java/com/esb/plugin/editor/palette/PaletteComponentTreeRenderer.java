package com.esb.plugin.editor.palette;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.component.domain.ComponentClass;
import com.esb.plugin.component.domain.ComponentDescriptor;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class PaletteComponentTreeRenderer implements TreeCellRenderer {

    private JPanel renderer;
    private JLabel value;
    private JLabel typeIcon;

    private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

    public PaletteComponentTreeRenderer() {
        this.value = new JLabel();
        this.value.setAlignmentY(JLabel.CENTER_ALIGNMENT);

        this.typeIcon = new JLabel();
        this.typeIcon.setAlignmentY(JLabel.CENTER_ALIGNMENT);
        this.typeIcon.setIcon(Icons.Module);
        this.typeIcon.setBorder(JBUI.Borders.empty(1, 0, 0, 3));

        this.renderer = new JPanel();
        this.renderer.setLayout(new GridBagLayout());
        this.renderer.add(typeIcon);
        this.renderer.add(value);

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
                this.value.setIcon(descriptor.getIcon());
                if (selected) {
                    this.value.setForeground(Color.WHITE);
                } else {
                    this.value.setForeground(Color.DARK_GRAY);
                }
                if (descriptor.getComponentClass().equals(ComponentClass.INBOUND)) {
                    this.typeIcon.setIcon(Icons.Component.InboundTypeComponent);
                } else {
                    this.typeIcon.setIcon(Icons.Component.ProcessorTypeComponent);
                }
                return renderer;
            }
        }
        return defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded,
                leaf, row, hasFocus);
    }
}
