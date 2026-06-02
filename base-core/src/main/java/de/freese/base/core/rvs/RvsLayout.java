package de.freese.base.core.rvs;

import java.util.List;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public final class RvsLayout {

    private final List<RvsField> fields;
    private final int minLineLength;

    public RvsLayout(final List<RvsField> fields) {
        super();

        this.fields = List.copyOf(Objects.requireNonNull(fields, "fields required"));

        this.minLineLength = fields.stream()
                .mapToInt(RvsField::getEndExclusive)
                .max()
                .orElse(0);
    }

    public List<RvsField> getFields() {
        return fields;
    }

    public int getMinLineLength() {
        return minLineLength;
    }
}
