package de.freese.base.resourcemap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.net.URI;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Testklasse der ResourceMap.
 *
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TestResourceMap
{
    /**
     *
     */
    private static IResourceMap resourceMap = null;

    /**
     *
     */
    @BeforeAll
    public static void beforeAll()
    {
        ResourceMap parentRM = new ResourceMap();
        parentRM.setBaseName("parentTest");

        resourceMap = new ResourceMap();
        resourceMap.setBaseName("resourcemap/test");
        resourceMap.setParent(parentRM);
    }

    /**
     * Erstellt ein neues {@link TestResourceMap} Object.
     */
    public TestResourceMap()
    {
        super();
    }

    /**
     *
     */
    @Test
    public void testBoolean()
    {
        Boolean value = resourceMap.getBoolean("test.boolean");

        assertNotNull(value);
        assertEquals(Boolean.FALSE, value);
    }

    /**
     *
     */
    @Test
    public void testBooleanNotExist()
    {
        try
        {
            Boolean value = resourceMap.getBoolean("test.boolean.notexsist");

            assertNull(value);
        }
        catch (Exception ex)
        {
            assertTrue(ex instanceof LookupException);
        }
    }

    /**
     *
     */
    @Test
    public void testByte()
    {
        Byte value = resourceMap.getByte("test.byte");

        assertNotNull(value);
        assertEquals(Byte.decode("1"), value);
    }

    /**
     *
     */
    @Test
    public void testColor()
    {
        Color value = resourceMap.getColor("test.color");

        Color ref = new Color(200, 200, 200);

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testColorAlpha()
    {
        Color value = resourceMap.getColor("test.colorAlpha");

        String key = "#20101010";
        int alpha = Integer.decode(key.substring(0, 3)).intValue();
        int rgb = Integer.decode("#" + key.substring(3)).intValue();
        Color ref = new Color((alpha << 24) | rgb, true);

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testColorHex()
    {
        Color value = resourceMap.getColor("test.colorHex");

        Color ref = Color.decode("#101010");

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testDimension()
    {
        Dimension value = resourceMap.getDimension("test.dimension");

        Dimension ref = new Dimension(100, 200);

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testDouble()
    {
        Double value = resourceMap.getDouble("test.double");

        Double ref = Double.valueOf(99.99D);

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testEmptyBorder()
    {
        EmptyBorder value = resourceMap.getEmptyBorder("test.emptyborder");

        EmptyBorder ref = new EmptyBorder(5, 5, 5, 5);

        assertNotNull(value);
        assertEquals(ref.getBorderInsets(), value.getBorderInsets());
    }

    /**
     *
     */
    @Test
    public void testFloat()
    {
        Float value = resourceMap.getFloat("test.float");

        Float ref = Float.valueOf(99.99F);

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testFont()
    {
        Font value = resourceMap.getFont("test.font");

        Font ref = Font.decode("Arial-PLAIN-12");

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     * @see TestResourceMap#testImageIcon()
     */
    @Test
    public void testIcon()
    {
        Icon value = resourceMap.getIcon("test.icon");

        assertNotNull(value);
    }

    /**
     * @see TestResourceMap#testIcon()
     */
    @Test
    public void testImageIcon()
    {
        ImageIcon value = resourceMap.getImageIcon("test.icon");

        URL url = ClassLoader.getSystemResource("icons/next.png");
        ImageIcon ref = new ImageIcon(url);

        assertNotNull(value);
        assertEquals(ref.getDescription(), value.getDescription());
    }

    /**
     *
     */
    @Test
    public void testInsets()
    {
        Insets value = resourceMap.getInsets("test.insets");

        Insets ref = new Insets(5, 5, 5, 5);

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testInteger()
    {
        Integer value = resourceMap.getInteger("test.integer");

        Integer ref = Integer.valueOf(1);

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testKeyCode()
    {
        Integer value = resourceMap.getKeyCode("test.keycode");

        Integer ref = Integer.valueOf(KeyStroke.getKeyStroke("control T").getKeyCode());

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testKeyStroke()
    {
        KeyStroke value = resourceMap.getKeyStroke("test.keystroke");

        KeyStroke ref = KeyStroke.getKeyStroke("control T");

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testLong()
    {
        Long value = resourceMap.getLong("test.long");

        Long ref = Long.valueOf(1L);

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testParentString()
    {
        String value = resourceMap.getString("test.parent.version");

        assertNotNull(value);
        assertEquals("Parent Value", value);
    }

    /**
     *
     */
    @Test
    public void testPoint()
    {
        Point value = resourceMap.getPoint("test.point");

        Point ref = new Point(100, 200);

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testRectangle()
    {
        Rectangle value = resourceMap.getRectangle("test.rectangle");

        Rectangle ref = new Rectangle(5, 5, 5, 5);

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testShort()
    {
        Short value = resourceMap.getShort("test.short");

        short i = 1;
        Short ref = Short.valueOf(i);

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testString1()
    {
        String value = resourceMap.getString("test.string.1");

        String ref = "Taeschtd";

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testString2()
    {
        String value = resourceMap.getString("test.string.2", "ist", "Test");

        String ref = "Dies ist ein Test.";

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testString3()
    {
        String value = resourceMap.getString("test.string.3", "ist", "Test");

        String ref = "Dies ist ein Test.";

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testString4()
    {
        String value = resourceMap.getString("test.string.4");

        String ref = "Dies ist ein Taeschtd.";

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     *
     */
    @Test
    public void testStringEmpty()
    {
        String value = resourceMap.getString("test.string.empty");

        String ref = "";

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testURI() throws Exception
    {
        URI value = resourceMap.getURI("test.uri");

        URI ref = new URI("http://www.javadesktop.org");

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void testURL() throws Exception
    {
        URL value = resourceMap.getURL("test.url");

        URL ref = new URL("http://www.javadesktop.org");

        assertNotNull(value);
        assertEquals(ref, value);
    }
}
