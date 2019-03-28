package com.esb.plugin.designer.editor.palette;

import com.esb.plugin.designer.editor.component.ComponentTransferHandler;
import com.esb.plugin.utils.ESBIcons;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class PalettePanel extends JBPanel {

    private Tree tree;

    public PalettePanel() {
        super(new BorderLayout());

        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

        //add the child nodes to the root node
        root.add(commons());
        root.add(rest());
        root.add(jms());

        PaletteTreeCellRenderer renderer = new PaletteTreeCellRenderer();
        renderer.setOpenIcon(ESBIcons.ModuleDeploy);
        renderer.setClosedIcon(ESBIcons.ModuleUnDeploy);


        tree = new Tree(root);
        tree.setCellRenderer(renderer);
        tree.setRootVisible(false);
        tree.setDragEnabled(true);
        tree.setTransferHandler(new ComponentTransferHandler());

        JBScrollPane componentsTreeScrollPanel = new JBScrollPane(tree);

        add(componentsTreeScrollPanel, BorderLayout.CENTER);

    }

    private DefaultMutableTreeNode rest() {
        DefaultMutableTreeNode restNode = new DefaultMutableTreeNode("REST");
        restNode.add(new DefaultMutableTreeNode(new Pair<>("REST Listener", "default")));
        restNode.add(new DefaultMutableTreeNode(new Pair<>("REST Request", "default")));
        return restNode;
    }

    private DefaultMutableTreeNode commons() {
        DefaultMutableTreeNode commonsNode = new DefaultMutableTreeNode("Commons");
        commonsNode.add(new DefaultMutableTreeNode(new Pair<>("Set Payload", "com.esb.core.component.SetPayload")));
        commonsNode.add(new DefaultMutableTreeNode(new Pair<>("Fork", "com.esb.component.Fork")));
        commonsNode.add(new DefaultMutableTreeNode(new Pair<>("Choice", "com.esb.component.Choice")));
        commonsNode.add(new DefaultMutableTreeNode(new Pair<>("Flow", "com.esb.component.FlowReference")));
        return commonsNode;
    }

    private DefaultMutableTreeNode jms() {
        DefaultMutableTreeNode jmsNode = new DefaultMutableTreeNode("JMS");
        jmsNode.add(new DefaultMutableTreeNode(new Pair<>("Queue Listener", "default")));
        jmsNode.add(new DefaultMutableTreeNode(new Pair<>("Queue Message", "default")));
        return jmsNode;
    }

}
