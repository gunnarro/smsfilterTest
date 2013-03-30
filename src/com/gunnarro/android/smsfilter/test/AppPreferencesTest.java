package com.gunnarro.android.smsfilter.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import junit.framework.TestCase;
import android.content.SharedPreferences;

import com.gunnarro.android.smsfilter.AppPreferences;
import com.gunnarro.android.smsfilter.ListAppPreferencesImpl;
import com.gunnarro.android.smsfilter.sms.SMS;

public class AppPreferencesTest extends TestCase {

    private SharedPreferences sharedPreferencesMock;
    private ListAppPreferencesImpl ListAppPreferences;

    public void init() {
        sharedPreferencesMock = mock(SharedPreferences.class);
        ListAppPreferences = new ListAppPreferencesImpl();
        ListAppPreferences.setAppSharedPrefs(sharedPreferencesMock);
    }

    public void testSMSBlockedlog() {
        init();
        System.out.println(Long.toString(System.currentTimeMillis()));
        String blockedListAsString = System.currentTimeMillis() + ":45465500;" + System.currentTimeMillis() + ":45465500;" + System.currentTimeMillis()
                + ":45465501";

        when(sharedPreferencesMock.getString(AppPreferences.SMS_BLOCKED_LOG, AppPreferences.DEFAULT_VALUE)).thenReturn(blockedListAsString);

        List<SMS> smsList = ListAppPreferences.getSMSList("number");
        assertEquals(2, smsList.size());
        assertEquals("45465500", smsList.get(0).getNumber());
        assertEquals(2, smsList.get(0).getNumberOfBlocked());

        smsList = ListAppPreferences.getSMSList("year");
        assertEquals(1, smsList.size());
        assertEquals(3, smsList.get(0).getNumberOfBlocked());

        smsList = ListAppPreferences.getSMSList("month");
        assertEquals(1, smsList.size());
        assertEquals(3, smsList.get(0).getNumberOfBlocked());

        smsList = ListAppPreferences.getSMSList("day");
        assertEquals(1, smsList.size());
        assertEquals(3, smsList.get(0).getNumberOfBlocked());

        // smsList = ListAppPreferences.getSMSList("default");
        // assertEquals(3, smsList.size());
    }
}
