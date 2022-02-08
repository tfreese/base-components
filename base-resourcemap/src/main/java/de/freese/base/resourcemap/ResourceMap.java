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

import de.freese.base.resourcemap.converter.ResourceConverter;

/**
 * ResourceMap for hierachical internationalisations.<br>
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
public interface ResourceMap
{
    /**
     * Translate a Key from an Enum.
     *
     * @author Thomas Freese
     */
    enum EnumResourceType
    {
        /**
         *
         */
        ICON,
        /**
         *
         */
        SHORT_DESCRIPTION,
        /**
         *
         */
        TEXT
        {
            /**
             * @see de.freese.base.resourcemap.DefaultResourceMap.EnumResourceType#getPostFix()
             */
            @Override
            protected String getPostFix()
            {
                return "";
            }
        };

        /**
         * @param enumValue {@link Enum}
         *
         * @return {@link String}
         */
        public final String getEnumKey(final Enum<?> enumValue)
        {
            String clazz = enumValue.getClass().getSimpleName().toLowerCase();
            String value = enumValue.name().toLowerCase();

            return String.format("%s.%s.%s%s", "enum", clazz, value, getPostFix());
        }

        /**
         * @return {@link String}
         */
        protected String getPostFix()
        {
            return String.format(".%s", name().toLowerCase());
        }
    }

    /**
     * @param key String
     *
     * @return Boolean
     *
     * @see #getObject
     */
    default Boolean getBoolean(final String key)
    {
        return getObject(key, Boolean.class);
    }

    /**
     * @param key String
     * @param defaultValue boolean
     *
     * @return boolean
     *
     * @see #getObject
     */
    default boolean getBoolean(final String key, final boolean defaultValue)
    {
        Boolean value = getBoolean(key);

        return value != null ? value : defaultValue;
    }

    /**
     * The Name of the {@link ResourceMap}.
     *
     * @return String
     */
    String getBundleName();

    /**
     * @param key String
     *
     * @return Byte
     *
     * @see #getObject
     */
    default Byte getByte(final String key)
    {
        return getObject(key, Byte.class);
    }

    /**
     * @param key String
     * @param defaultValue byte
     *
     * @return byte
     *
     * @see #getObject
     */
    default byte getByte(final String key, final byte defaultValue)
    {
        Byte value = getObject(key, Byte.class);

        return value != null ? value : defaultValue;
    }

    /**
     * Rekursive Search for Child.
     *
     * @param bundleName String
     *
     * @return {@link ResourceMap}
     */
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
     *
     * @param key String
     *
     * @return {@link Color}
     *
     * @see #getObject
     */
    default Color getColor(final String key)
    {
        return getObject(key, Color.class);
    }

    /**
     * @param key String
     *
     * @return {@link Dimension}
     */
    default Dimension getDimension(final String key)
    {
        return getObject(key, Dimension.class);
    }

    /**
     * @param key String
     *
     * @return Double
     *
     * @see #getObject
     */
    default Double getDouble(final String key)
    {
        return getObject(key, Double.class);
    }

    /**
     * @param key String
     * @param defaultValue double
     *
     * @return double
     *
     * @see #getObject
     */
    default double getDouble(final String key, final double defaultValue)
    {
        Double value = getObject(key, Double.class);

        return value != null ? value : defaultValue;
    }

    /**
     * @param key String
     *
     * @return {@link EmptyBorder}
     */
    default EmptyBorder getEmptyBorder(final String key)
    {
        return getObject(key, EmptyBorder.class);
    }

    /**
     * @param key String
     *
     * @return Float
     *
     * @see #getObject
     */
    default Float getFloat(final String key)
    {
        return getObject(key, Float.class);
    }

    /**
     * @param key String
     * @param defaultValue float
     *
     * @return Float
     *
     * @see #getObject
     */
    default Float getFloat(final String key, final float defaultValue)
    {
        Float value = getObject(key, Float.class);

        return value != null ? value : defaultValue;
    }

    /**
     * Format:
     *
     * <pre>
     * font = Arial - PLAIN - 12
     * </pre>
     *
     * @param key String
     *
     * @return {@link Font}
     *
     * @see #getObject
     * @see Font#decode
     */
    default Font getFont(final String key)
    {
        return getObject(key, Font.class);
    }

    /**
     * Format:
     *
     * <pre>
     * enum.ENUMCLASS.ENUMNAME.icon = icon
     * </pre>
     *
     * @param enumValue {@link Enum}
     *
     * @return {@link Icon}
     *
     * @see #getObject
     */
    default Icon getIcon(final Enum<?> enumValue)
    {
        return getObject(EnumResourceType.ICON.getEnumKey(enumValue), Icon.class);
    }

    /**
     * Format:
     *
     * <pre>
     * icon = myIcon.png
     * </pre>
     *
     * @param key String
     *
     * @return {@link Icon}
     *
     * @see #getObject
     */
    default Icon getIcon(final String key)
    {
        return getObject(key, Icon.class);
    }

    /**
     * Format:
     *
     * <pre>
     * image = myIcon.png
     * </pre>
     *
     * @param key String
     *
     * @return {@link BufferedImage}
     *
     * @see #getObject
     */
    default Image getImage(final String key)
    {
        return getObject(key, BufferedImage.class);
    }

    /**
     * Format:
     *
     * <pre>
     * image = myIcon.png
     * </pre>
     *
     * @param key String
     *
     * @return {@link ImageIcon}
     *
     * @see #getObject
     */
    default ImageIcon getImageIcon(final String key)
    {
        return getObject(key, ImageIcon.class);
    }

    /**
     * Format:
     *
     * <pre>
     * inset = top,left,bottom,right
     * </pre>
     *
     * @param key String
     *
     * @return {@link Insets}
     */
    default Insets getInsets(final String key)
    {
        return getObject(key, Insets.class);
    }

    /**
     * @param key String
     *
     * @return Integer
     *
     * @see #getObject
     */
    default Integer getInteger(final String key)
    {
        return getObject(key, Integer.class);
    }

    /**
     * @param key String
     * @param defaultValue int
     *
     * @return int
     *
     * @see #getObject
     */
    default int getInteger(final String key, final int defaultValue)
    {
        Integer value = getObject(key, Integer.class);

        return value != null ? value : defaultValue;
    }

    /**
     * Format:
     *
     * <pre>
     * keyCode = control T
     * </pre>
     *
     * @param key String
     *
     * @return Integer
     *
     * @see #getKeyStroke
     */
    default Integer getKeyCode(final String key)
    {
        KeyStroke ks = getKeyStroke(key);

        return (ks != null) ? ks.getKeyCode() : null;
    }

    /**
     * Format:
     *
     * <pre>
     * keyStroke = control T
     * </pre>
     *
     * @param key String
     *
     * @return {@link KeyStroke}
     *
     * @see #getObject
     * @see KeyStroke#getKeyStroke
     */
    default KeyStroke getKeyStroke(final String key)
    {
        return getObject(key, KeyStroke.class);
    }

    /**
     * @param key String
     *
     * @return Long
     *
     * @see #getObject
     */
    default Long getLong(final String key)
    {
        return getObject(key, Long.class);
    }

    /**
     * @param key String
     * @param defaultValue long
     *
     * @return long
     *
     * @see #getObject
     */
    default long getLong(final String key, final long defaultValue)
    {
        Long value = getObject(key, Long.class);

        return value != null ? value : defaultValue;
    }

    /**
     * Uses a {@link ResourceConverter} to convert the String value.<br>
     *
     * @param <T> Type
     * @param key String
     * @param type resource type
     *
     * @return Object
     */
    <T> T getObject(final String key, final Class<T> type);

    /**
     * Format:
     *
     * <pre>
     * point = 100,200
     * </pre>
     *
     * @param key String
     *
     * @return {@link Point}
     */
    default Point getPoint(final String key)
    {
        return getObject(key, Point.class);
    }

    /**
     * Format:
     *
     * <pre>
     * rectangle = 5,5,5,5
     * </pre>
     *
     * @param key String
     *
     * @return {@link Rectangle}
     */
    default Rectangle getRectangle(final String key)
    {
        return getObject(key, Rectangle.class);
    }

    /**
     * @param key String
     *
     * @return Short
     *
     * @see #getObject
     */
    default Short getShort(final String key)
    {
        return getObject(key, Short.class);
    }

    /**
     * @param key String
     * @param defaultValue short
     *
     * @return Short
     *
     * @see #getObject
     */
    default short getShort(final String key, final short defaultValue)
    {
        Short value = getObject(key, Short.class);

        return value != null ? value : defaultValue;
    }

    /**
     * Format:
     *
     * <pre>
     * enum.ENUMCLASS.ENUMNAME.text = value
     * </pre>
     *
     * @param enumValue {@link Enum}
     *
     * @return {@link String}
     */
    default String getString(final Enum<?> enumValue)
    {
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
     *
     * @param key String
     * @param args Object
     *
     * @return String
     */
    String getString(final String key, final Object...args);

    /**
     * @param key String
     *
     * @return {@link URI}
     */
    default URI getURI(final String key)
    {
        return getObject(key, URI.class);
    }

    /**
     * @param key String
     *
     * @return {@link URL}
     */
    default URL getURL(final String key)
    {
        return getObject(key, URL.class);
    }

    /**
     * Load Resources if absent for {@link Locale}.<br>
     * Calls the childs recursive.
     *
     * @param locale {@link Locale}
     */
    void load(Locale locale);
}
