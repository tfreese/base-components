/**
 * 22.09.2006
 */
package de.freese.base.reports;

import de.freese.base.reports.layout.DefaultLayoutElement;
import de.freese.base.reports.layout.ILayoutElement;
import de.freese.base.reports.layout.LayoutPreviewFrame;
import de.freese.base.reports.layout.LineLayoutElement;

/**
 * Testklasse des Layouts.
 *
 * @author Thomas Freese
 */
public class TestLayout
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        // Hauptelement
        DefaultLayoutElement masterElement = new DefaultLayoutElement("Master");
        masterElement.setHeight(300);
        masterElement.setWidth(300);

        ILayoutElement layoutElement1 = new DefaultLayoutElement("Element 1");
        layoutElement1.setHeight(50);
        layoutElement1.setWidth(80);

        ILayoutElement layoutElement2 = new DefaultLayoutElement("Element 2");
        layoutElement2.setX(45);
        layoutElement2.setY(45);
        layoutElement2.setHeight(100);
        layoutElement2.setWidth(100);

        ILayoutElement layoutElement3 = new DefaultLayoutElement("Element 3");
        layoutElement3.setX(110);
        layoutElement3.setY(40);
        layoutElement3.setHeight(50);
        layoutElement3.setWidth(150);

        ILayoutElement layoutElement4 = new LineLayoutElement();
        layoutElement4.setX(100);
        layoutElement4.setY(200);
        layoutElement4.setWidth(150);

        // Elemente in Hauptelement zusammenfuehren
        masterElement.addElement(layoutElement1);
        masterElement.addElement(layoutElement2);
        masterElement.addElement(layoutElement3);
        masterElement.addElement(layoutElement4);

        LayoutPreviewFrame frame = new LayoutPreviewFrame(masterElement);

        // Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        //
        // int w = frame.getSize().width;
        // int h = frame.getSize().height;
        // int x = (dim.width - w) / 2;
        // int y = (dim.height - h) / 2;
        //
        // frame.setLocation(x, y);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }
}
