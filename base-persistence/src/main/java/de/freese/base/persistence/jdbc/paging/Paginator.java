// Created: 21 Okt. 2025
package de.freese.base.persistence.jdbc.paging;

import java.util.List;

/**
 * @author Thomas Freese
 */
public interface Paginator<T> {
    /**
     * SQL: "%s LIMIT %d OFFSET %d
     *
     * @return {@link List} Never null.
     */
    List<T> getPage(int offset, int limit);
}
