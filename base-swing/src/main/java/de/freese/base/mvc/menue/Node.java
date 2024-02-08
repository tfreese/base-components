// Created: 08.02.24
package de.freese.base.mvc.menue;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import javax.swing.Icon;

/**
 * @author Thomas Freese
 */
public final class Node {
    private final Map<String, Node> children = new LinkedHashMap<>();
    private final Set<Component> components = new HashSet<>();
    private final String name;
    private final NodeType nodeType;

    private ActionListener actionListener;
    private Icon icon;
    private Node parent;
    private Supplier<String> textSupplier;

    public Node(final NodeType nodeType, final String name) {
        super();

        this.nodeType = Objects.requireNonNull(nodeType, "nodeType required");
        this.name = Objects.requireNonNull(name, "name required");
    }

    public void addChild(final Node child) {
        children.put(child.getName(), child);
        child.setParent(this);
    }

    public void addComponent(final Component component) {
        components.add(component);
    }

    public ActionListener getActionListener() {
        return actionListener;
    }

    public Node getChild(final String name) {
        return children.get(name);
    }

    public List<Node> getChildren() {
        return List.copyOf(children.values());
    }

    public Set<Component> getComponents() {
        return Set.copyOf(components);
    }

    public Icon getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public Node getParent() {
        return parent;
    }

    public Supplier<String> getTextSupplier() {
        return textSupplier;
    }

    public void setActionListener(final ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setEnabled(final boolean enabled) {
        components.forEach(component -> component.setEnabled(enabled));

        if (parent != null) {
            parent.decideEnabled();
        }
    }

    public void setIcon(final Icon icon) {
        this.icon = icon;
    }

    public void setTextSupplier(final Supplier<String> textSupplier) {
        this.textSupplier = textSupplier;
    }

    public void setVisible(final boolean visible) {
        components.forEach(component -> component.setVisible(visible));

        if (parent != null) {
            parent.decideVisible();
        }
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" [");
        sb.append("path=").append(getPath());
        sb.append(']');

        return sb.toString();
    }

    private void decideEnabled() {
        final long enabledChildren = children.values().stream().flatMap(node -> node.getComponents().stream()).filter(Objects::nonNull).filter(Component::isEnabled).count();
        setEnabled(enabledChildren > 0);
    }

    private void decideVisible() {
        final long visibleChildren = children.values().stream().flatMap(node -> node.getComponents().stream()).filter(Objects::nonNull).filter(Component::isVisible).count();
        setVisible(visibleChildren > 0);
    }

    private String getPath() {
        String path = getName();

        if (parent != null) {
            path += "." + parent.getPath();
        }

        return path;
    }

    private void setParent(final Node parent) {
        this.parent = parent;
    }
}
