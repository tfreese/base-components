package de.freese.base.mvc;

import java.util.Objects;

import javax.swing.JFrame;

import de.freese.base.mvc.registry.AbstractServiceRegistry;
import de.freese.base.resourcemap.ResourceMap;

/**
 * @author Thomas Freese
 */
public final class ApplicationContext extends AbstractServiceRegistry {
    private JFrame mainFrame;
    private ResourceMap resourceMapRoot;
    private String userId;

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public ResourceMap getResourceMap(final String bundleName) {
        return getResourceMapRoot().getChild(bundleName);
    }

    public ResourceMap getResourceMapRoot() {
        return resourceMapRoot;
    }

    /**
     * If not set, the SystemProperty "user.name" is used.
     */
    public String getUserId() {
        if (userId == null) {
            userId = System.getProperty("user.name").toUpperCase();
        }

        return userId;
    }

    public void setMainFrame(final JFrame mainFrame) {
        this.mainFrame = Objects.requireNonNull(mainFrame, "mainFrame required");
    }

    public void setResourceMapRoot(final ResourceMap resourceMapRoot) {
        this.resourceMapRoot = resourceMapRoot;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }
}
