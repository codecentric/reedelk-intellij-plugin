package com.reedelk.plugin.editor.palette;

import com.reedelk.plugin.service.module.impl.component.module.ModuleComponentDTO;
import com.reedelk.plugin.service.module.impl.component.module.ModuleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class PalettePanelTest {

    @Mock
    private ModuleComponentDTO mockDescriptor1;
    @Mock
    private ModuleComponentDTO mockDescriptor2;
    @Mock
    private ModuleComponentDTO mockDescriptor3;

    @BeforeEach
    void setUp() {
        lenient().doReturn("Mock Descriptor 1")
                .when(mockDescriptor1)
                .getDisplayName();

        lenient().doReturn("Mock Descriptor 2")
                .when(mockDescriptor2)
                .getDisplayName();

        lenient().doReturn("Mock Descriptor 3")
                .when(mockDescriptor3)
                .getDisplayName();
    }

    @Test
    void shouldCorrectlyBuildPackageTreeNode() {
        // Given
        ModuleDTO module1 = new ModuleDTO("Module1", asList(mockDescriptor1, mockDescriptor2));

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
        ModuleDTO module1 = new ModuleDTO("Module1", asList(mockDescriptor1, mockDescriptor2));

        // When
        DefaultMutableTreeNode treeNode = PalettePanel.asTreeNode(module1);

        // Then
        List<DefaultMutableTreeNode> children = toList(treeNode.children());

        assertThat(children).hasSize(1);
        assertThatExistsChildrenWithUserObject(children, mockDescriptor1);
    }

    @Test
    void shouldCorrectlyReturnCorrectComponentsPackagesTreeNodes() {
        // Given
        ModuleDTO module1 = new ModuleDTO("Module1", asList(mockDescriptor1, mockDescriptor2));
        ModuleDTO module2 = new ModuleDTO("Module2", singletonList(mockDescriptor3));

        // When
        List<DefaultMutableTreeNode> treeNodes = PalettePanel.asTreeNodes(asList(module1, module2));

        // Then
        assertThat(treeNodes).hasSize(2);

        assertExistsTreeNodeWithNameAndChildrenUserObjects(treeNodes, "Module1", mockDescriptor1, mockDescriptor2);
        assertExistsTreeNodeWithNameAndChildrenUserObjects(treeNodes, "Module2", mockDescriptor3);
    }

    @Test
    void shouldExcludeComponentsPackagesWithNoComponents() {
        // Given
        ModuleDTO module1 = new ModuleDTO("Module1", asList(mockDescriptor1, mockDescriptor2));
        ModuleDTO module2 = new ModuleDTO("Module 2", emptyList());

        // When
        List<DefaultMutableTreeNode> treeNodes =
                PalettePanel.asTreeNodes(asList(module1, module2));

        // Then
        assertThat(treeNodes).hasSize(1);
        assertExistsTreeNodeWithNameAndChildrenUserObjects(treeNodes, "Module1", mockDescriptor1, mockDescriptor2);
    }

    private void assertThatExistsChildrenWithUserObject(List<DefaultMutableTreeNode> childrenTreeNodes, ModuleComponentDTO expectedDTO) {
        boolean found = childrenTreeNodes
                .stream()
                .anyMatch(defaultMutableTreeNode -> defaultMutableTreeNode.getUserObject() == expectedDTO);
        assertThat(found).isTrue();
    }

    private Optional<DefaultMutableTreeNode> getTreeNodeHavingName(List<DefaultMutableTreeNode> treeNodes, String expectedName) {
        return treeNodes
                .stream()
                .filter(defaultMutableTreeNode -> defaultMutableTreeNode.getUserObject() == expectedName)
                .findFirst();
    }

    private void assertExistsTreeNodeWithNameAndChildrenUserObjects(List<DefaultMutableTreeNode> treeNodes, String expectedName, ModuleComponentDTO... dtos) {
        Optional<DefaultMutableTreeNode> treeNodeMatchingName = getTreeNodeHavingName(treeNodes, expectedName);
        assertThat(treeNodeMatchingName).isPresent();

        DefaultMutableTreeNode treeNode = treeNodeMatchingName.get();
        List<DefaultMutableTreeNode> treeNodeChildren = toList(treeNode.children());
        assertThat(treeNodeChildren).hasSize(dtos.length);

        stream(dtos)
                .forEach(descriptor -> assertThatExistsChildrenWithUserObject(treeNodeChildren, descriptor));
    }

    private List<DefaultMutableTreeNode> toList(Enumeration<?> enumeration) {
        List<DefaultMutableTreeNode> treeNodes = new ArrayList<>();
        while (enumeration.hasMoreElements()) {
            treeNodes.add((DefaultMutableTreeNode) enumeration.nextElement());
        }
        return treeNodes;
    }
}
