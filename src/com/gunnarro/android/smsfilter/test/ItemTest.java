package com.gunnarro.android.smsfilter.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.gunnarro.android.smsfilter.view.Item;

public class ItemTest extends TestCase {

    public void testToValuePair() {
        Item item = Item.createItem("12345678:true");
        assertEquals("12345678", item.getValue());
        assertTrue(item.isEnabled());
        assertEquals("12345678:true", item.toValuePair());

        item = Item.createItem("12345678:false");
        assertEquals("12345678", item.getValue());
        assertFalse(item.isEnabled());
        assertEquals("12345678:false", item.toValuePair());

        item = Item.createItem("12345678:invalid");
        assertNull(item);

        item = Item.createItem("12345678");
        assertNull(item);

        item = Item.createItem("");
        assertNull(item);

        item = Item.createItem(null);
        assertNull(item);
    }

    public void testItemList() {
        List<Item> list = new ArrayList<Item>();
        list.add(Item.createItem("12345678:true"));
        list.add(Item.createItem("11223344:true"));

        assertTrue(list.contains(new Item("11223344", true)));
        assertTrue(list.contains(new Item("11223344", false)));
        assertFalse(list.contains(new Item("11223345", true)));
    }
}
