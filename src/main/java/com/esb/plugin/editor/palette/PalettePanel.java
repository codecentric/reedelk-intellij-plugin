package com.esb.plugin.editor.palette;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.component.domain.ComponentsPackage;
import com.esb.plugin.component.scanner.ComponentListUpdateNotifier;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBusConnection;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.Collection;

public class PalettePanel extends JBPanel implements ComponentListUpdateNotifier {

    private final Tree tree;
    private final Module module;
    private DefaultMutableTreeNode root;

    public PalettePanel(Module module) {
        super(new BorderLayout());

        this.module = module;

        root = new DefaultMutableTreeNode("Root");
        DefaultMutableTreeNode componentsTreeNode = new DefaultMutableTreeNode("Components");
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
        connect.subscribe(ComponentListUpdateNotifier.COMPONENT_LIST_UPDATE_TOPIC, this);

        updatePaletteComponentsList();
    }

    @Override
    public void onComponentListUpdate() {
        updatePaletteComponentsList();
    }

    private void updatePaletteComponentsList() {
        Collection<ComponentsPackage> descriptors = ComponentService.getInstance(module).getModulesDescriptors();
        SwingUtilities.invokeLater(() -> {
            root.removeAllChildren();
            descriptors.forEach(descriptor -> {
                EsbModuleTreeNode treeNode = new EsbModuleTreeNode(descriptor);
                treeNode.buildChildren();
                root.add(treeNode);
            });
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            model.reload();
            expandRows();
        });
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


    class EsbModuleTreeNode extends DefaultMutableTreeNode {

        private final ComponentsPackage descriptor;

        EsbModuleTreeNode(ComponentsPackage descriptor) {
            this.userObject = descriptor.getName();
            this.descriptor = descriptor;
        }

        void buildChildren() {
            descriptor
                    .getModuleComponents()
                    .stream()
                    .filter(componentDescriptor -> !componentDescriptor.isHidden())
                    .forEach(descriptor -> add(new DefaultMutableTreeNode(descriptor)));
        }
    }


}
