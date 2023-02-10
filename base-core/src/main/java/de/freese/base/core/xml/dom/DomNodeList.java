package de.freese.base.core.xml.dom;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * {@link NodeList} auf Basis einer {@link ArrayList} für die Möglichkeit der Sortierung.<br>
 * Diese Liste kann nur DOM {@link Node} Objekte aufnehmen.
 *
 * @author Thomas Freese
 */
public class DomNodeList extends ArrayList<Node> implements NodeList {
    @Serial
    private static final long serialVersionUID = 510442491147365569L;

    public DomNodeList() {
        super();
    }

    public DomNodeList(final NodeList nodeList) {
        super(nodeList.getLength());

        addAll(nodeList);
    }

    /**
     * @see java.util.ArrayList#addAll(int, java.util.Collection)
     */
    @Override
    public boolean addAll(final int index, final Collection<? extends Node> c) {
        throw new UnsupportedOperationException();
    }

    public void addAll(final NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            add(nodeList.item(i));
        }
    }

    /**
     * @see org.w3c.dom.NodeList#getLength()
     */
    @Override
    public int getLength() {
        return size();
    }

    /**
     * @see org.w3c.dom.NodeList#item(int)
     */
    @Override
    public Node item(final int index) {
        return get(index);
    }
}
