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
import java.text.MessageFormat;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import de.freese.base.resourcemap.converter.ResourceConverter;

/**
 * Interface einer ResourceMap zum laden und verarbeiten lokalisierter Texte.
 *
 * @author Thomas Freese
 */
public interface ResourceMap
{
    /**
     * Ermittelt aus einem Enum den übersetzbaren Key.
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
     * Liefert den Namen der {@link ResourceMap}.
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
     * Sucht rekursiv nach der {@link ResourceMap}.
     *
     * @param bundleName String
     *
     * @return {@link ResourceMap}
     */
    ResourceMap getChild(String bundleName);

    /**
     * Liefert das Value des Keys als {@link Color}.<br>
     * Mögliche Formate:
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
     * Liefert das Value des Keys als {@link Font}.<br>
     * Format:
     *
     * <pre>
     * <code>font = Arial-PLAIN-12</code>
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
     * Liefert das Value des Keys als {@link Icon}.<br>
     * Format PropertyKey: enum.ENUMKLASSE.ENUMNAME.icon
     *
     * <pre>
     * icon = myIcon.png
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
     * Liefert das Value des Keys als {@link Icon}.<br>
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
     * Liefert das Value des Keys als {@link BufferedImage}.<br>
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
     * Liefert das Value des Keys als {@link ImageIcon}.<br>
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
     * Liefert das Value des Keys als {@link Insets}.<br>
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
     * Liefert das Value des {@link KeyStroke} als Integer.<br>
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
     * Liefert das Value des Keys als {@link KeyStroke}.<br>
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
     * Liefert das konvertierte Objekt aus dem String.<br>
     * Das Objekt wird durch einen {@link ResourceConverter} erzeugt, der mit dem Klassentyp verknüpft ist.<br>
     *
     * @param <T> Type
     * @param key String
     * @param type resource type
     *
     * @return Object
     */
    <T> T getObject(final String key, final Class<T> type);

    /**
     * Liefert das Value des Keys als {@link Point}.<br>
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
     * Liefert das Value des Keys als {@link Rectangle}.<br>
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
     * Liefert den Text für einen EnumTyp und den konkreten Wert.<br>
     * Format PropertyKey: enum.ENUMKLASSE.ENUMNAME
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
     * Liefert den String des Keys.<br>
     * Die Strings künnen wie folgt aus anderen Strings zusammengesetzt werden:
     *
     * <pre>
     * <code>
     * Application.title = My Application
     * ErrorDialog.title = Error: ${application.title}
     * WarningDialog.title = Warning: ${application.title}
     * </code>
     * </pre>
     *
     * Für Angabe einen StringFormats kann die ältere {} Notation oder die Java 1.5 Variante verwendet werden:
     *
     * <pre>
     * <code>hello = Hello {0}</code>
     * </pre>
     *
     * oder
     *
     * <pre>
     * <code>hello = Hello %s</code>
     * </pre>
     *
     * aber niemals beide gleichzeitig.
     *
     * @param key String
     * @param args Object
     *
     * @return String
     *
     * @see #getObject
     * @see String#format(String, Object...)
     * @see MessageFormat#format(String, Object...)
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
     * Laden der Resourcen der {@link ResourceMap} und deren Children.
     *
     * @param locale {@link Locale}
     */
    void load(Locale locale);
}
