package com.gunnarro.android.smsfilter.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import junit.framework.TestCase;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.gunnarro.android.smsfilter.domain.SMS;
import com.gunnarro.android.smsfilter.service.FilterService;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl.FilterTypeEnum;

public class FilterServiceTest extends TestCase {

    private SharedPreferences sharedPreferencesMock;
    private Editor prefsEditorMock;
    private FilterServiceImpl filterService;

    public void init() {
        filterService = new FilterServiceImpl();
        // init. mocks
        sharedPreferencesMock = mock(SharedPreferences.class);
        prefsEditorMock = mock(Editor.class);
        filterService.setAppSharedPrefs(sharedPreferencesMock);

        // Mock the logging of blocked sms part of the smsfilter class
        String blockedNumbersLogList = "";
        when(sharedPreferencesMock.getString("SMS_BLOCKED_LOG", FilterService.DEFAULT_VALUE)).thenReturn(blockedNumbersLogList);
        when(prefsEditorMock.putString("SMS_BLOCKED_LOG", blockedNumbersLogList)).thenReturn(null);
        when(prefsEditorMock.commit()).thenReturn(true);
    }

    public void testFilter() {
        assertTrue(FilterServiceImpl.FilterTypeEnum.ALLOW_ALL.isAllowAll());
        assertTrue(FilterServiceImpl.FilterTypeEnum.SMS_BLACK_LIST.isBlackList());
        assertTrue(FilterServiceImpl.FilterTypeEnum.SMS_WHITE_LIST.isWhiteList());
        assertTrue(FilterServiceImpl.FilterTypeEnum.SMS_CONTACTS.isContacts());
    }

    public void testSMSBlockedlog() {
        init();
        System.out.println(Long.toString(System.currentTimeMillis()));
        String blockedListAsString = System.currentTimeMillis() + ":45465500;" + System.currentTimeMillis() + ":45465500;" + System.currentTimeMillis()
                + ":45465501";

        when(sharedPreferencesMock.getString("SMS_BLOCKED_LOG", FilterService.DEFAULT_VALUE)).thenReturn(blockedListAsString);

        List<SMS> smsList = filterService.getSMSList("number");
        assertEquals(2, smsList.size());
        assertEquals("45465500", smsList.get(0).getNumber());
        assertEquals(2, smsList.get(0).getNumberOfBlocked());

        smsList = filterService.getSMSList("year");
        assertEquals(1, smsList.size());
        assertEquals(3, smsList.get(0).getNumberOfBlocked());

        smsList = filterService.getSMSList("month");
        assertEquals(1, smsList.size());
        assertEquals(3, smsList.get(0).getNumberOfBlocked());

        smsList = filterService.getSMSList("day");
        assertEquals(1, smsList.size());
        assertEquals(3, smsList.get(0).getNumberOfBlocked());

    }

    public void testSaveFilterType() {
        init();
        filterService.save(FilterService.SMS_ACTIVE_FILTER_TYPE, FilterServiceImpl.FilterTypeEnum.SMS_BLACK_LIST.name());
        String storedFilterType = filterService.getValue(FilterService.SMS_ACTIVE_FILTER_TYPE);
        assertEquals(FilterServiceImpl.FilterTypeEnum.SMS_BLACK_LIST.name(), storedFilterType);
    }

    public void testBlackList() {
        init();
        String blackListAsString = "45465500:true;45465501:true;45465503:false";
        when(sharedPreferencesMock.getString(FilterTypeEnum.SMS_BLACK_LIST.name(), FilterService.DEFAULT_VALUE)).thenReturn(blackListAsString);
        when(sharedPreferencesMock.getString(FilterService.SMS_ACTIVE_FILTER_TYPE, FilterService.DEFAULT_VALUE)).thenReturn(FilterTypeEnum.SMS_BLACK_LIST.name());

        FilterServiceImpl filterService = new FilterServiceImpl();
        filterService.setAppSharedPrefs(sharedPreferencesMock);
        filterService.setPrefsEditor(prefsEditorMock);

        // Do the testing
        assertTrue(filterService.isBlocked("45465500"));
        assertTrue(filterService.isBlocked("45465501"));
        assertFalse(filterService.isBlocked("45465502"));
        assertFalse(filterService.isBlocked("47465502"));
        assertFalse(filterService.isBlocked("45465503"));
    }

}
