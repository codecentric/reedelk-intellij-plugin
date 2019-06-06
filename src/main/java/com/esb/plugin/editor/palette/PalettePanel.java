package com.esb.plugin.editor.palette;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.component.domain.ComponentDescriptor;
import com.esb.plugin.component.domain.ComponentsPackage;
import com.esb.plugin.component.scanner.ComponentListUpdateNotifier;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static java.awt.BorderLayout.CENTER;
import static java.util.stream.Collectors.toList;

public class PalettePanel extends JBPanel implements ComponentListUpdateNotifier {

    private final String rootTreeNodeName = "root";

    private final Tree tree;
    private final Module module;
    private DefaultMutableTreeNode root;

    public PalettePanel(Module module) {
        super(new BorderLayout());

        this.module = module;
        this.root = new DefaultMutableTreeNode(rootTreeNodeName);

        PaletteTreeCellRenderer renderer = new PaletteTreeCellRenderer();
        renderer.setOpenIcon(Icons.ModuleDeploy);
        renderer.setClosedIcon(Icons.ModuleUnDeploy);

        this.tree = new Tree(root);
        this.tree.setCellRenderer(renderer);
        this.tree.setRootVisible(false);
        this.tree.setDragEnabled(true);
        this.tree.setTransferHandler(new ComponentTransferableHandler());

        JBScrollPane componentsTreeScrollPanel = new JBScrollPane(tree);

        add(componentsTreeScrollPanel, CENTER);

        registerComponentListUpdateNotifier();

        onComponentListUpdate();
    }

    @Override
    public void onComponentListUpdate() {
        List<MutableTreeNode> componentsTreeNodes = getComponentsPackagesTreeNodes();

        SwingUtilities.invokeLater(() -> {

            root.removeAllChildren();

            componentsTreeNodes.forEach(componentTreeNode -> root.add(componentTreeNode));

            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

            model.reload();

            expandRows(tree);
        });
    }

    @NotNull
    List<MutableTreeNode> getComponentsPackagesTreeNodes() {
        return getComponentsPackages()
                .stream()
                .filter(ExcludeModuleWithoutComponents)
                .map(this::buildPackageTreeNode)
                .collect(toList());
    }

    @NotNull
    DefaultMutableTreeNode buildPackageTreeNode(ComponentsPackage componentsPackage) {
        DefaultMutableTreeNode componentTreeNode = new DefaultMutableTreeNode(componentsPackage.getName());
        componentsPackage
                .getModuleComponents()
                .stream()
                .filter(ExcludeHiddenComponent)
                .forEach(componentDescriptor ->
                        componentTreeNode.add(new DefaultMutableTreeNode(componentDescriptor)));
        return componentTreeNode;
    }

    @NotNull
    Collection<ComponentsPackage> getComponentsPackages() {
        return ComponentService.getInstance(module).getModulesDescriptors();
    }

    void registerComponentListUpdateNotifier() {
        MessageBusConnection connect = module.getMessageBus().connect();
        connect.subscribe(ComponentListUpdateNotifier.COMPONENT_LIST_UPDATE_TOPIC, this);
    }

    private static final Predicate<ComponentDescriptor> ExcludeHiddenComponent =
            componentDescriptor -> !componentDescriptor.isHidden();

    private static final Predicate<ComponentsPackage> ExcludeModuleWithoutComponents =
            componentsPackage -> !componentsPackage.getModuleComponents().isEmpty();

    private static void expandRows(Tree tree) {
        int j = tree.getRowCount();
        int i = 0;
        while (i < j) {
            tree.expandRow(i);
            i += 1;
            j = tree.getRowCount();
        }
    }
}
