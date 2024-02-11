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
        Boolean value = resourceMap.getBoolean("test.boolean");
        assertNotNull(value);
        assertEquals(Boolean.FALSE, value);

        value = resourceMap.getBoolean("test.booleann", Boolean.TRUE);
        assertNotNull(value);
        assertEquals(Boolean.TRUE, value);
    }

    @Test
    void testByte() {
        Byte value = resourceMap.getByte("test.byte");
        assertNotNull(value);
        assertEquals(Byte.decode("1"), value);

        value = resourceMap.getByte("test.bytee", Byte.decode("-1"));
        assertNotNull(value);
        assertEquals(Byte.decode("-1"), value);
    }

    @Test
    void testColor() {
        final Color value = resourceMap.getColor("test.color");

        assertNotNull(value);
        assertEquals(new Color(200, 200, 200), value);
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

        assertNotNull(value);
        assertEquals(Color.decode("#101010"), value);
    }

    @Test
    void testDimension() {
        final Dimension value = resourceMap.getDimension("test.dimension");

        assertNotNull(value);
        assertEquals(new Dimension(100, 200), value);
    }

    @Test
    void testDouble() {
        Double value = resourceMap.getDouble("test.double");
        assertNotNull(value);
        assertEquals(99.99D, value);

        value = resourceMap.getDouble("test.doublee", -1D);
        assertEquals(-1D, value);
    }

    @Test
    void testEmptyBorder() {
        final EmptyBorder value = resourceMap.getEmptyBorder("test.emptyborder");

        assertNotNull(value);
        assertEquals(new EmptyBorder(5, 5, 5, 5).getBorderInsets(), value.getBorderInsets());
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
        Float value = resourceMap.getFloat("test.float");
        assertNotNull(value);
        assertEquals(99.99F, value);

        value = resourceMap.getFloat("test.floatt", -1F);
        assertNotNull(value);
        assertEquals(-1F, value);
    }

    @Test
    void testFont() {
        final Font value = resourceMap.getFont("test.font");

        assertNotNull(value);
        assertEquals(Font.decode("Arial-PLAIN-12"), value);
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

        assertNotNull(value);
        assertEquals(new Insets(5, 5, 5, 5), value);
    }

    @Test
    void testInteger() {
        Integer value = resourceMap.getInteger("test.integer");
        assertNotNull(value);
        assertEquals(1, value);

        value = resourceMap.getInteger("test.integerr", -1);
        assertNotNull(value);
        assertEquals(-1, value);
    }

    @Test
    void testKeyCode() {
        final Integer value = resourceMap.getKeyCode("test.keycode");

        assertNotNull(value);
        assertEquals(KeyStroke.getKeyStroke("control T").getKeyCode(), value);
    }

    @Test
    void testKeyStroke() {
        final KeyStroke value = resourceMap.getKeyStroke("test.keystroke");

        assertNotNull(value);
        assertEquals(KeyStroke.getKeyStroke("control T"), value);
    }

    @Test
    void testLong() {
        Long value = resourceMap.getLong("test.long");
        assertNotNull(value);
        assertEquals(1L, value);

        value = resourceMap.getLong("test.longg", -1L);
        assertNotNull(value);
        assertEquals(-1L, value);
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

        assertNotNull(value);
        assertEquals(new Point(100, 200), value);
    }

    @Test
    void testRectangle() {
        final Rectangle value = resourceMap.getRectangle("test.rectangle");

        assertNotNull(value);
        assertEquals(new Rectangle(5, 5, 5, 5), value);
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
        Short value = resourceMap.getShort("test.short");
        assertNotNull(value);
        assertEquals((short) 1, value);

        value = resourceMap.getShort("test.shortt", (short) -1);
        assertNotNull(value);
        assertEquals((short) -1, value);
    }

    @Test
    void testString1() {
        final String value = resourceMap.getString("test.string.1");

        assertNotNull(value);
        assertEquals("Taeschtd", value);
    }

    @Test
    void testString2() {
        final String value = resourceMap.getString("test.string.2", "ist", "Test");

        assertNotNull(value);
        assertEquals("Dies ist ein Test.", value);
    }

    @Test
    void testString3() {
        final String value = resourceMap.getString("test.string.3", "ist", "Test");

        assertNotNull(value);
        assertEquals("Dies ist ein Test.", value);
    }

    @Test
    void testString4() {
        final String value = resourceMap.getString("test.string.4");

        assertNotNull(value);
        assertEquals("Dies ist ein Taeschtd.", value);
    }

    @Test
    void testStringEmpty() {
        final String value = resourceMap.getString("test.string.empty");

        assertNotNull(value);
        assertEquals("", value);
    }

    @Test
    void testUri() throws Exception {
        final URI value = resourceMap.getURI("test.uri");

        assertNotNull(value);
        assertEquals(new URI("http://www.google.de"), value);
    }

    @Test
    void testUrl() throws Exception {
        final URL value = resourceMap.getURL("test.url");

        assertNotNull(value);
        assertEquals(new URI("http://www.google.de").toURL(), value);
    }
}
