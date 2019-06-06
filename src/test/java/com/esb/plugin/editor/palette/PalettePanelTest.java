package com.esb.plugin.editor.palette;

import com.esb.plugin.component.domain.ComponentDescriptor;
import com.esb.plugin.component.domain.ComponentsPackage;
import com.intellij.openapi.module.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PalettePanelTest {

    @Mock
    private Module mockModule;
    @Mock
    private ComponentDescriptor mockDescriptor1;
    @Mock
    private ComponentDescriptor mockDescriptor2;
    @Mock
    private ComponentDescriptor mockDescriptor3;

    private PalettePanel palettePanel;

    @BeforeEach
    void setUp() {
        palettePanel = spy(new TestablePalettePanel(mockModule));
    }

    @Nested
    @DisplayName("Build package tree node tests")
    class BuildPackageTreeNode {

        @Test
        void shouldCorrectlyBuildPackageTreeNode() {
            // Given
            ComponentsPackage module1 = new ComponentsPackage("Module1", asList(mockDescriptor1, mockDescriptor2));

            // When
            DefaultMutableTreeNode treeNode = palettePanel.buildPackageTreeNode(module1);

            // Then
            assertThat(treeNode.getUserObject()).isEqualTo("Module1");

            List<DefaultMutableTreeNode> children = Collections.list(treeNode.children());
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
            ComponentsPackage module1 = new ComponentsPackage("Module1", asList(mockDescriptor1, mockDescriptor2));

            // When
            DefaultMutableTreeNode treeNode = palettePanel.buildPackageTreeNode(module1);

            // Then
            List<DefaultMutableTreeNode> children = Collections.list(treeNode.children());
            assertThat(children).hasSize(1);

            assertThatExistsChildrenWithUserObject(children, mockDescriptor1);
        }
    }

    @Nested
    @DisplayName("Get components packages tree nodes tests")
    class GetComponentsPackagesTreeNodes {

        @Test
        void shouldCorrectlyReturnCorrectComponentsPackagesTreeNodes() {
            // Given
            DefaultMutableTreeNode mockTreeNode1 = mock(DefaultMutableTreeNode.class);
            DefaultMutableTreeNode mockTreeNode2 = mock(DefaultMutableTreeNode.class);

            ComponentsPackage module1 = new ComponentsPackage("Module1", asList(mockDescriptor1, mockDescriptor2));
            ComponentsPackage module2 = new ComponentsPackage("Module2", asList(mockDescriptor3));

            doReturn(asList(module1, module2))
                    .when(palettePanel)
                    .getComponentsPackages();

            doReturn(mockTreeNode1)
                    .when(palettePanel)
                    .buildPackageTreeNode(module1);

            doReturn(mockTreeNode2)
                    .when(palettePanel)
                    .buildPackageTreeNode(module2);

            // When
            List<MutableTreeNode> treeNodes = palettePanel.getComponentsPackagesTreeNodes();

            // Then
            assertThat(treeNodes).hasSize(2);
            verify(palettePanel).buildPackageTreeNode(module1);
            verify(palettePanel).buildPackageTreeNode(module2);
        }

        @Test
        void shouldExcludeComponentsPackagesWithNoComponents() {
            // Given
            DefaultMutableTreeNode mockTreeNode1 = mock(DefaultMutableTreeNode.class);

            ComponentsPackage module1 = new ComponentsPackage("Module1", asList(mockDescriptor1, mockDescriptor2));
            ComponentsPackage module2 = new ComponentsPackage("Module2", Collections.emptyList());

            doReturn(asList(module1, module2))
                    .when(palettePanel)
                    .getComponentsPackages();

            doReturn(mockTreeNode1)
                    .when(palettePanel)
                    .buildPackageTreeNode(module1);

            // When
            List<MutableTreeNode> treeNodes = palettePanel.getComponentsPackagesTreeNodes();

            // Then
            assertThat(treeNodes).hasSize(1);
            verify(palettePanel).buildPackageTreeNode(module1);
            verify(palettePanel, never()).buildPackageTreeNode(module2);
        }
    }

    private void assertThatExistsChildrenWithUserObject(List<DefaultMutableTreeNode> childrenTreeNodes, ComponentDescriptor expectedDescriptor) {
        boolean found = childrenTreeNodes
                .stream()
                .anyMatch(defaultMutableTreeNode -> defaultMutableTreeNode.getUserObject() == expectedDescriptor);
        assertThat(found).isTrue();
    }

    /**
     * Overrides the Palette Panel so that it does not call
     * SwingUtilities or subscribe to the message bus.
     */
    class TestablePalettePanel extends PalettePanel {

        TestablePalettePanel(Module module) {
            super(module);
        }

        @Override
        public void onComponentListUpdate() {
            // do nothing
        }

        @Override
        void registerComponentListUpdateNotifier() {
            // nothing to do
        }
    }
}