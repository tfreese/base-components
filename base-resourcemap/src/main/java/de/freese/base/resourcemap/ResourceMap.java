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
import java.util.Set;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import de.freese.base.resourcemap.converter.ResourceConverter;
import de.freese.base.resourcemap.provider.ResourceProvider;

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
     * @param baseName String
     * @return {@link ResourceMap}
     */
    public static ResourceMap create(final String baseName)
    {
        return create(baseName, DefaultResourceMap.class.getClassLoader());
    }

    /**
     * @param baseName String
     * @param classLoader {@link ClassLoader}
     * @return {@link ResourceMap}
     */
    public static ResourceMap create(final String baseName, final ClassLoader classLoader)
    {
        return create(baseName, classLoader, null);
    }

    /**
     * @param baseName String
     * @param classLoader {@link ClassLoader}
     * @param resourceProvider {@link ResourceProvider}
     * @return {@link ResourceMap}
     */
    public static ResourceMap create(final String baseName, final ClassLoader classLoader, final ResourceProvider resourceProvider)
    {
        ResourceMap resourceMap = new DefaultResourceMap(baseName, classLoader, resourceProvider);

        return resourceMap;
    }

    /**
     * @param baseName String
     * @param resourceProvider {@link ResourceProvider}
     * @return {@link ResourceMap}
     */
    public static ResourceMap create(final String baseName, final ResourceProvider resourceProvider)
    {
        return create(baseName, DefaultResourceMap.class.getClassLoader(), resourceProvider);
    }

    /**
     * Hinzufügen eines neuen {@link ResourceConverter}s.<br>
     * Die Converter der Parent ResourceMaps vererben sich auf ihre Kinder.
     *
     * @param supportedType Class
     * @param converter {@link ResourceConverter}
     */
    public void addResourceConverter(Class<?> supportedType, final ResourceConverter<?> converter);

    /**
     * Liefert den BaseName der {@link ResourceMap}.
     *
     * @return String
     */
    public String getBaseName();

    /**
     * Liefert das Value des Keys als Boolean.
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return Boolean
     * @see #getObject
     */
    public default Boolean getBoolean(final String key)
    {
        return getObject(key, Boolean.class);
    }

    /**
     * Liefert das Value des Keys als Byte.
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return Byte
     * @see #getObject
     */
    public default Byte getByte(final String key)
    {
        return getObject(key, Byte.class);
    }

    /**
     * Liefert das Value des Keys als Color.<br>
     * Mögliche Formate:
     *
     * <pre>
     * myHexRGBColor = #RRGGBB
     * myHexAlphaRGBColor = #AARRGGBB
     * myRGBColor = R, G, B
     * myAlphaRGBColor = R, G, B, A
     * </pre>
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return {@link Color}
     * @see #getObject
     */
    public default Color getColor(final String key)
    {
        return getObject(key, Color.class);
    }

    /**
     * Liefert das Value des Keys als Dimension.
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return {@link Dimension}
     */
    public default Dimension getDimension(final String key)
    {
        return getObject(key, Dimension.class);
    }

    /**
     * Liefert das Value des Keys als Double.
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return Double
     * @see #getObject
     */
    public default Double getDouble(final String key)
    {
        return getObject(key, Double.class);
    }

    /**
     * Liefert das Value des Keys als EmptyBorder.
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return {@link EmptyBorder}
     */
    public default EmptyBorder getEmptyBorder(final String key)
    {
        return getObject(key, EmptyBorder.class);
    }

    /**
     * Liefert das Value des Keys als Float.
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return Float
     * @see #getObject
     */
    public default Float getFloat(final String key)
    {
        return getObject(key, Float.class);
    }

    /**
     * Liefert das Value des Keys als Font.<br>
     * Format:
     *
     * <pre>
     * <code>myFont = Arial-PLAIN-12</code>
     * </pre>
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return {@link Font}
     * @see #getObject
     * @see Font#decode
     */
    public default Font getFont(final String key)
    {
        return getObject(key, Font.class);
    }

    /**
     * Liefert das Value des Keys als Icon.<br>
     * Format PropertyKey: enum.ENUMKLASSE.ENUMNAME.icon
     *
     * <pre>
     * openIcon = myOpenIcon.png
     * </pre>
     *
     * @param enumValue {@link Enum}
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return {@link Icon}
     * @see #getObject
     */
    public default Icon getIcon(final Enum<?> enumValue)
    {
        return getObject(EnumResourceType.ICON.getEnumKey(enumValue), ImageIcon.class);
    }

    /**
     * Liefert das Value des Keys als Icon.<br>
     * Format:
     *
     * <pre>
     * openIcon = myOpenIcon.png
     * </pre>
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return {@link Icon}
     * @see #getObject
     */
    public default Icon getIcon(final String key)
    {
        return getObject(key, Icon.class);
    }

    /**
     * Liefert das Value des Keys als BufferedImage.<br>
     * Format:
     *
     * <pre>
     * openIcon = myOpenIcon.png
     * </pre>
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return {@link BufferedImage}
     * @see #getObject
     */
    public default Image getImage(final String key)
    {
        return getObject(key, BufferedImage.class);
    }

    /**
     * Liefert das Value des Keys als ImageIcon.<br>
     * Format:
     *
     * <pre>
     * openIcon = myOpenIcon.png
     * </pre>
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return {@link ImageIcon}
     * @see #getObject
     */
    public default ImageIcon getImageIcon(final String key)
    {
        return getObject(key, ImageIcon.class);
    }

    /**
     * Liefert das Value des Keys als Insets.
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return {@link Insets}
     */
    public default Insets getInsets(final String key)
    {
        return getObject(key, Insets.class);
    }

    /**
     * Liefert das Value des Keys als Integer.
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return Integer
     * @see #getObject
     */
    public default Integer getInteger(final String key)
    {
        return getObject(key, Integer.class);
    }

    /**
     * Liefert das Value des Keys als Integer.
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return Integer
     * @see #getObject
     */
    public default Integer getKeyCode(final String key)
    {
        KeyStroke ks = getKeyStroke(key);

        return (ks != null) ? Integer.valueOf(ks.getKeyCode()) : null;
    }

    /**
     * Liefert alle Keys der {@link ResourceMap}.<br>
     * Die Bundles werden, wenn noch nicht vorhanden, geladen.
     *
     * @return {@link Set}
     */
    public Set<String> getKeys();

    /**
     * Liefert das Value des Keys als KeyStroke.
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return {@link KeyStroke}
     * @see #getObject
     * @see KeyStroke#getKeyStroke
     */
    public default KeyStroke getKeyStroke(final String key)
    {
        return getObject(key, KeyStroke.class);
    }

    /**
     * Liefert das Value des Keys als Long.
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return Long
     * @see #getObject
     */
    public default Long getLong(final String key)
    {
        return getObject(key, Long.class);
    }

    /**
     * Liefert das konvertierte Objekt aus dem String.<br>
     * Das Objekt wird durch einen {@link ResourceConverter} erzeugt, der mit dem Klassentyp verknüpft ist.<br>
     *
     * @param <T> Objeckttyp
     * @param key String
     * @param type resource type
     * @throws LookupException wenn kein Converter für Typ, String konvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return Object
     */
    public <T> T getObject(final String key, final Class<T> type);

    /**
     * Liefert das Value des Keys als Point.
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return {@link Point}
     */
    public default Point getPoint(final String key)
    {
        return getObject(key, Point.class);
    }

    /**
     * Liefert das Value des Keys als Rectangle.
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return {@link Rectangle}
     */
    public default Rectangle getRectangle(final String key)
    {
        return getObject(key, Rectangle.class);
    }

    /**
     * Liefert den {@link ResourceConverter} für den Typ.<br>
     * Sollte diese ResourceMap keinen passenden Converter enthalten, wird der Parent befraget, wenn vorhanden.
     *
     * @param type {@link Class}
     * @return {@link ResourceConverter}
     */
    public ResourceConverter<?> getResourceConverter(final Class<?> type);

    /**
     * Liefert den verwendeten {@link ResourceProvider}.<br>
     * Sollte diese ResourceMap keinen eigenen ResourceProvider besitzen, wird der vorhandene Parent befragt.
     *
     * @return {@link ResourceProvider}
     */
    public ResourceProvider getResourceProvider();

    /**
     * Liefert das Value des Keys als Shorts.
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return Short
     * @see #getObject
     */
    public default Short getShort(final String key)
    {
        return getObject(key, Short.class);
    }

    /**
     * Liefert den Text für einen EnumTyp und den konkreten Wert.<br>
     * Format PropertyKey: enum.ENUMKLASSE.ENUMNAME
     *
     * @param enumValue {@link Enum}
     * @return {@link String}
     */
    public default String getString(final Enum<?> enumValue)
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
     * Für Angabe einen Stringformats kann die ältere {} Notation oder die Java 1.5 Variante verwendet werden:
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
     * @throws LookupException wenn kein Converter für Typ, String konvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return String
     * @see #getObject
     * @see String#format(String, Object...)
     * @see MessageFormat#format(String, Object...)
     */
    public String getString(final String key, final Object...args);

    /**
     * Liefert das Value des Keys als URI.
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return {@link URI}
     */
    public default URI getURI(final String key)
    {
        return getObject(key, URI.class);
    }

    /**
     * Liefert das Value des Keys als URL.
     *
     * @param key String
     * @throws LookupException wenn kein Converter für Typ, Stringkonvertierung fehlgeschlagen oder Wert nicht gefunden
     * @throws IllegalArgumentException wenn key null ist
     * @return {@link URL}
     */
    public default URL getURL(final String key)
    {
        return getObject(key, URL.class);
    }

    /**
     * Setzt den Parent der ResourceMap.
     *
     * @param parent {@link ResourceMap} optional
     */
    public void setParent(ResourceMap parent);
}