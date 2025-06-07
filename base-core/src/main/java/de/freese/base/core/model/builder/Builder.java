// Created: 15.04.2020
package de.freese.base.core.model.builder;

/**
 * The Builder interface is designed to designate a class as a <em>builder</em> object in the Builder design pattern. Builders are capable of creating and
 * configuring objects or results that normally take multiple steps to construct or are very complex to derive.<br>
 * <br>
 * The builder interface defines a single method, {@link #build()}, that classes must implement. The result of this method should be the final configured object
 * or result after all building operations are performed.<br>
 * <br>
 * It is a recommended practice that the methods supplied to configure the object or result being built return a reference to {@code this} so that method calls
 * can be chained together.<br>
 * <br>
 * Example Builder:<br>
 * <pre>
 * {@code
 * class FontBuilder implements Builder&lt;Font&gt; {
 *     private Font font;
 *
 *     public FontBuilder(String fontName) {
 *         font = new Font(fontName, Font.PLAIN, 12);
 *     }
 *
 *     public FontBuilder bold() {
 *         font = font.deriveFont(Font.BOLD);
 *         return this; // Reference returned so calls can be chained
 *     }
 *
 *     public FontBuilder size(float pointSize) {
 *         font = font.deriveFont(pointSize);
 *         return this; // Reference returned so calls can be chained
 *     }
 *
 *     // Other Font construction methods
 *
 *     public Font build() {
 *         return font;
 *     }
 * }
 * }
 * </pre>
 *
 * Example Builder Usage:<br>
 * {@code
 * Font bold14ptSansSerifFont = new FontBuilder(Font.SANS_SERIF).bold().size(14.0F).build();
 * }
 *
 * @param <T> the type of object that the builder will construct or compute.
 */
@FunctionalInterface
public interface Builder<T> {
    /**
     * Returns a reference to the object being constructed or result being calculated by the builder.
     *
     * @return the object constructed or result calculated by the builder.
     */
    T build();
}
