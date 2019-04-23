package com.esb.plugin.designer.editor.palette;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.commons.SystemComponents;
import com.esb.plugin.designer.editor.component.ComponentTransferHandler;
import com.esb.plugin.reflection.ImplementorScanner;
import com.esb.plugin.reflection.ImplementorScanner.ComponentDescriptor;
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


        //add the child nodes to the root node
        //root.add(commons());
        //root.add(rest());
        //root.add(jms());

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
                    Pair<String, String> pair = new Pair<>(descriptor.getComponentDisplayName(), descriptor.getComponentFullyQualifiedName());
                    components.add(new DefaultMutableTreeNode(pair));
                }
                expandRows(tree);
            });
        }).start();
    }

    // TODO: These components are only temporary. They will be dinamically loaded
    // TODO: by the framework.
    // The following tree nodes should be dynamically built.
    private DefaultMutableTreeNode rest() {
        DefaultMutableTreeNode restNode = new DefaultMutableTreeNode("REST");
        restNode.add(new DefaultMutableTreeNode(new Pair<>("REST Listener", "default")));
        restNode.add(new DefaultMutableTreeNode(new Pair<>("REST Request", "default")));
        return restNode;
    }

    private DefaultMutableTreeNode commons() {
        DefaultMutableTreeNode commonsNode = new DefaultMutableTreeNode("Commons");
        commonsNode.add(new DefaultMutableTreeNode(new Pair<>("Set Payload", "com.esb.core.component.SetPayload")));
        commonsNode.add(new DefaultMutableTreeNode(new Pair<>("Fork", SystemComponents.FORK.qualifiedName())));
        commonsNode.add(new DefaultMutableTreeNode(new Pair<>("Choice", SystemComponents.CHOICE.qualifiedName())));
        commonsNode.add(new DefaultMutableTreeNode(new Pair<>("Flow Reference", SystemComponents.FLOW_REFERENCE.qualifiedName())));
        return commonsNode;
    }

    private DefaultMutableTreeNode jms() {
        DefaultMutableTreeNode jmsNode = new DefaultMutableTreeNode("JMS");
        jmsNode.add(new DefaultMutableTreeNode(new Pair<>("Queue Listener", "default")));
        jmsNode.add(new DefaultMutableTreeNode(new Pair<>("Queue Message", "default")));
        return jmsNode;
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
