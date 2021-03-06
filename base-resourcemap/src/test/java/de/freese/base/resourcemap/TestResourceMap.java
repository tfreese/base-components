package de.freese.base.resourcemap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.base.resourcemap.provider.ResourceBundleProvider;

/**
 * Testklasse der ResourceMap.
 *
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestResourceMap
{
    /**
     *
     */
    private static ResourceMap resourceMap = null;

    /**
     *
     */
    @BeforeAll
    static void beforeAll()
    {
        ResourceMap parentRM = ResourceMapBuilder.create("parentTest").resourceProvider(new ResourceBundleProvider())
                .classLoader(TestResourceMap.class.getClassLoader()).build();

        resourceMap = ResourceMapBuilder.create("resourcemap/test").parent(parentRM).build();
    }

    /**
     *
     */
    @Test
    void testBoolean()
    {
        Boolean value = resourceMap.getBoolean("test.boolean");

        assertNotNull(value);
        assertEquals(Boolean.FALSE, value);
    }

    /**
     *
     */
    @Test
    void testByte()
    {
        Byte value = resourceMap.getByte("test.byte");

        assertNotNull(value);
        assertEquals(Byte.decode("1"), value);
    }

    /**
     *
     */
    @Test
    void testColor()
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
    void testColorAlpha()
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
    void testColorHex()
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
    void testDimension()
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
    void testDouble()
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
    void testEmptyBorder()
    {
        EmptyBorder value = resourceMap.getEmptyBorder("test.emptyborder");

        EmptyBorder ref = new EmptyBorder(5, 5, 5, 5);

        assertNotNull(value);
        assertEquals(ref.getBorderInsets(), value.getBorderInsets());
    }

    /**
     * Gegeben sind die folgenden Resourcen:
     *
     * <pre>
     *  hello = Hello
     *  world = World
     *  place = ${hello} ${world}
     * </pre>
     *
     * Der Wert von evaluateStringExpression("place") wäre "Hello World". Der Wert von einem ${null} ist null.
     */
    @Test
    void testEvaluateStringExpression()
    {
        String expression = "${hello} in my ${world} guest";

        // Split
        if (expression.contains("${"))
        {
            List<String> keys = new ArrayList<>();

            String[] splits = expression.split("\\$\\{");
            // System.out.println(Arrays.toString(splits));

            for (String split : splits)
            {
                int lastIndex = split.lastIndexOf("}");

                if (lastIndex <= 0)
                {
                    continue;
                }

                String key = split.substring(0, lastIndex);
                keys.add(key);
            }

            // System.out.println(keys);
            assertLinesMatch(List.of("hello", "world"), keys);
        }

        // Iteration
        if (expression.contains("${"))
        {
            List<String> keys = new ArrayList<>();
            int startIndex = 0;
            int lastEndIndex = 0;

            while ((startIndex = expression.indexOf("${", lastEndIndex)) != -1)
            {
                int endIndex = expression.indexOf("}", startIndex);

                if (endIndex != -1)
                {
                    String key = expression.substring(startIndex + 2, endIndex);
                    keys.add(key);

                    lastEndIndex = endIndex;
                }
            }

            // System.out.println(keys);
            assertLinesMatch(List.of("hello", "world"), keys);
        }
    }

    /**
     *
     */
    @Test
    void testFloat()
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
    void testFont()
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
    void testIcon()
    {
        Icon value = resourceMap.getIcon("test.icon");

        URL url = ClassLoader.getSystemResource("icons/next.png");
        ImageIcon ref = new ImageIcon(url);

        assertNotNull(value);
        assertNotNull(ref);
        assertEquals(ref.getIconHeight(), value.getIconHeight());
        assertEquals(ref.getIconWidth(), value.getIconWidth());
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    @Test
    void testImage() throws IOException
    {
        Image value = resourceMap.getImage("test.icon");

        URL url = ClassLoader.getSystemResource("icons/next.png");
        BufferedImage ref = ImageIO.read(url);

        assertNotNull(value);
        assertNotNull(ref);
    }

    /**
     * @see TestResourceMap#testIcon()
     */
    @Test
    void testImageIcon()
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
    void testInsets()
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
    void testInteger()
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
    void testKeyCode()
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
    void testKeyStroke()
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
    void testLong()
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
    void testParentString()
    {
        String value = resourceMap.getString("test.parent.version");

        assertNotNull(value);
        assertEquals("Parent Value", value);
    }

    /**
     *
     */
    @Test
    void testPoint()
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
    void testRectangle()
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
    void testShort()
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
    void testString1()
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
    void testString2()
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
    void testString3()
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
    void testString4()
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
    void testStringEmpty()
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
    void testURI() throws Exception
    {
        URI value = resourceMap.getURI("test.uri");

        URI ref = new URI("http://www.google.de");

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testURL() throws Exception
    {
        URL value = resourceMap.getURL("test.url");

        URL ref = new URL("http://www.google.de");

        assertNotNull(value);
        assertEquals(ref, value);
    }

    /**
    *
    */
    @Test
    void testWrongKey()
    {
        String value = resourceMap.getString("not.exist");

        assertNull(value);
        // assertEquals(ref, value);
    }
}
