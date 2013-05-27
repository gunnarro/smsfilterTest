package com.gunnarro.android.smsfilter.test;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gunnarro.android.smsfilter.domain.Filter;
import com.gunnarro.android.smsfilter.domain.Item;
import com.gunnarro.android.smsfilter.domain.MsgLog;
import com.gunnarro.android.smsfilter.domain.SMS;
import com.gunnarro.android.smsfilter.domain.SMSLog;
import com.gunnarro.android.smsfilter.repository.impl.FilterRepositoryImpl;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl;
import com.gunnarro.android.smsfilter.service.impl.FilterServiceImpl.FilterTypeEnum;

public class FilterServiceTest extends TestCase {

    private FilterServiceImpl filterService;

    @Mock
    private FilterRepositoryImpl filterRepositoryMock;

    public void init() {
        // init. mocks
        MockitoAnnotations.initMocks(this);
        filterService = new FilterServiceImpl();
        filterService.setFilterRepository(filterRepositoryMock);
    }

    public void testFilter() {
        assertTrue(FilterServiceImpl.FilterTypeEnum.SMS_BLACK_LIST.isBlackList());
        assertTrue(FilterServiceImpl.FilterTypeEnum.SMS_WHITE_LIST.isWhiteList());
        assertTrue(FilterServiceImpl.FilterTypeEnum.CONTACTS.isContacts());
    }

    public void testSMSBlockedlog() {
        init();
        List<MsgLog> smsLogs = new ArrayList<MsgLog>();
        smsLogs.add(new SMSLog(System.currentTimeMillis(), "45460000", SMSLog.STATUS_MSG_BLOCKED, FilterTypeEnum.SMS_BLACK_LIST.name()));
        smsLogs.add(new SMSLog(System.currentTimeMillis(), "45460001", SMSLog.STATUS_MSG_BLOCKED, FilterTypeEnum.SMS_WHITE_LIST.name()));
        smsLogs.add(new SMSLog(System.currentTimeMillis(), "45460002", SMSLog.STATUS_MSG_BLOCKED, FilterTypeEnum.CONTACTS.name()));

        when(filterRepositoryMock.getLogList("number", "SMS")).thenReturn(smsLogs);

        List<MsgLog> logs = filterService.getLogs("number", "%");
        assertEquals(2, logs.size());
        assertEquals("45460000", logs.get(0).getPhoneNumber());
        assertEquals(1, logs.get(0).getCount());
        assertEquals(FilterTypeEnum.SMS_BLACK_LIST.name(), logs.get(0).getFilterType());
        assertEquals("", logs.get(0).getKey());
        assertNotNull(logs.get(0).getReceivedTime());
        assertEquals(SMSLog.STATUS_MSG_BLOCKED, logs.get(0).getStatus());
    }

    public void testBlackList() {
        init();
        List<Item> itemList = new ArrayList<Item>();
        itemList.add(new Item("45465500", true));
        itemList.add(new Item("45465501", true));
        itemList.add(new Item("45465503", false));
        itemList.add(new Item("45465503", false));
        when(filterRepositoryMock.getActiveFilter()).thenReturn(new Filter(FilterTypeEnum.SMS_BLACK_LIST.name(), true));
        when(filterRepositoryMock.getItemList(FilterTypeEnum.SMS_BLACK_LIST.name())).thenReturn(itemList);

        // Do the testing
        assertFalse(filterService.isBlocked(new SMS("+4745465500")));
        assertFalse(filterService.isBlocked(new SMS("004745465500")));
        assertTrue(filterService.isBlocked(new SMS("45465500")));
        assertTrue(filterService.isBlocked(new SMS("45465501")));
        assertFalse(filterService.isBlocked(new SMS("45465502")));
        assertFalse(filterService.isBlocked(new SMS("47465502")));
        assertFalse(filterService.isBlocked(new SMS("45465503")));
        assertFalse(filterService.isBlocked(null));
        assertFalse(filterService.isBlocked(new SMS("")));
        assertFalse(filterService.isBlocked(new SMS("hidden")));
    }

    public void testBlackListBlockCountryCode() {
        init();
        List<Item> itemList = new ArrayList<Item>();
        itemList.add(new Item("+45*", true));
        itemList.add(new Item("+46*", true));
        itemList.add(new Item("*+47*", false));
        when(filterRepositoryMock.getActiveFilter()).thenReturn(new Filter(FilterTypeEnum.SMS_BLACK_LIST.name(), true));
        when(filterRepositoryMock.getItemList(FilterTypeEnum.SMS_BLACK_LIST.name())).thenReturn(itemList);

        // Do the testing
        assertTrue(filterService.isBlocked(new SMS("+4545465500")));
        assertTrue(filterService.isBlocked(new SMS("+4645465501")));
        assertFalse(filterService.isBlocked(new SMS("+4745465500")));
        assertFalse(filterService.isBlocked(new SMS("46465502")));
        assertFalse(filterService.isBlocked(new SMS("45465502")));
    }

    public void testBlackListEmptyList() {
        init();
        when(filterRepositoryMock.getActiveFilter()).thenReturn(new Filter(FilterTypeEnum.SMS_BLACK_LIST.name(), true));
        when(filterRepositoryMock.getItemList(FilterTypeEnum.SMS_BLACK_LIST.name())).thenReturn(new ArrayList<Item>());

        // Do the testing
        assertFalse(filterService.isBlocked(new SMS("+4545465500")));
        assertFalse(filterService.isBlocked(new SMS("+46465501")));
        assertFalse(filterService.isBlocked(new SMS("45465502")));
        assertFalse(filterService.isBlocked(new SMS("+47455500")));
    }

    public void testWhiteList() {
        init();
        List<Item> itemList = new ArrayList<Item>();
        itemList.add(new Item("45465500", true));
        itemList.add(new Item("45465501", true));
        itemList.add(new Item("45465503", false));
        itemList.add(new Item("45465503", false));
        when(filterRepositoryMock.getActiveFilter()).thenReturn(new Filter(FilterTypeEnum.SMS_WHITE_LIST.name(), true));
        when(filterRepositoryMock.getItemList(FilterTypeEnum.SMS_WHITE_LIST.name())).thenReturn(itemList);

        // Do the testing
        assertTrue(filterService.isBlocked(new SMS("+4745465500")));
        assertTrue(filterService.isBlocked(new SMS("004745465500")));
        assertFalse(filterService.isBlocked(new SMS("45465500")));
        assertFalse(filterService.isBlocked(new SMS("45465501")));
        assertTrue(filterService.isBlocked(new SMS("45465502")));
        assertTrue(filterService.isBlocked(new SMS("47465502")));
        assertTrue(filterService.isBlocked(new SMS("45465503")));
        assertTrue(filterService.isBlocked(null));
        assertTrue(filterService.isBlocked(new SMS("")));
        assertTrue(filterService.isBlocked(new SMS("hidden")));
    }

    public void testWhiteListAllowCountryCode() {
        init();
        List<Item> itemList = new ArrayList<Item>();
        itemList.add(new Item("+45*", true));
        itemList.add(new Item("+46*", true));
        itemList.add(new Item("*+47*", false));
        when(filterRepositoryMock.getActiveFilter()).thenReturn(new Filter(FilterTypeEnum.SMS_WHITE_LIST.name(), true));
        when(filterRepositoryMock.getItemList(FilterTypeEnum.SMS_WHITE_LIST.name())).thenReturn(itemList);

        assertFalse(filterService.isBlocked(new SMS("+4545465500")));
        assertFalse(filterService.isBlocked(new SMS("+4645465501")));
        assertTrue(filterService.isBlocked(new SMS("+4745465502")));
        assertTrue(filterService.isBlocked(new SMS("47465502")));
    }

    public void testWhiteListEmptyList() {
        // Setup
        init();
        when(filterRepositoryMock.getActiveFilter()).thenReturn(new Filter(FilterTypeEnum.SMS_WHITE_LIST.name(), true));
        when(filterRepositoryMock.getItemList(FilterTypeEnum.SMS_WHITE_LIST.name())).thenReturn(new ArrayList<Item>());

        // Do the testing
        assertTrue(filterService.isBlocked(new SMS("+4745465500")));
        assertTrue(filterService.isBlocked(new SMS("+4645465501")));
        assertTrue(filterService.isBlocked(new SMS("+4545465502")));
        assertTrue(filterService.isBlocked(new SMS("47465502")));
    }

    public void testSearchFilter() {
        assertTrue("+4745465500".matches(FilterServiceImpl.createSearch("+47*")));
        assertFalse("+4645465500".matches(FilterServiceImpl.createSearch("+47*")));
        assertFalse("+4545465500".matches(FilterServiceImpl.createSearch("+47*")));
        assertTrue("45465500".matches(FilterServiceImpl.createSearch("45465500")));
        assertFalse("45465500".matches(FilterServiceImpl.createSearch("92019486")));
        assertTrue("45465500".matches(FilterServiceImpl.createSearch("4546550*")));
        assertTrue("45465501".matches(FilterServiceImpl.createSearch("4546550*")));
        assertFalse("skjult nummer".matches(FilterServiceImpl.createSearch("hidden")));
        assertFalse("HIDDEN NUMBER".matches(FilterServiceImpl.createSearch("hidden")));
        assertFalse("asdf2345".matches(FilterServiceImpl.createSearch("hidden")));
        assertFalse("xxx".matches(FilterServiceImpl.createSearch("hidden")));
        assertFalse("45465500".matches(FilterServiceImpl.createSearch("")));
        assertFalse("45465500".matches(FilterServiceImpl.createSearch(null)));
        assertTrue("+4745465500".matches(FilterServiceImpl.createSearch("hidden")));
    }

    public void testGetLogStartDateAndEndDate() {
        // Setup
        init();
        List<MsgLog> logs = new ArrayList<MsgLog>();
        when(filterRepositoryMock.getLogListOrderByDate("SMS")).thenReturn(logs);
        List<MsgLog> logStartAndEndDate = filterService.getLogsStartDateAndEndDate();
        assertEquals(2, logStartAndEndDate.size());
        assertNotNull("", logStartAndEndDate.get(0).getReceivedTime());
        assertNotNull("", logStartAndEndDate.get(1).getReceivedTime());

    }

    public void testGetLogEndDate() {
        // Setup
        init();
        List<MsgLog> logs = new ArrayList<MsgLog>();
        when(filterRepositoryMock.getLogListOrderByDate("%")).thenReturn(logs);
        MsgLog log = filterService.getLogsEndDate();
        assertNotNull("", log.getReceivedTime());
    }
}
