package com.esb.plugin.designer.editor.palette;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.editor.component.ComponentTransferHandler;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkState;

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

        fetchAllComponents(project, file, components);
    }

    private void fetchAllComponents(Project project, VirtualFile file, DefaultMutableTreeNode components) {
        Module module = ModuleUtil.findModuleForFile(file, project);
        checkState(module != null, "Module must not be null");
        ComponentService
                .getInstance(module)
                .asyncFindAllComponents(descriptors -> updatePaletteComponentsList(components, descriptors));
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

    private void updatePaletteComponentsList(DefaultMutableTreeNode components, Collection<ComponentDescriptor> descriptors) {
        SwingUtilities.invokeLater(() -> {
            components.removeAllChildren();
            descriptors.forEach(descriptor -> components.add(new DefaultMutableTreeNode(descriptor)));
            expandRows();
        });
    }
}
