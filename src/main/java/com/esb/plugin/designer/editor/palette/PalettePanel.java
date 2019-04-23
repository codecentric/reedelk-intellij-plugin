package com.esb.plugin.designer.editor.palette;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.commons.SystemComponents;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.editor.component.ComponentTransferHandler;
import com.esb.plugin.reflection.ImplementorScanner;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.List;

public class PalettePanel extends JBPanel {

    private Tree tree;

    public PalettePanel(Project project, VirtualFile file) {
        super(new BorderLayout());

        //create the root node
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

        fetchAllComponentsInClasspath(project, file, components);
    }

    private void fetchAllComponentsInClasspath(Project project, VirtualFile file, DefaultMutableTreeNode components) {
        new Thread(() -> {
            ImplementorScanner scanner = new ImplementorScanner();
            List<ComponentDescriptor> descriptors = scanner.listComponents(project, file);
            SwingUtilities.invokeLater(() -> {
                components.removeAllChildren();
                for (ComponentDescriptor descriptor : descriptors) {
                    components.add(new DefaultMutableTreeNode(descriptor));
                }
                expandRows(tree);
            });
        }).start();
    }


    private DefaultMutableTreeNode commons() {
        DefaultMutableTreeNode commonsNode = new DefaultMutableTreeNode("Commons");
        commonsNode.add(new DefaultMutableTreeNode(new Pair<>("Fork", SystemComponents.FORK.qualifiedName())));
        commonsNode.add(new DefaultMutableTreeNode(new Pair<>("Choice", SystemComponents.CHOICE.qualifiedName())));
        commonsNode.add(new DefaultMutableTreeNode(new Pair<>("Flow Reference", SystemComponents.FLOW_REFERENCE.qualifiedName())));
        return commonsNode;
    }

    private void expandRows(Tree tree) {
        int j = tree.getRowCount();
        int i = 0;
        while (i < j) {
            tree.expandRow(i);
            i += 1;
            j = tree.getRowCount();
        }
    }
}
