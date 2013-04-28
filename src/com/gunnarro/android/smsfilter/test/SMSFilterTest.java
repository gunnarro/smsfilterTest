package com.gunnarro.android.smsfilter.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.gunnarro.android.smsfilter.service.FilterService;
import com.gunnarro.android.smsfilter.service.SMSFilter;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl.FilterTypeEnum;

public class SMSFilterTest extends TestCase {

    private SharedPreferences sharedPreferencesMock;
    private Editor prefsEditorMock;

    public void init() {
        sharedPreferencesMock = mock(SharedPreferences.class);
        prefsEditorMock = mock(Editor.class);
        // Mock the logging of blocked sms part of the smsfilter class
        String blockedNumbersLogList = "";
        when(sharedPreferencesMock.getString("SMS_BLOCKED_LOG", FilterService.DEFAULT_VALUE)).thenReturn(blockedNumbersLogList);
        when(prefsEditorMock.putString("SMS_BLOCKED_LOG", blockedNumbersLogList)).thenReturn(null);
        when(prefsEditorMock.commit()).thenReturn(true);
    }

    public void testBlackList() {
        init();
        String blackListAsString = "45465500:true;45465501:true;45465503:false";
        when(sharedPreferencesMock.getString(FilterTypeEnum.SMS_BLACK_LIST.name(), FilterService.DEFAULT_VALUE)).thenReturn(blackListAsString);
        when(sharedPreferencesMock.getString(FilterService.SMS_ACTIVE_FILTER_TYPE, FilterService.DEFAULT_VALUE)).thenReturn(FilterTypeEnum.SMS_BLACK_LIST.name());

        SMSFilter smsFilter = new SMSFilter();
        FilterServiceImpl listAppPreferencesImpl = new FilterServiceImpl();
        listAppPreferencesImpl.setAppSharedPrefs(sharedPreferencesMock);
        listAppPreferencesImpl.setPrefsEditor(prefsEditorMock);
        smsFilter.setAppPreferences(listAppPreferencesImpl);

        // Do the testing
        assertTrue(smsFilter.isBlocked("45465500"));
        assertTrue(smsFilter.isBlocked("45465501"));
        assertFalse(smsFilter.isBlocked("45465502"));
        assertFalse(smsFilter.isBlocked("47465502"));
        assertFalse(smsFilter.isBlocked("45465503"));
    }

    public void testBlackListEmptyList() {
        init();
        String blackListAsString = "";
        when(sharedPreferencesMock.getString(FilterTypeEnum.SMS_BLACK_LIST.name(), FilterService.DEFAULT_VALUE)).thenReturn(blackListAsString);
        when(sharedPreferencesMock.getString(FilterService.SMS_ACTIVE_FILTER_TYPE, FilterService.DEFAULT_VALUE)).thenReturn(FilterTypeEnum.SMS_BLACK_LIST.name());
        SMSFilter smsFilter = new SMSFilter();
        FilterServiceImpl listAppPreferencesImpl = new FilterServiceImpl();
        listAppPreferencesImpl.setAppSharedPrefs(sharedPreferencesMock);
        smsFilter.setAppPreferences(listAppPreferencesImpl);

        // Do the testing
        assertFalse(smsFilter.isBlocked("+4545465500"));
        assertFalse(smsFilter.isBlocked("+46465501"));
        assertFalse(smsFilter.isBlocked("45465502"));
        assertFalse(smsFilter.isBlocked("+47455500"));
    }

    public void testBlackListBlockCountryCode() {
        init();
        String blackListAsString = "+45*:true;+46*:true;+47*:false";
        when(sharedPreferencesMock.getString(FilterTypeEnum.SMS_BLACK_LIST.name(), FilterService.DEFAULT_VALUE)).thenReturn(blackListAsString);
        when(sharedPreferencesMock.getString(FilterService.SMS_ACTIVE_FILTER_TYPE, FilterService.DEFAULT_VALUE)).thenReturn(FilterTypeEnum.SMS_BLACK_LIST.name());

        SMSFilter smsFilter = new SMSFilter();
        FilterServiceImpl listAppPreferencesImpl = new FilterServiceImpl();
        listAppPreferencesImpl.setAppSharedPrefs(sharedPreferencesMock);
        listAppPreferencesImpl.setPrefsEditor(prefsEditorMock);
        smsFilter.setAppPreferences(listAppPreferencesImpl);

        // Do the testing
        assertTrue(smsFilter.isBlocked("+4545465500"));
        assertTrue(smsFilter.isBlocked("+4645465501"));
        assertFalse(smsFilter.isBlocked("+4745465500"));
        assertFalse(smsFilter.isBlocked("46465502"));
        assertFalse(smsFilter.isBlocked("45465502"));
    }

    public void testWhiteList() {
        init();
        String whiteListAsString = "45465500:true;45465501:true;45465502:false";
        when(sharedPreferencesMock.getString(FilterTypeEnum.SMS_WHITE_LIST.name(), FilterService.DEFAULT_VALUE)).thenReturn(whiteListAsString);
        when(sharedPreferencesMock.getString(FilterService.SMS_ACTIVE_FILTER_TYPE, FilterService.DEFAULT_VALUE)).thenReturn(FilterTypeEnum.SMS_WHITE_LIST.name());
        SMSFilter smsFilter = new SMSFilter();
        FilterServiceImpl listAppPreferencesImpl = new FilterServiceImpl();
        listAppPreferencesImpl.setAppSharedPrefs(sharedPreferencesMock);
        listAppPreferencesImpl.setPrefsEditor(prefsEditorMock);
        smsFilter.setAppPreferences(listAppPreferencesImpl);

        // Do the testing
        assertFalse(smsFilter.isBlocked("45465500"));
        assertFalse(smsFilter.isBlocked("45465501"));
        assertTrue(smsFilter.isBlocked("45465502"));
        assertTrue(smsFilter.isBlocked("47465502"));
    }

    public void testWhiteListEmptyList() {
        // Setup
        init();
        String whiteListAsString = "";
        when(sharedPreferencesMock.getString(FilterTypeEnum.SMS_WHITE_LIST.name(), FilterService.DEFAULT_VALUE)).thenReturn(whiteListAsString);
        when(sharedPreferencesMock.getString(FilterService.SMS_ACTIVE_FILTER_TYPE, FilterService.DEFAULT_VALUE)).thenReturn(FilterTypeEnum.SMS_WHITE_LIST.name());

        SMSFilter smsFilter = new SMSFilter();
        FilterServiceImpl listAppPreferencesImpl = new FilterServiceImpl();
        listAppPreferencesImpl.setAppSharedPrefs(sharedPreferencesMock);
        listAppPreferencesImpl.setPrefsEditor(prefsEditorMock);
        smsFilter.setAppPreferences(listAppPreferencesImpl);

        // Do the testing
        assertTrue(smsFilter.isBlocked("+4745465500"));
        assertTrue(smsFilter.isBlocked("+4645465501"));
        assertTrue(smsFilter.isBlocked("+4545465502"));
        assertTrue(smsFilter.isBlocked("47465502"));
    }

    public void testWhiteListAllowCountryCode() {
        init();
        String whiteListAsString = "+45*:true;+46*:true;+47*:false";
        when(sharedPreferencesMock.getString(FilterTypeEnum.SMS_WHITE_LIST.name(), FilterService.DEFAULT_VALUE)).thenReturn(whiteListAsString);
        when(sharedPreferencesMock.getString(FilterService.SMS_ACTIVE_FILTER_TYPE, FilterService.DEFAULT_VALUE)).thenReturn(FilterTypeEnum.SMS_WHITE_LIST.name());
        SMSFilter smsFilter = new SMSFilter();
        FilterServiceImpl listAppPreferencesImpl = new FilterServiceImpl();
        listAppPreferencesImpl.setAppSharedPrefs(sharedPreferencesMock);
        listAppPreferencesImpl.setPrefsEditor(prefsEditorMock);
        smsFilter.setAppPreferences(listAppPreferencesImpl);
        assertFalse(smsFilter.isBlocked("+4545465500"));
        assertFalse(smsFilter.isBlocked("+4645465501"));
        assertTrue(smsFilter.isBlocked("+4745465502"));
        assertTrue(smsFilter.isBlocked("47465502"));
    }

    public void testContacts() {
        init();
        when(sharedPreferencesMock.getString(FilterService.SMS_ACTIVE_FILTER_TYPE, FilterService.DEFAULT_VALUE)).thenReturn(FilterTypeEnum.SMS_CONTACTS.name());
        SMSFilter smsFilter = new SMSFilter();
        FilterServiceImpl listAppPreferencesImpl = new FilterServiceImpl();
        listAppPreferencesImpl.setAppSharedPrefs(sharedPreferencesMock);
        smsFilter.setAppPreferences(listAppPreferencesImpl);
        assertFalse(smsFilter.isBlocked("45465500"));
        assertFalse(smsFilter.isBlocked("45465501"));
        assertFalse(smsFilter.isBlocked("45465502"));
        assertFalse(smsFilter.isBlocked("47465502"));
    }

    public void testAllowAll() {
        init();
        when(sharedPreferencesMock.getString(FilterService.SMS_ACTIVE_FILTER_TYPE, FilterService.DEFAULT_VALUE)).thenReturn(FilterTypeEnum.ALLOW_ALL.name());
        SMSFilter smsFilter = new SMSFilter();
        FilterServiceImpl filterServiceImpl = new FilterServiceImpl();
        filterServiceImpl.setAppSharedPrefs(sharedPreferencesMock);
        smsFilter.setAppPreferences(filterServiceImpl);
        assertFalse(smsFilter.isBlocked("45465500"));
        assertFalse(smsFilter.isBlocked("45465501"));
        assertFalse(smsFilter.isBlocked("45465502"));
        assertFalse(smsFilter.isBlocked("47465502"));
    }

    public void testSearchFilter() {
        assertTrue("+4745465500".matches(FilterServiceImpl.createSearch("+47*")));
        assertFalse("+4645465500".matches(FilterServiceImpl.createSearch("+47*")));
        assertFalse("+4545465500".matches(FilterServiceImpl.createSearch("+47*")));
        assertTrue("45465500".matches(FilterServiceImpl.createSearch("45465500")));
        assertFalse("45465500".matches(FilterServiceImpl.createSearch("92019486")));
        assertTrue("45465500".matches(FilterServiceImpl.createSearch("4546550*")));
        assertTrue("45465501".matches(FilterServiceImpl.createSearch("4546550*")));
    }

}
