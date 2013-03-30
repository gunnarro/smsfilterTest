package com.gunnarro.android.smsfilter.test;

import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.test.InstrumentationTestCase;

import com.gunnarro.android.smsfilter.AppPreferences;
import com.gunnarro.android.smsfilter.sms.SMS;
import com.gunnarro.android.smsfilter.sms.SMSReader;

public class SMSReaderTest extends InstrumentationTestCase {

    @Mock
    private AppPreferences appPreferencesMock;

    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    public void testGetBlockedSMSList() {
        this.init();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.MONTH, 4);
        cal.set(Calendar.DAY_OF_MONTH, 20);
        String time = Long.toString(cal.getTimeInMillis());
        String blockedList = time + ":45465500;" + time + ":45465500;" + time + ":45465500;" + time + ":45465500;" + time + ":45465501;" + time + ":45465501;"
                + time + ":45465501";
        when(appPreferencesMock.getListAsString(AppPreferences.SMS_BLOCKED_LOG)).thenReturn(blockedList.toString());
        SMSReader smsReader = new SMSReader();
        smsReader.setAppPreferences(appPreferencesMock);
        List<SMS> smsBlocked = smsReader.getSMSBlocked("number");
        System.out.println(smsBlocked.toString());
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        assertEquals(7, smsBlocked.size());
        assertEquals("20.05.2013", formatter.format(smsBlocked.get(0).getTimeMilliSecound()));
        assertEquals("45465500", smsBlocked.get(0).getNumber());
    }

}
