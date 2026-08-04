package de.freese.base.core.i18n;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Bundle über einer bereits fertig gemergten Map (Properties + DB).
 *
 * @author Thomas Freese
 * @since 04.08.26
 */
final class MapResourceBundle extends ResourceBundle {

    private final Map<String, Object> lookup;

    MapResourceBundle(final Map<String, Object> lookup) {
        super();

        // Wird nach Konstruktion nicht mehr verändert -> effektiv immutable.
        this.lookup = Objects.requireNonNull(lookup, "lookup required");
    }

    @Override
    public Enumeration<String> getKeys() {
        final Set<String> keys = new HashSet<>(lookup.keySet());

        // parent ist protected in ResourceBundle.
        if (parent != null) {
            keys.addAll(Collections.list(parent.getKeys()));
        }

        return Collections.enumeration(keys);
    }

    @Override
    protected Object handleGetObject(final String key) {
        return lookup.get(Objects.requireNonNull(key));
    }

    /**
     * Nur die Keys DIESER Ebene – Parent-Merge macht getKeys().
     */
    @Override
    protected Set<String> handleKeySet() {
        return lookup.keySet();
    }
}
