package de.codecentric.reedelk.plugin.commons;

import com.intellij.ide.BrowserUtil;
import com.intellij.ui.HyperlinkAdapter;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.net.URL;

public class HyperlinkListenerUtils {

    private HyperlinkListenerUtils() {
    }

    public static final HyperlinkListener BROWSER_OPEN = new HyperlinkAdapter() {
        @Override
        protected void hyperlinkActivated(HyperlinkEvent event) {
            URL url = event.getURL();
            if (url == null) {
                BrowserUtil.browse(event.getDescription());
            } else {
                BrowserUtil.browse(url);
            }
        }
    };
}
