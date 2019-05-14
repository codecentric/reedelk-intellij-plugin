package com.esb.plugin.designer.palette;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.ComponentTransferHandler;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.Collection;
import java.util.Set;

public class PalettePanel extends JBPanel {

    private Tree tree;

    public PalettePanel(Module module) {
        super(new BorderLayout());

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        DefaultMutableTreeNode components = new DefaultMutableTreeNode("Components");
        root.add(components);

        PaletteTreeCellRenderer renderer = new PaletteTreeCellRenderer();
        renderer.setOpenIcon(Icons.ModuleDeploy);
        renderer.setClosedIcon(Icons.ModuleUnDeploy);

        tree = new Tree(root);
        tree.setCellRenderer(renderer);
        tree.setRootVisible(false);
        tree.setDragEnabled(true);
        tree.setTransferHandler(new ComponentTransferHandler());


        JBScrollPane componentsTreeScrollPanel = new JBScrollPane(tree);

        add(componentsTreeScrollPanel, BorderLayout.CENTER);

        Set<ComponentDescriptor> componentDescriptors = ComponentService.getInstance(module).listComponents();
        updatePaletteComponentsList(components, componentDescriptors);
    }

    private void expandRows() {
        int j = tree.getRowCount();
        int i = 0;
        while (i < j) {
            tree.expandRow(i);
            i += 1;
            j = tree.getRowCount();
        }
    }

    private void updatePaletteComponentsList(DefaultMutableTreeNode componentsTreeNode, Collection<ComponentDescriptor> descriptors) {
        SwingUtilities.invokeLater(() -> {
            componentsTreeNode.removeAllChildren();
            descriptors.forEach(descriptor -> componentsTreeNode.add(new DefaultMutableTreeNode(descriptor)));
            expandRows();
        });
    }
}
