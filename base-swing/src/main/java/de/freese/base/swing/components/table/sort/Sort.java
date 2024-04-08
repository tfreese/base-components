// Created: 27.06.2009
package de.freese.base.swing.components.table.sort;

/**
 * Enum für die Sortierungsmöglichkeiten.
 *
 * @author Thomas Freese
 */
public enum Sort {
    /**
     * 0
     */
    ASCENDING,
    /**
     * 1
     */
    DESCENDING,
    /**
     * -1
     */
    UNSORTED;

    // Cycle the sorting states through {NOT_SORTED, ASCENDING, DESCENDING}
    // or {NOT_SORTED, DESCENDING, ASCENDING} depending on whether shift is pressed.
    // status = status + (e.isShiftDown() ? (-1) : 1);
    // status = ((status + 4) % 3) - 1; // signed mod, returning {-1, 0, 1}

    public Sort getNext() {
        if (UNSORTED.name().equals(name())) {
            return ASCENDING;
        }
        else if (ASCENDING.name().equals(name())) {
            return DESCENDING;
        }
        else {
            return UNSORTED;
        }
    }

    public Sort getPrevious() {
        if (UNSORTED.name().equals(name())) {
            return DESCENDING;
        }
        else if (DESCENDING.name().equals(name())) {
            return ASCENDING;
        }
        else {
            return UNSORTED;
        }
    }
}
