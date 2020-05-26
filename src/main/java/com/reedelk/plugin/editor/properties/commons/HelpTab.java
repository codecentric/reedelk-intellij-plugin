package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.application.ApplicationManager;
import com.reedelk.plugin.commons.HyperlinkListenerUtils;
import com.reedelk.plugin.component.ComponentData;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Optional;

import static com.intellij.util.ui.JBUI.Borders;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public class HelpTab extends DisposableScrollPane {

    private static final String IMAGE_CACHE_PROPERTY = "imageCache";

    private boolean loaded = false;

    public HelpTab(ComponentData componentData) {
        createVerticalScrollBar();
        setBorder(Borders.empty());
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        setViewportView(ContainerFactory.pushTop(new PanelWithText.LoadingContentPanel()));

        addComponentListener(new ComponentListenerAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // If the content was already loaded, there is no need to loading it again.
                if (loaded) return;

                // Lazy loading of Table content. We do it so that we can optimize the rendering
                // of the component's properties.
                ApplicationManager.getApplication().invokeLater(() -> {

                    ImageCache imageCache = new ImageCache((BufferedImage) componentData.getComponentImage());
                    String componentDescription = Optional.ofNullable(componentData.getDescription())
                            .orElse(message("component.description.not.provided"));
                    String componentTitle = componentData.getDisplayName();

                    JEditorPane editorPane = new JEditorPane();
                    editorPane.setEditorKit(new HTMLEditorKit());
                    editorPane.setEditable(false);
                    editorPane.addHyperlinkListener(HyperlinkListenerUtils.BROWSER_OPEN);
                    editorPane.setContentType(CONTENT_TYPE);
                    editorPane.getDocument().putProperty(IMAGE_CACHE_PROPERTY, imageCache);
                    editorPane.setText(String.format(HTML_TEMPLATE, componentTitle, componentImageURI, componentDescription));

                    setViewportView(editorPane);

                    loaded = true;
                });
            }
        });
    }

    private static URI componentImageURI;
    static {
        try {
            componentImageURI = new URI("file:component-icon.png");
        } catch (URISyntaxException e) {
            // This exception will never be thrown.
        }
    }

    private static final String CONTENT_TYPE = "text/html";
    private static final String HTML_TEMPLATE =
            "<div style=\"color: #333333; padding-left:15px; padding-bottom:15px; padding-right:15px; font-family:verdana\">" +
                    "<h2>%s</h2>" +
                    "<img src=\"%s\"\">" +
                    "<p style=\"text-align: justify\">%s</p>" +
                    "</div>";

    static class ImageCache extends Hashtable {

        private final transient BufferedImage image;

        ImageCache(BufferedImage image) {
            this.image = image;
        }

        @Override
        public Object get(Object key) {
            if (componentImageURI.equals(key)) {
                return Toolkit.getDefaultToolkit().createImage(toByteArray(image));
            } else {
                return super.get(key);
            }
        }
    }

    public static byte[] toByteArray(BufferedImage originalImage) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(originalImage, "png", outputStream);
            outputStream.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
