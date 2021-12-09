/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2020, by Object Refinery Limited and Contributors.
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
 * ----------------------
 * XYTaskDatasetTest.java
 * ----------------------
 * (C) Copyright 2008-2020, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 */

package org.jfree.data.gantt;

import java.lang.reflect.Field;
import java.util.Date;

import org.jfree.chart.TestUtils;
import org.jfree.chart.internal.CloneUtils;

import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.xy.YIntervalSeries;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the {@link XYTaskDataset} class.
 */
public class XYTaskDatasetTest implements DatasetChangeListener {

    DatasetChangeEvent lastEvent;

    //CS427 Issue Link: https://github.com/jfree/jfreechart/issues/249

    /**
     * Records the last event.
     *
     * @param event the event.
     */
    @Override
    public void datasetChanged(DatasetChangeEvent event) {
        this.lastEvent = event;
    }

    /**
     * Some checks for the equals() method.
     */
    @Test
    public void testEquals() {
        TaskSeries<String> s1 = new TaskSeries<>("Series");
        s1.add(new Task("Task 1", new Date(0L), new Date(1L)));
        s1.add(new Task("Task 2", new Date(10L), new Date(11L)));
        s1.add(new Task("Task 3", new Date(20L), new Date(21L)));
        TaskSeriesCollection<String, String> u1 = new TaskSeriesCollection<>();
        u1.add(s1);
        XYTaskDataset d1 = new XYTaskDataset(u1);
        TaskSeries<String> s2 = new TaskSeries<>("Series");
        s2.add(new Task("Task 1", new Date(0L), new Date(1L)));
        s2.add(new Task("Task 2", new Date(10L), new Date(11L)));
        s2.add(new Task("Task 3", new Date(20L), new Date(21L)));
        TaskSeriesCollection<String, String> u2 = new TaskSeriesCollection<>();
        u2.add(s2);
        XYTaskDataset d2 = new XYTaskDataset(u2);
        assertTrue(d1.equals(d2));

        d1.setSeriesWidth(0.123);
        assertFalse(d1.equals(d2));
        d2.setSeriesWidth(0.123);
        assertTrue(d1.equals(d2));

        d1.setTransposed(true);
        assertFalse(d1.equals(d2));
        d2.setTransposed(true);
        assertTrue(d1.equals(d2));

        s1.add(new Task("Task 2", new Date(10L), new Date(11L)));
        assertFalse(d1.equals(d2));
        s2.add(new Task("Task 2", new Date(10L), new Date(11L)));
        assertTrue(d1.equals(d2));
    }

    /**
     * Confirm that cloning works.
     */
    @Test
    public void testCloning() throws CloneNotSupportedException {
        TaskSeries<String> s1 = new TaskSeries<>("Series");
        s1.add(new Task("Task 1", new Date(0L), new Date(1L)));
        TaskSeriesCollection<String, String> u1 = new TaskSeriesCollection<>();
        u1.add(s1);
        XYTaskDataset d1 = new XYTaskDataset(u1);
        XYTaskDataset d2 = CloneUtils.clone(d1);
        assertTrue(d1 != d2);
        assertTrue(d1.getClass() == d2.getClass());
        assertTrue(d1.equals(d2));

        // basic check for independence
        s1.add(new Task("Task 2", new Date(10L), new Date(11L)));
        assertFalse(d1.equals(d2));
        TaskSeriesCollection<String, String> u2 = d2.getTasks();
        TaskSeries<String> s2 = u2.getSeries("Series");
        s2.add(new Task("Task 2", new Date(10L), new Date(11L)));
        assertTrue(d1.equals(d2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    @Test
    public void testSerialization() {
        TaskSeries<String> s1 = new TaskSeries<>("Series");
        s1.add(new Task("Task 1", new Date(0L), new Date(1L)));
        TaskSeriesCollection<String, String> u1 = new TaskSeriesCollection<>();
        u1.add(s1);
        XYTaskDataset d1 = new XYTaskDataset(u1);
        XYTaskDataset d2 = TestUtils.serialised(d1);
        assertEquals(d1, d2);

        // basic check for independence
        s1.add(new Task("Task 2", new Date(10L), new Date(11L)));
        assertFalse(d1.equals(d2));
        TaskSeriesCollection<String, String> u2 = d2.getTasks();
        TaskSeries<String> s2 = u2.getSeries("Series");
        s2.add(new Task("Task 2", new Date(10L), new Date(11L)));
        assertTrue(d1.equals(d2));
    }

    //CS427 Issue Link: https://github.com/jfree/jfreechart/issues/249

    /**
     * issue_249 was a bug where EventListenerList was not declared as transient.
     * when the class was serialized and then deserialized it had problems
     * this test just makes sure that the serialized version ends up with a
     * listener list.
     */
    @Test
    public void testBug_issue_249() {
        TaskSeries<String> s1 = new TaskSeries<>("Series");
        TaskSeriesCollection<String, String> u1 = new TaskSeriesCollection<>();
        u1.add(s1);

        // test basic serialization
        XYTaskDataset d1 = new XYTaskDataset(u1);
        XYTaskDataset d2 = TestUtils.serialised(d1);
        assertEquals(d1, d2);

        // make this class a listener
        d2.addChangeListener(this);
        assertNull(this.lastEvent);
        assertTrue(s1.isEmpty());

        // add a data item to the series
        s1.add(new Task("Task 1", new Date(0L), new Date(1L)));
        assertFalse(s1.isEmpty());

        // add an item to the tasks (this will fail if serialization didn't work)
        TaskSeriesCollection<String, String> u2 = d2.getTasks();
        TaskSeries<String> s2 = u2.getSeries("Series");
        s2.add(new Task("Task 2", new Date(10L), new Date(11L)));
        assertFalse(d1.equals(d2));


    }


    //CS427 Issue Link: https://github.com/jfree/jfreechart/issues/249

    
    /**
     * this tests to make sure that after a serialization that the
     * eventlisteners list is not null (issue 249). it uses reflection
     * and the fact that this is a series to find the field.
     */
    @Test
    public void testBug_issue_249_via_reflection() {
        TaskSeries<String> s1 = new TaskSeries<>("Series");
        TaskSeriesCollection<String, String> u1 = new TaskSeriesCollection<>();
        u1.add(s1);

        // test basic serialization
        XYTaskDataset d1 = new XYTaskDataset(u1);
        XYTaskDataset d2 = TestUtils.serialised(d1);
        assertEquals(d1, d2);

        // reflection is typically not suggested in junit tests
        // uses reflection to get the listeners field and make sure it's not
        // null
        try {
            Class s1class = s1.getClass().getSuperclass();
            Field myfield = s1class.getDeclaredField("listeners");
            myfield.setAccessible(true);
            Object value = myfield.get(s1);
            assertNotNull(value);
        } catch (NoSuchFieldException e) {
            fail("was looking for listeners in Series in reflection and failed");
        } catch (IllegalAccessException e) {
            fail("reflection failed");//
        }
    }
}
