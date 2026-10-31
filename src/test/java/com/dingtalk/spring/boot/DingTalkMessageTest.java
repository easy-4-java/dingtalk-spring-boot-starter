package com.dingtalk.spring.boot;

import com.dingtalk.spring.boot.bean.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkMessageTest {
    @Test
    void defaultMessagesCanBePopulatedAndSerialized() {
        DingTalkRobotOperations robot = new DingTalkRobotOperations(null);
        TextMessage text = new TextMessage();
        text.setContent("hello"); text.setAtMobiles(new String[] {"123"}); text.setAtAll(true);
        assertEquals("hello", text.getContent());
        assertArrayEquals(new String[] {"123"}, text.getAtMobiles());
        assertTrue(text.isAtAll());
        assertTrue(robot.buidRequest(text).getText().contains("hello"));

        LinkMessage link = new LinkMessage();
        link.setTitle("title"); link.setText("body");
        link.setMessageUrl("https://message.test"); link.setPicUrl("https://pic.test");
        assertEquals("title", link.getTitle()); assertEquals("body", link.getText());
        assertEquals("https://message.test", link.getMessageUrl());
        assertEquals("https://pic.test", link.getPicUrl());
        assertTrue(robot.buidRequest(link).getLink().contains("message.test"));

        MarkdownMessage markdown = new MarkdownMessage();
        markdown.setTitle("title"); markdown.setText("**body**");
        markdown.setAtMobiles(new String[] {"456"}); markdown.setIsAtAll(true);
        assertEquals("title", markdown.getTitle()); assertEquals("**body**", markdown.getText());
        assertArrayEquals(new String[] {"456"}, markdown.getAtMobiles()); assertTrue(markdown.getIsAtAll());
        assertTrue(robot.buidRequest(markdown).getAt().contains("456"));

        ActionCardButton button = new ActionCardButton();
        button.setTitle("go"); button.setActionURL("https://go.test");
        assertEquals("go", button.getTitle()); assertEquals("https://go.test", button.getActionURL());
        assertEquals("https://read.test", ActionCardButton.defaultReadButton("https://read.test").getActionURL());
        ActionCardMessage action = new ActionCardMessage();
        action.setTitle("title"); action.setText("body");
        action.setHideAvatar(HideAvatarType.HIDE);
        action.setBtnOrientation(ButtonOrientationType.VERTICAL);
        action.addButton(button);
        assertEquals("title", action.getTitle()); assertEquals("body", action.getText());
        assertEquals(HideAvatarType.HIDE, action.getHideAvatar());
        assertEquals(ButtonOrientationType.VERTICAL, action.getBtnOrientation());
        assertTrue(robot.buidRequest(action).getActionCard().contains("go.test"));

        FeedCardMessageItem item = new FeedCardMessageItem();
        item.setTitle("news"); item.setMessageURL("https://news.test"); item.setPicURL("https://image.test");
        assertEquals("news", item.getTitle()); assertEquals("https://news.test", item.getMessageURL());
        assertEquals("https://image.test", item.getPicURL());
        FeedCardMessage feed = new FeedCardMessage();
        feed.addFeedCardItem(item);
        assertEquals(List.of(item), feed.getFeedCardItems());
        assertTrue(robot.buidRequest(feed).getFeedCard().contains("news.test"));
    }

    @Test
    void cardsRejectMissingItemsAndExcessButtons() {
        ActionCardMessage card = new ActionCardMessage();
        assertThrows(IllegalArgumentException.class, () -> card.addButton(null));
        for (int i = 0; i < 5; i++) card.addButton(new ActionCardButton("go", "https://go.test"));
        assertThrows(IllegalArgumentException.class, () -> card.addButton(new ActionCardButton("extra", "https://extra.test")));
        FeedCardMessage feed = new FeedCardMessage();
        assertThrows(IllegalArgumentException.class, () -> feed.addFeedCardItem(null));
        assertThrows(IllegalArgumentException.class, () -> feed.addFeedCardItem(new FeedCardMessageItem("", "url", "pic")));
        assertThrows(IllegalArgumentException.class, () -> new FeedCardMessage(List.of(new FeedCardMessageItem("title", "url", "pic"))));
        ArrayList<FeedCardMessageItem> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) items.add(new FeedCardMessageItem("title", "url", "pic"));
        FeedCardMessage full = new FeedCardMessage(items);
        assertThrows(IllegalArgumentException.class, () -> full.addFeedCardItem(new FeedCardMessageItem("extra", "url", "pic")));
        items.add(new FeedCardMessageItem("extra", "url", "pic"));
        assertThrows(IllegalArgumentException.class, () -> new FeedCardMessage(items));
    }
}
