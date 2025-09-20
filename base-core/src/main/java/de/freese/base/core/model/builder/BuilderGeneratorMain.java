// Created: 13.11.22
package de.freese.base.core.model.builder;

import de.freese.base.core.model.tupel.Tupel3;

/**
 * @author Thomas Freese
 */
public final class BuilderGeneratorMain {
    static void main() {
        final BuilderGenerator generator = new BuilderGenerator(fields -> {
            fields.remove("serialVersionUID");
            fields.remove("valueB");

            return fields;
        });
        generator.createBuilder(Tupel3.class, true, System.out);
    }

    private BuilderGeneratorMain() {
        super();
    }
}
