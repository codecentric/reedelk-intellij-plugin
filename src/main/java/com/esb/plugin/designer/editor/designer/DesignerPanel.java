package com.esb.plugin.designer.editor.designer;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.DropListener;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphChangeListener;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.decorators.NothingSelectedDrawable;
import com.esb.plugin.designer.graph.layout.FlowGraphLayout;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collection;
import java.util.Optional;
import java.util.TooManyListenersException;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkState;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class DesignerPanel extends JBPanel implements MouseMotionListener, MouseListener, FlowGraphChangeListener, DropTargetListener {

    private static final Logger LOG = Logger.getInstance(DesignerPanel.class);

    private final JBColor BACKGROUND_COLOR = JBColor.WHITE;
    private final Drawable NOTHING_SELECTED = new NothingSelectedDrawable();

    private FlowGraph graph;
    private Drawable selected = NOTHING_SELECTED;

    private int offsetx;
    private int offsety;
    private boolean updated;
    private boolean dragging;


    public DesignerPanel() {
        DropTarget dropTarget = new DropTarget();
        try {
            dropTarget.addDropTargetListener(this);
        } catch (TooManyListenersException e) {
            LOG.error(e);
        }
        setDropTarget(dropTarget);
        setBackground(BACKGROUND_COLOR);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (graph == null) return;

        // Set Antialiasing
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

        // We compute again the graph layout if and only if it was updated.
        if (updated) {
            FlowGraphLayout.compute(graph, g2);
            System.out.println("------- Graph Updated --------");
            graph.breadthFirstTraversal(new Consumer<Drawable>() {
                @Override
                public void accept(Drawable drawable) {
                    System.out.println("Name: " + drawable.component().getName() + ", x: " + drawable.x() + ", y: " + drawable.y());
                }
            });
            adjustWindowSize();
            updated = false;
        }

        Collection<Drawable> graphNodes = graph.nodes();

        // Draw each node of the graph (except the current selected Drawable - see below)
        graphNodes.forEach(drawable -> {
            if (!drawable.isSelected()) {
                drawable.draw(graph, g2, DesignerPanel.this);
            }
        });

        // The selected drawable must be drawn LAST so
        // that it is on top of all the other drawables.
        graphNodes
                .stream()
                .filter(Drawable::isSelected)
                .findFirst()
                .ifPresent(drawable -> drawable.draw(graph, g2, DesignerPanel.this));
    }

    @Override
    public void updated(FlowGraph updatedGraph) {
        checkState(updatedGraph != null, "Updated graph must not be null");
        SwingUtilities.invokeLater(() -> {
            graph = updatedGraph;
            updated = true;
            invalidate();
            repaint();
        });
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (dragging) {
            selected.drag(event.getX() - offsetx, event.getY() - offsety);
            repaint();
        }
    }


    @Override
    public void mouseMoved(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();

        Optional<Drawable> drawableWithinCoordinates = getDrawableWithinCoordinates(x, y);
        if (drawableWithinCoordinates.isPresent()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();

        Optional<Drawable> drawableWithinCoordinates = getDrawableWithinCoordinates(x, y);
        if (drawableWithinCoordinates.isPresent()) {
            // Unselect the previous one
            selected.unselected();
            selected = drawableWithinCoordinates.get();

            offsetx = event.getX() - selected.x();
            offsety = event.getY() - selected.y();

            selected.selected();
            selected.dragging();
            selected.drag(event.getX() - offsetx, event.getY() - offsety);

            dragging = true;

        } else {
            // If no drawable is selected, then we reset it.
            selected.unselected();
            selected = NOTHING_SELECTED;
        }

        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (dragging) {
            dragging = false;

            int dragX = e.getX();
            int dragY = e.getY();

            selected.drag(dragX, dragY);
            selected.drop();

            MoveActionHandler delegate = new MoveActionHandler(
                    graph,
                    (Graphics2D) getGraphics(),
                    selected,
                    new Point(dragX, dragY));
            delegate.handle().ifPresent(this::updated);

            repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // nothing to do
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // nothing to do
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // nothing to do
    }

    @Override
    public void drop(DropTargetDropEvent dropEvent) {
        PaletteDropActionHandler delegate = new PaletteDropActionHandler(graph, (Graphics2D) getGraphics(), dropEvent);
        Optional<FlowGraph> updatedGraph = delegate.handle();
        updatedGraph.ifPresent(this::updated);
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        System.out.println("Drag enter");
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        System.out.println("Drag over");
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        System.out.println("Drop Action Changed");
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        System.out.println("Drag exit");
    }

    public void setDropListener(@NotNull DropListener dropListener) {
        // TODO: Do something about this.
        //  this.dropListener = dropListener;
    }

    /**
     * If the graph has grown beyond the current window size, we must adapt-it.
     */
    private void adjustWindowSize() {
        int maxX = graph.nodes().stream().mapToInt(Drawable::x).max().getAsInt();
        int maxY = graph.nodes().stream().mapToInt(Drawable::y).max().getAsInt();
        int newSizeX = maxX + Tile.WIDTH;
        int newSizeY = maxY + Tile.HEIGHT;
        setSize(new Dimension(newSizeX, newSizeY));
        setPreferredSize(new Dimension(newSizeX, newSizeY));
    }

    private Optional<Drawable> getDrawableWithinCoordinates(int x, int y) {
        //TODO: Graph should never be null.
        return graph == null ? Optional.empty() :
                graph.nodes()
                        .stream()
                        .filter(drawable -> drawable.contains(this, x, y))
                        .findFirst();
    }

}
