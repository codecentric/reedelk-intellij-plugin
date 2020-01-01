package com.reedelk.plugin.editor.palette;

import com.reedelk.module.descriptor.model.ComponentDescriptor;
import com.reedelk.plugin.service.module.impl.component.ModuleComponents;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PalettePanelTest {

    @Mock
    private ComponentDescriptor mockDescriptor1;
    @Mock
    private ComponentDescriptor mockDescriptor2;
    @Mock
    private ComponentDescriptor mockDescriptor3;

    @Nested
    @DisplayName("Build package tree node tests")
    class BuildPackageTreeNode {

        @Test
        void shouldCorrectlyBuildPackageTreeNode() {
            // Given
            ModuleComponents module1 = new ModuleComponents("Module1", asList(mockDescriptor1, mockDescriptor2));

            // When
            DefaultMutableTreeNode treeNode = PalettePanel.asTreeNode(module1);

            // Then
            assertThat(treeNode.getUserObject()).isEqualTo("Module1");

            List<DefaultMutableTreeNode> children = toList(treeNode.children());
            assertThat(children).hasSize(2);

            assertThatExistsChildrenWithUserObject(children, mockDescriptor1);
            assertThatExistsChildrenWithUserObject(children, mockDescriptor2);
        }

        @Test
        void shouldCorrectlyBuildPackageTreeNodeExcludingHiddenComponents() {
            // Given
            doReturn(true)
                    .when(mockDescriptor2)
                    .isHidden();
            ModuleComponents module1 = new ModuleComponents("Module1", asList(mockDescriptor1, mockDescriptor2));

            // When
            DefaultMutableTreeNode treeNode = PalettePanel.asTreeNode(module1);

            // Then
            List<DefaultMutableTreeNode> children = toList(treeNode.children());

            assertThat(children).hasSize(1);
            assertThatExistsChildrenWithUserObject(children, mockDescriptor1);
        }
    }

    @Nested
    @DisplayName("Get components packages tree nodes tests")
    class GetModuleComponentsTreeNodes {

        @Test
        void shouldCorrectlyReturnCorrectComponentsPackagesTreeNodes() {
            // Given
            ModuleComponents module1 = new ModuleComponents("Module1", asList(mockDescriptor1, mockDescriptor2));
            ModuleComponents module2 = new ModuleComponents("Module2", Collections.singletonList(mockDescriptor3));

            // When
            List<DefaultMutableTreeNode> treeNodes =
                    PalettePanel.asTreeNodes(asList(module1, module2));

            // Then
            assertThat(treeNodes).hasSize(2);

            assertExistsTreeNodeWithNameAndChildrenUserObjects(treeNodes, "Module1", mockDescriptor1, mockDescriptor2);
            assertExistsTreeNodeWithNameAndChildrenUserObjects(treeNodes, "Module2", mockDescriptor3);
        }

        @Test
        void shouldExcludeComponentsPackagesWithNoComponents() {
            // Given
            ModuleComponents module1 = new ModuleComponents("Module1", asList(mockDescriptor1, mockDescriptor2));
            ModuleComponents module2 = new ModuleComponents("Module2", Collections.emptyList());

            // When
            List<DefaultMutableTreeNode> treeNodes =
                    PalettePanel.asTreeNodes(asList(module1, module2));

            // Then
            assertThat(treeNodes).hasSize(1);
            assertExistsTreeNodeWithNameAndChildrenUserObjects(treeNodes, "Module1", mockDescriptor1, mockDescriptor2);
        }
    }

    private void assertThatExistsChildrenWithUserObject(List<DefaultMutableTreeNode> childrenTreeNodes, ComponentDescriptor expectedDescriptor) {
        boolean found = childrenTreeNodes
                .stream()
                .anyMatch(defaultMutableTreeNode -> defaultMutableTreeNode.getUserObject() == expectedDescriptor);
        assertThat(found).isTrue();
    }

    private Optional<DefaultMutableTreeNode> getTreeNodeHavingName(List<DefaultMutableTreeNode> treeNodes, String expectedName) {
        return treeNodes
                .stream()
                .filter(defaultMutableTreeNode -> defaultMutableTreeNode.getUserObject() == expectedName)
                .findFirst();
    }

    private void assertExistsTreeNodeWithNameAndChildrenUserObjects(List<DefaultMutableTreeNode> treeNodes, String expectedName, ComponentDescriptor... descriptors) {
        Optional<DefaultMutableTreeNode> treeNodeMatchingName = getTreeNodeHavingName(treeNodes, expectedName);
        assertThat(treeNodeMatchingName).isPresent();

        DefaultMutableTreeNode treeNode = treeNodeMatchingName.get();
        List<DefaultMutableTreeNode> treeNodeChildren = toList(treeNode.children());
        assertThat(treeNodeChildren).hasSize(descriptors.length);

        Arrays.stream(descriptors)
                .forEach(descriptor -> assertThatExistsChildrenWithUserObject(treeNodeChildren, descriptor));
    }

    private List<DefaultMutableTreeNode> toList(Enumeration enumeration) {
        List<DefaultMutableTreeNode> treeNodes = new ArrayList<>();
        while (enumeration.hasMoreElements()) {
            treeNodes.add((DefaultMutableTreeNode) enumeration.nextElement());
        }
        return treeNodes;
    }
}