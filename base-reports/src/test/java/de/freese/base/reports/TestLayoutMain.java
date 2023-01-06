// Created: 22.09.2006
package de.freese.base.reports;

import de.freese.base.reports.layout.DefaultLayoutElement;
import de.freese.base.reports.layout.LayoutElement;
import de.freese.base.reports.layout.LayoutPreviewFrame;
import de.freese.base.reports.layout.LineLayoutElement;

/**
 * @author Thomas Freese
 */
public final class TestLayoutMain
{
    public static void main(final String[] args)
    {
        // Hauptelement
        DefaultLayoutElement masterElement = new DefaultLayoutElement("Master");
        masterElement.setHeight(300D);
        masterElement.setWidth(300D);

        LayoutElement layoutElement1 = new DefaultLayoutElement("Element 1");
        layoutElement1.setHeight(50D);
        layoutElement1.setWidth(80D);

        LayoutElement layoutElement2 = new DefaultLayoutElement("Element 2");
        layoutElement2.setX(45D);
        layoutElement2.setY(45D);
        layoutElement2.setHeight(100D);
        layoutElement2.setWidth(100D);

        LayoutElement layoutElement3 = new DefaultLayoutElement("Element 3");
        layoutElement3.setX(110D);
        layoutElement3.setY(40D);
        layoutElement3.setHeight(50D);
        layoutElement3.setWidth(150D);

        LayoutElement layoutElement4 = new LineLayoutElement();
        layoutElement4.setX(100D);
        layoutElement4.setY(200D);
        layoutElement4.setWidth(150D);

        // Elemente in Hauptelement zusammenführen
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

    private TestLayoutMain()
    {
        super();
    }
}
