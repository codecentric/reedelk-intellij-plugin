package com.esb.plugin.editor.palette;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.component.domain.ComponentDescriptor;
import com.esb.plugin.component.domain.ComponentsPackage;
import com.esb.plugin.component.scanner.ComponentListUpdateNotifier;
import com.esb.plugin.editor.DesignerVisibleNotifier;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static java.awt.BorderLayout.CENTER;
import static java.util.stream.Collectors.toList;

public class PalettePanel extends JBPanel implements DesignerVisibleNotifier, ComponentListUpdateNotifier {

    private final String rootTreeNodeName = "root";

    private final Project project;
    private final Tree tree;
    private DefaultMutableTreeNode root;

    public PalettePanel(Project project) {
        super(new BorderLayout());

        this.project = project;
        MessageBusConnection busConnection = project.getMessageBus().connect();
        busConnection.subscribe(DESIGNER_VISIBLE, this);


        this.root = new DefaultMutableTreeNode(rootTreeNodeName);
        TreeModel model = new DefaultTreeModel(this.root);

        PaletteTreeCellRenderer renderer = new PaletteTreeCellRenderer();
        renderer.setOpenIcon(Icons.Module);
        renderer.setClosedIcon(Icons.Module);

        this.tree = new SimpleTree(model);
        this.tree.setCellRenderer(renderer);
        this.tree.setRootVisible(false);
        this.tree.setDragEnabled(true);
        this.tree.setTransferHandler(new ComponentTransferableHandler());


        JScrollPane componentsTreeScrollPanel = new JBScrollPane(tree);
        componentsTreeScrollPanel.setBorder(BorderFactory.createEmptyBorder());

        add(componentsTreeScrollPanel, CENTER);

        registerComponentListUpdateNotifier();
    }

    @Override
    public void onComponentListUpdate(Module module) {
        updateComponents(module);
    }

    @Override
    public void onDesignerVisible(VirtualFile virtualFile) {
        Module module = ModuleUtil.findModuleForFile(virtualFile, project);
        updateComponents(module);
    }

    @NotNull
    static List<DefaultMutableTreeNode> getComponentsPackagesTreeNodes(Collection<ComponentsPackage> componentsPackages) {
        return componentsPackages
                .stream()
                .filter(ExcludeModuleWithoutComponents)
                .map(PalettePanel::buildPackageTreeNode)
                .collect(toList());
    }

    @NotNull
    static DefaultMutableTreeNode buildPackageTreeNode(ComponentsPackage componentsPackage) {
        DefaultMutableTreeNode componentTreeNode = new DefaultMutableTreeNode(componentsPackage.getName());
        componentsPackage
                .getModuleComponents()
                .stream()
                .filter(ExcludeHiddenComponent)
                .forEach(componentDescriptor ->
                        componentTreeNode.add(new DefaultMutableTreeNode(componentDescriptor)));
        return componentTreeNode;
    }

    void registerComponentListUpdateNotifier() {
        MessageBusConnection connect = project.getMessageBus().connect();
        connect.subscribe(ComponentListUpdateNotifier.COMPONENT_LIST_UPDATE_TOPIC, this);
    }

    private void updateComponents(Module module) {
        Collection<ComponentsPackage> componentsPackages = ComponentService.getInstance(module).getModulesDescriptors();
        List<DefaultMutableTreeNode> componentsTreeNodes = getComponentsPackagesTreeNodes(componentsPackages);
        SwingUtilities.invokeLater(() -> {
            root.removeAllChildren();
            componentsTreeNodes.forEach(componentTreeNode -> root.add(componentTreeNode));
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            model.reload();
            expandRows(tree);
        });
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
