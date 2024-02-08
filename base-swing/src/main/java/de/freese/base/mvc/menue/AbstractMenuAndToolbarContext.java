// Created: 24.01.23
package de.freese.base.mvc.menue;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

/**
 * @author Thomas Freese
 */
public abstract class AbstractMenuAndToolbarContext {
    protected static final String ROOT_NAME = "ROOT";

    private static class DelegateAction extends AbstractAction {
        @Serial
        private static final long serialVersionUID = 6983453488082237165L;

        private transient ActionListener actionListener;

        @Override
        public void actionPerformed(final ActionEvent event) {
            if (event != null && actionListener != null) {
                actionListener.actionPerformed(event);
            }
        }

        public void setActionListener(final ActionListener actionListener) {
            this.actionListener = actionListener;
        }
    }

    private final Node root = new Node(NodeType.MENU, ROOT_NAME);

    public abstract void configure();

    public JMenuBar generateMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        menuBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));

        generateMenuBar(menuBar, getRoot());

        return menuBar;
    }

    public JToolBar generateToolBar() {
        final JToolBar toolBar = new JToolBar();
        toolBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        toolBar.setFloatable(false);

        generateToolBar(toolBar, getRoot());

        return toolBar;
    }

    public void resetDefaults() {
        iterateAll(getRoot(), node -> {
            node.setActionListener(null);

            for (Component component : node.getComponents()) {
                if (component instanceof AbstractButton ab && ab.getAction() instanceof DelegateAction da) {
                    da.setActionListener(null);
                }

                component.setEnabled(false);
                component.setVisible(true);
            }
        });
    }

    public void setActionListener(final String nameParent, final String name, final ActionListener actionListener) {
        final Node parent = getNode(getRoot(), nameParent);
        final Node node = parent.getChild(name);

        if (node == null) {
            throw new IllegalStateException("Node not found: " + name);
        }

        for (Component component : node.getComponents()) {
            if (component instanceof AbstractButton ab && ab.getAction() instanceof DelegateAction da) {
                da.setActionListener(actionListener);
            }
        }

        node.setVisible(true);
        node.setEnabled(true);
    }

    protected void addMenu(final String nameParent, final String name, final Consumer<Node> configurer) {
        final Node parent = getNode(getRoot(), nameParent);
        final Node node = new Node(NodeType.MENU, name);

        parent.addChild(node);

        configurer.accept(node);
    }

    protected void addMenuAndToolbarItem(final String nameParent, final String name, final Consumer<Node> configurer) {
        final Node parent = getNode(getRoot(), nameParent);
        final Node node = new Node(NodeType.MENU_AND_TOOLBAR_ITEM, name);

        parent.addChild(node);

        configurer.accept(node);
    }

    protected void addMenuItem(final String nameParent, final String name, final Consumer<Node> configurer) {
        final Node parent = getNode(getRoot(), nameParent);
        final Node node = new Node(NodeType.MENU_ITEM, name);

        parent.addChild(node);

        configurer.accept(node);
    }

    protected void addSeparator(final String nameParent) {
        final Node parent = getNode(getRoot(), nameParent);
        final Node node = new Node(NodeType.SEPARATOR, "SEPARATOR");

        parent.addChild(node);
    }

    protected void addToolbarItem(final String nameParent, final String name, final Consumer<Node> configurer) {
        final Node parent = getNode(getRoot(), nameParent);
        final Node node = new Node(NodeType.TOOLBAR_ITEM, name);

        parent.addChild(node);

        configurer.accept(node);
    }

    protected Node findNode(final Node parent, final String name) {
        Node child = parent.getChild(name);

        if (child == null) {
            for (Node parentChild : parent.getChildren()) {
                child = findNode(parentChild, name);

                if (child != null) {
                    break;
                }
            }
        }

        return child;
    }

    protected Node getNode(final Node parent, final String name) {
        if (ROOT_NAME.equals(name)) {
            return getRoot();
        }

        final Node node = findNode(parent, name);

        if (node == null) {
            throw new IllegalStateException("Node not found: " + name);
        }

        return node;
    }

    protected Node getRoot() {
        return root;
    }

    protected void iterateAll(final Node parent, final Consumer<Node> nodeConsumer) {
        for (Node child : parent.getChildren()) {
            nodeConsumer.accept(child);

            iterateAll(child, nodeConsumer);
        }
    }

    private void generateMenuBar(final JMenuBar menuBar, final Node parent) {
        for (Node child : parent.getChildren()) {
            if (NodeType.MENU.equals(child.getNodeType())) {
                final JMenu menu = new JMenu();
                menu.setAction(new DelegateAction());
                menu.setText(child.getTextSupplier().get());

                menuBar.add(menu);
                child.addComponent(menu);
            }
            else if (NodeType.MENU_ITEM.equals(child.getNodeType()) || NodeType.MENU_AND_TOOLBAR_ITEM.equals(child.getNodeType())) {
                final JMenuItem menuItem = new JMenuItem();
                menuItem.setAction(new DelegateAction());
                menuItem.setText(child.getTextSupplier().get());

                if (child.getIcon() != null) {
                    menuItem.setIcon(child.getIcon());
                }

                // Find Parent/JMenue
                final JMenu menu = parent.getComponents().stream().filter(JMenu.class::isInstance).map(JMenu.class::cast).findFirst().orElse(null);

                if (menu == null) {
                    throw new IllegalStateException("Parent must be a JMenu: " + parent.getName());
                }

                menu.add(menuItem);

                child.addComponent(menuItem);
            }
            else if (NodeType.SEPARATOR.equals(child.getNodeType())) {
                final JSeparator separator = new JSeparator();
                menuBar.add(separator);

                child.addComponent(separator);
            }

            generateMenuBar(menuBar, child);
        }
    }

    private void generateToolBar(final JToolBar toolBar, final Node parent) {
        for (Node child : parent.getChildren()) {
            if (NodeType.TOOLBAR_ITEM.equals(child.getNodeType()) || NodeType.MENU_AND_TOOLBAR_ITEM.equals(child.getNodeType())) {
                final JButton button = new JButton();
                button.setAction(new DelegateAction());

                if (child.getIcon() != null) {
                    button.setIcon(child.getIcon());
                    button.setToolTipText(child.getTextSupplier().get());
                }
                else {
                    button.setText(child.getTextSupplier().get());
                }

                toolBar.add(button);
                child.addComponent(button);
            }
            else if (NodeType.SEPARATOR.equals(child.getNodeType())) {
                // toolBar.addSeparator();
                final JToolBar.Separator separator = new JToolBar.Separator();
                toolBar.add(separator);

                child.addComponent(separator);
            }

            generateToolBar(toolBar, child);
        }
    }

    private void setState(final Node parentNode, final String name, final ComponentState state) {
        final Node node = parentNode.getChild(name);

        if (node == null) {
            throw new IllegalStateException("Node not found: %s".formatted(name));
        }

        switch (state) {
            case VISIBLE_ENABLED -> {
                node.setVisible(true);
                node.setEnabled(true);
            }
            case VISIBLE_DISABLED -> {
                node.setVisible(true);
                node.setEnabled(false);
            }
            default -> {
                node.setVisible(false);
                node.setEnabled(false);
            }
        }
    }
}
