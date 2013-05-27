package com.gunnarro.android.smsfilter.test;

import java.util.Calendar;

import junit.framework.TestCase;

import com.gunnarro.android.smsfilter.domain.Filter;

public class FilterTest extends TestCase {

    public void testIsActvated() {
        Filter filter = new Filter("blackList", true);
        assertTrue(filter.isActivated());
        Calendar fromTime = Calendar.getInstance();
        fromTime.set(Calendar.HOUR_OF_DAY, 9);
        fromTime.set(Calendar.MINUTE, 0);
        filter.setFromTime(fromTime);
        Calendar toTime = Calendar.getInstance();
        toTime.set(Calendar.HOUR_OF_DAY, 20);
        toTime.set(Calendar.MINUTE, 0);
        filter.setToTime(toTime);
        assertFalse(filter.isActivated());
    }

}
