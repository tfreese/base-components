package de.freese.base.resourcemap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import de.freese.base.resourcemap.converter.ResourceConverter;
import de.freese.base.resourcemap.converter.ResourceConverters;
import de.freese.base.resourcemap.provider.ResourceBundleProvider;

/**
 * Testklasse der ResourceMap.
 *
 * @author Thomas Freese
 */
@Execution(ExecutionMode.CONCURRENT)
class TestResourceMap {
    private static ResourceMap resourceMap;

    @BeforeAll
    static void beforeAll() {
        // @formatter:off
        final ResourceMap resourceMapRoot = ResourceMapBuilder.create()
                .resourceProvider(new ResourceBundleProvider())
                //.converter(..., ...)
                .cacheDisabled()
                .bundleName("parentTest")
                .addChild()
                    .bundleName("bundles/test1")
                    .addChild()
                        .bundleName("bundles/test2")
                        .cacheDisabled()
                        .done()
                    .done()
                .build()
                ;
        // @formatter:on

        resourceMapRoot.load(Locale.getDefault());

        resourceMap = resourceMapRoot.getChild("bundles/test2");
    }

    @Test
    void testBoolean() {
        final Boolean value = resourceMap.getBoolean("test.boolean");

        assertNotNull(value);
        assertEquals(Boolean.FALSE, value);
    }

    @Test
    void testByte() {
        final Byte value = resourceMap.getByte("test.byte");

        assertNotNull(value);
        assertEquals(Byte.decode("1"), value);
    }

    @Test
    void testColor() {
        final Color value = resourceMap.getColor("test.color");

        final Color ref = new Color(200, 200, 200);

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testColorAlpha() {
        final Color value = resourceMap.getColor("test.colorAlpha");

        final String key = "#20101010";
        final int alpha = Integer.decode(key.substring(0, 3));
        final int rgb = Integer.decode("#" + key.substring(3));
        final Color ref = new Color((alpha << 24) | rgb, true);

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testColorHex() {
        final Color value = resourceMap.getColor("test.colorHex");

        final Color ref = Color.decode("#101010");

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testDimension() {
        final Dimension value = resourceMap.getDimension("test.dimension");

        final Dimension ref = new Dimension(100, 200);

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testDouble() {
        final Double value = resourceMap.getDouble("test.double");

        final Double ref = 99.99D;

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testEmptyBorder() {
        final EmptyBorder value = resourceMap.getEmptyBorder("test.emptyborder");

        final EmptyBorder ref = new EmptyBorder(5, 5, 5, 5);

        assertNotNull(value);
        assertEquals(ref.getBorderInsets(), value.getBorderInsets());
    }

    /**
     * Gegeben sind die folgenden Ressourcen:
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
    void testEvaluateStringExpression() {
        final String expression = "${hello} in my ${world} guest";

        // Split
        if (expression.contains("${")) {
            final List<String> keys = new ArrayList<>();

            final String[] splits = expression.split("\\$\\{");
            // System.out.println(Arrays.toString(splits));

            for (String split : splits) {
                final int lastIndex = split.lastIndexOf("}");

                if (lastIndex <= 0) {
                    continue;
                }

                final String key = split.substring(0, lastIndex);
                keys.add(key);
            }

            // System.out.println(keys);
            assertLinesMatch(List.of("hello", "world"), keys);
        }

        // Iteration
        if (expression.contains("${")) {
            final List<String> keys = new ArrayList<>();
            int startIndex = 0;
            int lastEndIndex = 0;

            while ((startIndex = expression.indexOf("${", lastEndIndex)) != -1) {
                final int endIndex = expression.indexOf("}", startIndex);

                if (endIndex != -1) {
                    final String key = expression.substring(startIndex + 2, endIndex);
                    keys.add(key);

                    lastEndIndex = endIndex;
                }
            }

            // System.out.println(keys);
            assertLinesMatch(List.of("hello", "world"), keys);
        }
    }

    @Test
    void testFloat() {
        final Float value = resourceMap.getFloat("test.float");

        final Float ref = 99.99F;

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testFont() {
        final Font value = resourceMap.getFont("test.font");

        final Font ref = Font.decode("Arial-PLAIN-12");

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testIcon() {
        final Icon value = resourceMap.getIcon("test.icon");

        final URL url = ClassLoader.getSystemResource("icons/next.png");
        final ImageIcon ref = new ImageIcon(url);

        assertNotNull(value);
        assertNotNull(ref);
        assertEquals(ref.getIconHeight(), value.getIconHeight());
        assertEquals(ref.getIconWidth(), value.getIconWidth());
    }

    @Test
    void testImage() throws IOException {
        final Image value = resourceMap.getImage("test.icon");

        final URL url = ClassLoader.getSystemResource("icons/next.png");
        final BufferedImage ref = ImageIO.read(url);

        assertNotNull(value);
        assertNotNull(ref);
    }

    @Test
    void testImageIcon() {
        final ImageIcon value = resourceMap.getImageIcon("test.icon");

        final URL url = ClassLoader.getSystemResource("icons/next.png");
        final ImageIcon ref = new ImageIcon(url);

        assertNotNull(value);
        assertEquals(ref.getDescription(), value.getDescription());
    }

    @Test
    void testInsets() {
        final Insets value = resourceMap.getInsets("test.insets");

        final Insets ref = new Insets(5, 5, 5, 5);

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testInteger() {
        final Integer value = resourceMap.getInteger("test.integer");

        final Integer ref = 1;

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testKeyCode() {
        final Integer value = resourceMap.getKeyCode("test.keycode");

        final Integer ref = KeyStroke.getKeyStroke("control T").getKeyCode();

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testKeyStroke() {
        final KeyStroke value = resourceMap.getKeyStroke("test.keystroke");

        final KeyStroke ref = KeyStroke.getKeyStroke("control T");

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testLong() {
        final Long value = resourceMap.getLong("test.long");

        final Long ref = 1L;

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testNotExist() {
        final String value = resourceMap.getString("not.exist");

        assertEquals("#not.exist", value);
    }

    @Test
    void testParentString() {
        final String value = resourceMap.getString("test.parent.version");

        assertNotNull(value);
        assertEquals("Parent Value", value);
    }

    @Test
    void testPoint() {
        final Point value = resourceMap.getPoint("test.point");

        final Point ref = new Point(100, 200);

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testRectangle() {
        final Rectangle value = resourceMap.getRectangle("test.rectangle");

        final Rectangle ref = new Rectangle(5, 5, 5, 5);

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testResourceConverters() {
        final ResourceConverters resourceConverters = ResourceConverters.ofDefaults();
        //        resourceConverters.customize(converters -> converters.put(int.class, new IntegerResourceConverter()));

        final ResourceConverter<Integer> resourceConverter = resourceConverters.getConverter(int.class);

        assertNotNull(resourceConverter);
    }

    @Test
    void testShort() {
        final Short value = resourceMap.getShort("test.short");

        final Short ref = 1;

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testString1() {
        final String value = resourceMap.getString("test.string.1");

        final String ref = "Taeschtd";

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testString2() {
        final String value = resourceMap.getString("test.string.2", "ist", "Test");

        final String ref = "Dies ist ein Test.";

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testString3() {
        final String value = resourceMap.getString("test.string.3", "ist", "Test");

        final String ref = "Dies ist ein Test.";

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testString4() {
        final String value = resourceMap.getString("test.string.4");

        final String ref = "Dies ist ein Taeschtd.";

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testStringEmpty() {
        final String value = resourceMap.getString("test.string.empty");

        final String ref = "";

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testUri() throws Exception {
        final URI value = resourceMap.getURI("test.uri");

        final URI ref = new URI("http://www.google.de");

        assertNotNull(value);
        assertEquals(ref, value);
    }

    @Test
    void testUrl() throws Exception {
        final URL value = resourceMap.getURL("test.url");

        final URL ref = new URI("http://www.google.de").toURL();

        assertNotNull(value);
        assertEquals(ref, value);
    }
}
