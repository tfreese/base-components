package de.freese.base.resourcemap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

/**
 * ResourceMap for hierarchical internationalization.<br>
 * Configuration Example:
 *
 * <pre>
 * <code>
 * ResourceMap rootMap = ResourceMapBuilder.create()
 *      .resourceProvider(new ResourceBundleProvider())
 *      .defaultConverters()
 *      .cacheDisabled()
 *      .bundleName("parentTest")
 *      .addChild()
 *          .bundleName("bundles/test1")
 *          .addChild()
 *              .bundleName("bundles/test2")
 *              .cacheDisabled()
 *              .done()
 *          .done()
 *      .build();
 * </code>
 * </pre>
 *
 * @author Thomas Freese
 */
public interface ResourceMap {
    /**
     * Translate a Key from an Enum.
     *
     * @author Thomas Freese
     */
    enum EnumResourceType {
        ICON,
        SHORT_DESCRIPTION,
        TEXT {
            @Override
            protected String getPostFix() {
                return "";
            }
        };

        public final String getEnumKey(final Enum<?> enumValue) {
            String clazz = enumValue.getClass().getSimpleName().toLowerCase();
            String value = enumValue.name().toLowerCase();

            return String.format("%s.%s.%s%s", "enum", clazz, value, getPostFix());
        }

        protected String getPostFix() {
            return String.format(".%s", name().toLowerCase());
        }
    }

    default Boolean getBoolean(final String key) {
        return getObject(key, Boolean.class);
    }

    default boolean getBoolean(final String key, final boolean defaultValue) {
        Boolean value = getBoolean(key);

        return value != null ? value : defaultValue;
    }

    String getBundleName();

    default Byte getByte(final String key) {
        return getObject(key, Byte.class);
    }

    default byte getByte(final String key, final byte defaultValue) {
        Byte value = getObject(key, Byte.class);

        return value != null ? value : defaultValue;
    }

    ResourceMap getChild(String bundleName);

    /**
     * Formats:
     *
     * <pre>
     * hexRGBColor = #RRGGBB
     * hexAlphaRGBColor = #AARRGGBB
     * rgbColor = R, G, B
     * alphaRGBColor = R, G, B, A
     * </pre>
     */
    default Color getColor(final String key) {
        return getObject(key, Color.class);
    }

    default Dimension getDimension(final String key) {
        return getObject(key, Dimension.class);
    }

    default Double getDouble(final String key) {
        return getObject(key, Double.class);
    }

    default double getDouble(final String key, final double defaultValue) {
        Double value = getObject(key, Double.class);

        return value != null ? value : defaultValue;
    }

    default EmptyBorder getEmptyBorder(final String key) {
        return getObject(key, EmptyBorder.class);
    }

    default Float getFloat(final String key) {
        return getObject(key, Float.class);
    }

    default Float getFloat(final String key, final float defaultValue) {
        Float value = getObject(key, Float.class);

        return value != null ? value : defaultValue;
    }

    /**
     * Format:
     *
     * <pre>
     * font = Arial - PLAIN - 12
     * </pre>
     */
    default Font getFont(final String key) {
        return getObject(key, Font.class);
    }

    /**
     * Format:
     *
     * <pre>
     * enum.ENUMCLASS.ENUMNAME.icon = icon
     * </pre>
     */
    default Icon getIcon(final Enum<?> enumValue) {
        return getObject(EnumResourceType.ICON.getEnumKey(enumValue), Icon.class);
    }

    /**
     * Format:
     *
     * <pre>
     * icon = myIcon.png
     * </pre>
     */
    default Icon getIcon(final String key) {
        return getObject(key, Icon.class);
    }

    /**
     * Format:
     *
     * <pre>
     * image = myIcon.png
     * </pre>
     */
    default Image getImage(final String key) {
        return getObject(key, BufferedImage.class);
    }

    /**
     * Format:
     *
     * <pre>
     * image = myIcon.png
     * </pre>
     */
    default ImageIcon getImageIcon(final String key) {
        return getObject(key, ImageIcon.class);
    }

    /**
     * Format:
     *
     * <pre>
     * inset = top,left,bottom,right
     * </pre>
     */
    default Insets getInsets(final String key) {
        return getObject(key, Insets.class);
    }

    default Integer getInteger(final String key) {
        return getObject(key, Integer.class);
    }

    default int getInteger(final String key, final int defaultValue) {
        Integer value = getObject(key, Integer.class);

        return value != null ? value : defaultValue;
    }

    /**
     * Format:
     *
     * <pre>
     * keyCode = control T
     * </pre>
     */
    default Integer getKeyCode(final String key) {
        KeyStroke ks = getKeyStroke(key);

        return (ks != null) ? ks.getKeyCode() : null;
    }

    /**
     * Format:
     *
     * <pre>
     * keyStroke = control T
     * </pre>
     */
    default KeyStroke getKeyStroke(final String key) {
        return getObject(key, KeyStroke.class);
    }

    default Long getLong(final String key) {
        return getObject(key, Long.class);
    }

    default long getLong(final String key, final long defaultValue) {
        Long value = getObject(key, Long.class);

        return value != null ? value : defaultValue;
    }

    <T> T getObject(String key, Class<T> type);

    /**
     * Format:
     *
     * <pre>
     * point = 100,200
     * </pre>
     */
    default Point getPoint(final String key) {
        return getObject(key, Point.class);
    }

    /**
     * Format:
     *
     * <pre>
     * rectangle = 5,5,5,5
     * </pre>
     */
    default Rectangle getRectangle(final String key) {
        return getObject(key, Rectangle.class);
    }

    default Short getShort(final String key) {
        return getObject(key, Short.class);
    }

    default short getShort(final String key, final short defaultValue) {
        Short value = getObject(key, Short.class);

        return value != null ? value : defaultValue;
    }

    /**
     * Format:
     *
     * <pre>
     * enum.ENUMCLASS.ENUMNAME.text = value
     * </pre>
     */
    default String getString(final Enum<?> enumValue) {
        return getString(EnumResourceType.TEXT.getEnumKey(enumValue));
    }

    /**
     * Strings can be composed:
     *
     * <pre>
     * <code>
     * Application.title = My Application
     * ErrorDialog.title = Error: ${application.title}
     * WarningDialog.title = Warning: ${application.title}
     * </code>
     * </pre>
     *
     * Strings can have placeholders
     *
     * <pre>
     * hello = Hello {0}
     * hello = Hello %s
     * </pre>
     */
    String getString(String key, Object... args);

    default URI getURI(final String key) {
        return getObject(key, URI.class);
    }

    default URL getURL(final String key) {
        return getObject(key, URL.class);
    }

    /**
     * Load Resources if absent for {@link Locale}.<br>
     * Calls the children recursive.
     */
    void load(Locale locale);
}
