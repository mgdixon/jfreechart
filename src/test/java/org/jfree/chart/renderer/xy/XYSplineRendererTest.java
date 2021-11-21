/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2021, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * -------------------------
 * XYSplineRendererTest.java
 * -------------------------
 * (C) Copyright 2007-2021, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 */

package org.jfree.chart.renderer.xy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.api.RectangleInsets;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.TestUtils;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.GradientPaintTransformType;
import org.jfree.chart.util.StandardGradientPaintTransformer;
import org.jfree.chart.internal.CloneUtils;
import org.jfree.chart.api.PublicCloneable;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeTableXYDataset;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link XYSplineRenderer} class.
 */
public class XYSplineRendererTest {

    /**
     * Test that the equals() method distinguishes all fields.
     */
    @Test
    public void testEquals() {
        XYSplineRenderer r1 = new XYSplineRenderer();
        XYSplineRenderer r2 = new XYSplineRenderer();
        assertEquals(r1, r2);
        assertEquals(r2, r1);

        r1.setPrecision(9);
        assertFalse(r1.equals(r2));
        r2.setPrecision(9);
        assertTrue(r1.equals(r2));
        
        r1.setFillType(XYSplineRenderer.FillType.TO_ZERO);
        assertFalse(r1.equals(r2));
        r2.setFillType(XYSplineRenderer.FillType.TO_ZERO);
        assertTrue(r1.equals(r2));
        
        r1.setGradientPaintTransformer(null);
        assertFalse(r1.equals(r2));
        r2.setGradientPaintTransformer(null);
        assertTrue(r1.equals(r2));
        
        r1.setGradientPaintTransformer(new StandardGradientPaintTransformer(
                GradientPaintTransformType.HORIZONTAL));
        assertFalse(r1.equals(r2));
        r2.setGradientPaintTransformer(new StandardGradientPaintTransformer(
                GradientPaintTransformType.HORIZONTAL));
        assertTrue(r1.equals(r2));
    }

    /**
     * Two objects that are equal are required to return the same hashCode.
     */
    @Test
    public void testHashcode() {
        XYSplineRenderer r1 = new XYSplineRenderer();
        XYSplineRenderer r2 = new XYSplineRenderer();
        assertTrue(r1.equals(r2));
        int h1 = r1.hashCode();
        int h2 = r2.hashCode();
        assertEquals(h1, h2);
    }

    /**
     * Confirm that cloning works.
     */
    @Test
    public void testCloning() throws CloneNotSupportedException {
        Rectangle2D legendShape = new Rectangle2D.Double(1.0, 2.0, 3.0, 4.0);
        XYSplineRenderer r1 = new XYSplineRenderer();
        r1.setLegendLine(legendShape);
        XYSplineRenderer r2 = CloneUtils.clone(r1);
        assertTrue(r1 != r2);
        assertTrue(r1.getClass() == r2.getClass());
        assertTrue(r1.equals(r2));
    }

    /**
     * Verify that this class implements {@link PublicCloneable}.
     */
    @Test
    public void testPublicCloneable() {
        XYSplineRenderer r1 = new XYSplineRenderer();
        assertTrue(r1 instanceof PublicCloneable);
    }

    /**
     * Verify that the constructor that uses a Precision works
     * {@link XYSplineRenderer} class specifically
     * {@link XYSplineRenderer#XYSplineRenderer(int precision)}
     */
    @Test
    public void testPrecisionConstructor() {
        XYSplineRenderer r1 = new XYSplineRenderer(15);
        assertEquals(15, r1.getPrecision());
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    @Test
    public void testSerialization() {
        XYSplineRenderer r1 = new XYSplineRenderer();
        XYSplineRenderer r2 = TestUtils.serialised(r1);
        assertEquals(r1, r2);
    }

    /**
     * Tests for bug 210 where spline values exceed the axis in the
     * {@link XYSplineRenderer} class
     */
    @Test
    public void testBug210() {
        TimeSeries s1 = new TimeSeries("L&G European Index Trust");
        s1.add(new Month(2, 2001), 390.95);
        s1.add(new Month(3, 2001), 371.80);
        s1.add(new Month(4, 2001), 413.92);
        s1.add(new Month(5, 2001), 321.47);
        s1.add(new Month(6, 2001), 258.96);
        s1.add(new Month(7, 2001), 197.32);
        s1.add(new Month(8, 2001), 173.96);
        s1.add(new Month(9, 2001), 488.99);
        s1.add(new Month(10, 2001), 247.73);
        s1.add(new Month(11, 2001), 454.94);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);

        JFreeChart chart = ChartFactory.createTimeSeriesChart("A Title", "X", "Y", dataset, false, false, false);
        XYSplineRenderer r = new XYSplineRenderer();

        chart.setBackgroundPaint(Color.WHITE);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        plot.setRenderer(r);

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
        ValueAxis yAxis = (ValueAxis) plot.getRangeAxis();
        assertEquals(127.142, yAxis.getLowerBound(), 1.0);
        assertEquals(583.062, yAxis.getUpperBound(), 1.0);

    }

}
