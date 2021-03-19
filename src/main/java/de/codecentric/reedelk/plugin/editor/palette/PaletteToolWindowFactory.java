package de.codecentric.reedelk.plugin.editor.palette;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import de.codecentric.reedelk.plugin.commons.Icons;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

public class PaletteToolWindowFactory implements ToolWindowFactory {

    public static final String ID = "com.reedelk.plugin.toolwindow.palette.components";

    @Override
    public void init(ToolWindow window) {
        window.setStripeTitle(message("toolwindow.components.title"));
        window.setIcon(Icons.ToolWindow.Components);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JBPanel panel = new PalettePanel(project);

        final ContentFactory contentFactory = ServiceManager.getService(ContentFactory.class);
        final Content content = contentFactory.createContent(panel, StringUtils.EMPTY, false);

        toolWindow.getContentManager().addContent(content);
    }
}
