package com.esb.plugin.designer.editor;

import com.esb.plugin.designer.editor.component.ComponentTransferHandler;
import com.esb.plugin.utils.ESBIcons;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class PalettePanel extends JBPanel {

    private Tree tree;

    public PalettePanel() {
        super(new BorderLayout());


        //create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        //create the child nodes
        DefaultMutableTreeNode commonsNode = new DefaultMutableTreeNode("Commons");
        commonsNode.add(new DefaultMutableTreeNode("Set Payload"));
        commonsNode.add(new DefaultMutableTreeNode("Fork"));
        commonsNode.add(new DefaultMutableTreeNode("Choice"));
        commonsNode.add(new DefaultMutableTreeNode("Join Text"));

        DefaultMutableTreeNode restNode = new DefaultMutableTreeNode("REST");
        restNode.add(new DefaultMutableTreeNode("REST Listener"));
        restNode.add(new DefaultMutableTreeNode("REST Request"));

        DefaultMutableTreeNode jmsNode = new DefaultMutableTreeNode("JMS");
        jmsNode.add(new DefaultMutableTreeNode("Queue Listener"));
        jmsNode.add(new DefaultMutableTreeNode("Queue Message"));
        //add the child nodes to the root node
        root.add(commonsNode);
        root.add(restNode);
        root.add(jmsNode);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(ESBIcons.FileTypeFlow);
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
}
