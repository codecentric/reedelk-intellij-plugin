package com.reedelk.plugin.editor.palette;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.module.ModuleComponentDTO;
import com.reedelk.plugin.service.module.impl.component.module.ModuleDTO;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.awt.BorderLayout.CENTER;
import static java.util.stream.Collectors.toList;

public class PalettePanel extends JBPanel<PalettePanel> implements PlatformModuleService.ModuleChangeNotifier, FileEditorManagerListener {

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
        connect.subscribe(Topics.COMPONENTS_UPDATE_EVENTS, this);
        connect.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this);

        updateComponentsForSelectedFile();
    }

    @Override
    public void onModuleChange(Collection<ModuleDTO> modules) {
        // We only update the components for this module if and only if
        // the current selected file belongs to the module for which the
        // components have been updated.
        VirtualFile[] selectedFiles = FileEditorManager.getInstance(project).getSelectedFiles();
        if (selectedFiles.length > 0) {
            ApplicationManager.getApplication().runReadAction(() -> updateComponents(modules));
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
                    .ifPresent(module -> {
                        Collection<ModuleDTO> moduleDTOs = PlatformModuleService.getInstance(module).listModules();
                        updateComponents(moduleDTOs);
                    });
        }
    }

    private void updateComponents(Collection<ModuleDTO> modules) {
        List<DefaultMutableTreeNode> componentsTreeNodes = asTreeNodes(modules);

        ApplicationManager.getApplication().invokeLater(() -> {

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
    static List<DefaultMutableTreeNode> asTreeNodes(Collection<ModuleDTO> modules) {
        return modules.stream()
                .filter(ExcludeModuleWithoutComponents)
                .sorted(Comparator.comparing(ModuleDTO::getName))
                .map(PalettePanel::asTreeNode)
                .collect(toList());
    }

    @NotNull
    static DefaultMutableTreeNode asTreeNode(ModuleDTO moduleComponents) {
        DefaultMutableTreeNode componentTreeNode = new DefaultMutableTreeNode(moduleComponents.getName());
        moduleComponents.getComponents()
                .stream()
                .filter(ExcludeHiddenComponent)
                .sorted(Comparator.comparing(ModuleComponentDTO::getDisplayName))
                .forEach(componentDescriptor -> componentTreeNode.add(new DefaultMutableTreeNode(componentDescriptor)));
        return componentTreeNode;
    }

    private static final Predicate<ModuleComponentDTO> ExcludeHiddenComponent =
            componentDescriptor -> !componentDescriptor.isHidden();

    // A module might not have any component if for instance all its components are hidden.
    // This is why in this method we filter all the component descriptors which are not hidden.
    private static final Predicate<ModuleDTO> ExcludeModuleWithoutComponents =
            moduleComponents -> moduleComponents.getComponents()
                    .stream()
                    .anyMatch(componentDescriptor -> !componentDescriptor.isHidden());
}
