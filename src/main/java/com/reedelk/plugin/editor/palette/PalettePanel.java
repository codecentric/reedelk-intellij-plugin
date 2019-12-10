package com.reedelk.plugin.editor.palette;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.service.module.impl.component.ComponentsPackage;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentListUpdateNotifier;
import com.reedelk.plugin.topic.ReedelkTopics;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.awt.BorderLayout.CENTER;
import static java.util.stream.Collectors.toList;

public class PalettePanel extends JBPanel implements ComponentListUpdateNotifier, FileEditorManagerListener {

    private final transient Tree tree;
    private final transient Project project;
    private final transient DefaultMutableTreeNode root;

    PalettePanel(Project project) {
        super(new BorderLayout());

        this.project = project;

        this.root = new DefaultMutableTreeNode("root");
        TreeModel model = new DefaultTreeModel(this.root);

        PaletteComponentTreeRenderer renderer = new PaletteComponentTreeRenderer();

        this.tree = new SimpleTree(model);
        this.tree.setCellRenderer(renderer);
        this.tree.setRootVisible(false);
        this.tree.setDragEnabled(true);
        this.tree.setTransferHandler(new ComponentTransferableHandler());

        JScrollPane componentsTreeScrollPanel = new JBScrollPane(tree);
        componentsTreeScrollPanel.setBorder(BorderFactory.createEmptyBorder());
        add(componentsTreeScrollPanel, CENTER);

        MessageBusConnection connect = project.getMessageBus().connect();
        connect.subscribe(ReedelkTopics.COMPONENTS_UPDATE_EVENTS, this);
        connect.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this);

        updateComponentsForSelectedFile();
    }

    @Override
    public void onComponentListUpdate() {
        // We only update the components for this module if and only if
        // the current selected file belongs to the module for which the
        // components have been updated.
        VirtualFile[] selectedFiles = FileEditorManager.getInstance(project).getSelectedFiles();
        if (selectedFiles.length > 0) {
            ApplicationManager.getApplication().runReadAction(() -> {
                Module selectedFileModule = ModuleUtil.findModuleForFile(selectedFiles[0], project);
                updateComponents(selectedFileModule);
            });
        }
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        VirtualFile selectedFile = event.getNewFile();
        updateComponents(selectedFile);
    }

    private void updateComponents(VirtualFile file) {
        if (file != null) {
            Optional.ofNullable(ModuleUtil.findModuleForFile(file, project))
                    .ifPresent(this::updateComponents);
        }
    }

    private void updateComponents(Module module) {
        Collection<ComponentsPackage> componentsPackages = ComponentService.getInstance(module).getModulesDescriptors();
        List<DefaultMutableTreeNode> componentsTreeNodes = getComponentsPackagesTreeNodes(componentsPackages);

        SwingUtilities.invokeLater(() -> {

            root.removeAllChildren();

            componentsTreeNodes.forEach(root::add);

            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

            model.reload();

            expandRows(tree);
        });
    }

    private void updateComponentsForSelectedFile() {
        VirtualFile[] selectedFiles = FileEditorManager.getInstance(project).getSelectedFiles();
        if (selectedFiles.length > 0) {
            updateComponents(selectedFiles[0]);
        }
    }

    private static void expandRows(Tree tree) {
        int j = tree.getRowCount();
        int i = 0;
        while (i < j) {
            tree.expandRow(i);
            i += 1;
            j = tree.getRowCount();
        }
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

    private static final Predicate<ComponentDescriptor> ExcludeHiddenComponent =
            componentDescriptor -> !componentDescriptor.isHidden();

    // A module might not have any component if for instance all its components are hidden.
    // This is why in this method we filter all the component descriptors which are not hidden.
    private static final Predicate<ComponentsPackage> ExcludeModuleWithoutComponents =
            componentsPackage -> !componentsPackage.getModuleComponents()
                    .stream()
                    .filter(componentDescriptor -> !componentDescriptor.isHidden())
                    .collect(toList())
                    .isEmpty();

}
