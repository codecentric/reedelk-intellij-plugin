package com.esb.plugin.designer.palette;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.ComponentTransferableHandler;
import com.esb.plugin.service.module.ComponentService;
import com.esb.plugin.service.module.impl.ComponentListUpdateNotifier;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBusConnection;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.Set;

public class PalettePanel extends JBPanel implements ComponentListUpdateNotifier {

    private final Tree tree;
    private final DefaultMutableTreeNode root;
    private final Module module;
    private final DefaultMutableTreeNode componentsTreeNode;

    public PalettePanel(Module module) {
        super(new BorderLayout());

        this.module = module;

        root = new DefaultMutableTreeNode("Root");
        componentsTreeNode = new DefaultMutableTreeNode("Components");
        root.add(componentsTreeNode);

        PaletteTreeCellRenderer renderer = new PaletteTreeCellRenderer();
        renderer.setOpenIcon(Icons.ModuleDeploy);
        renderer.setClosedIcon(Icons.ModuleUnDeploy);

        tree = new Tree(root);
        tree.setCellRenderer(renderer);
        tree.setRootVisible(false);
        tree.setDragEnabled(true);
        tree.setTransferHandler(new ComponentTransferableHandler());


        JBScrollPane componentsTreeScrollPanel = new JBScrollPane(tree);

        add(componentsTreeScrollPanel, BorderLayout.CENTER);

        MessageBusConnection connect = module.getMessageBus().connect();
        connect.subscribe(ComponentListUpdateNotifier.TOPIC, this);

        updatePaletteComponentsList();
    }

    @Override
    public void onComponentListUpdate() {
        updatePaletteComponentsList();
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

    private void updatePaletteComponentsList() {
        Set<ComponentDescriptor> descriptors = ComponentService.getInstance(module).listComponents();
        SwingUtilities.invokeLater(() -> {
            componentsTreeNode.removeAllChildren();
            descriptors.forEach(descriptor -> componentsTreeNode.add(new DefaultMutableTreeNode(descriptor)));
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            model.reload();
            expandRows();
        });
    }

}
