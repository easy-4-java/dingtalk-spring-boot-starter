package com.dingtalk.spring.boot;

import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.dingtalk.spring.boot.bean.*;
import com.dingtalk.spring.boot.property.DingTalkRobotProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DingTalkRobotTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void actionCardUsesWholeCardForDefaultAndExplicitFalseAndButtonsForTrue() throws Exception {
        DingTalkRobotOperations robot = new DingTalkRobotOperations(null);
        ActionCardButton button = new ActionCardButton("open", "https://action.test");

        ActionCardMessage defaultLayout = new ActionCardMessage("title", "body", button);
        assertWholeCard(robot, defaultLayout);

        ActionCardMessage explicitWholeCard = new ActionCardMessage("title", "body", button);
        explicitWholeCard.setButtonView(false);
        assertWholeCard(robot, explicitWholeCard);

        ActionCardMessage independentButton = new ActionCardMessage("title", "body", button);
        independentButton.setButtonView(true);
        JsonNode independentPayload = objectMapper.readTree(robot.buidRequest(independentButton).getActionCard());
        assertEquals("open", independentPayload.path("btns").path(0).path("title").asText());
        assertEquals("https://action.test", independentPayload.path("btns").path(0).path("actionURL").asText());
        assertFalse(independentPayload.has("singleTitle"));
        assertFalse(independentPayload.has("singleURL"));
    }

    private void assertWholeCard(DingTalkRobotOperations robot, ActionCardMessage message) throws Exception {
        JsonNode payload = objectMapper.readTree(robot.buidRequest(message).getActionCard());
        assertEquals("open", payload.path("singleTitle").asText());
        assertEquals("https://action.test", payload.path("singleURL").asText());
        assertFalse(payload.has("btns"));
    }

    @Test
    void buildsTextMarkdownLinkAndCardsForSdk() {
        DingTalkRobotOperations robot = new DingTalkRobotOperations(null);
        OapiRobotSendRequest text = robot.buidRequest(new TextMessage("hello", new String[] {"123"}));
        assertEquals("text", text.getMsgtype());
        assertTrue(text.getText().contains("hello"));
        assertTrue(text.getAt().contains("123"));
        OapiRobotSendRequest everyone = robot.buidRequest(new TextMessage("all", true));
        assertTrue(everyone.getAt().contains("true"));
        OapiRobotSendRequest link = robot.buidRequest(new LinkMessage("title", "body", "https://link.test", "https://image.test"));
        assertEquals("link", link.getMsgtype());
        assertTrue(link.getLink().contains("link.test"), link.getLink());
        OapiRobotSendRequest markdown = robot.buidRequest(new MarkdownMessage("title", "**body**", new String[] {"123"}));
        assertTrue(markdown.getMarkdown().contains("**body**"));
        assertTrue(markdown.getAt().contains("123"));
        ActionCardButton button = new ActionCardButton("open", "https://action.test");
        ActionCardMessage single = new ActionCardMessage("title", "body", button);
        single.setButtonView(true);
        assertTrue(robot.buidRequest(single).getActionCard().contains("action.test"));
        ActionCardMessage buttons = new ActionCardMessage("title", "body");
        buttons.addButton(button);
        buttons.addButton(new ActionCardButton("other", "https://other.test"));
        assertTrue(robot.buidRequest(buttons).getActionCard().contains("other.test"));
        FeedCardMessage feed = new FeedCardMessage(new ArrayList<>(List.of(new FeedCardMessageItem("news", "https://news.test", "https://pic.test"))));
        assertTrue(robot.buidRequest(feed).getFeedCard().contains("news.test"));
    }

    @Test
    void signsWebhookAndDelegatesConvenienceMessagesWithoutNetwork() throws Exception {
        DingTalkConfigProvider config = mock(DingTalkConfigProvider.class);
        DingTalkRobotProperties props = new DingTalkRobotProperties();
        props.setAccessToken("token"); props.setSecretToken("secret");
        when(config.getDingTalkRobotProperties("corp", "robot")).thenReturn(props);
        DingTalkTemplate template = new DingTalkTemplate(config, mock(DingTalkAccessTokenProvider.class));
        DingTalkRobotOperations robot = spy(new DingTalkRobotOperations(template));
        String webhook = robot.getWebhook("corp", "robot", 123L);
        assertTrue(webhook.contains("access_token=token"));
        assertTrue(webhook.contains("timestamp=123"));
        assertTrue(webhook.contains(template.getSign("secret", 123L)));
        OapiRobotSendResponse response = new OapiRobotSendResponse();
        doReturn(response).when(robot).sendMessage(eq("corp"), eq("robot"), any(OapiRobotSendRequest.class));
        assertSame(response, robot.sendTextMessage("corp", "robot", "hello"));
        assertSame(response, robot.sendTextMessage("corp", "robot", "hello", new String[] {"123"}));
        assertSame(response, robot.sendTextMessage("corp", "robot", "hello", true));
        assertSame(response, robot.sendTextMessage("corp", "robot", new TextMessage("hello")));
        assertSame(response, robot.sendLinkMessage("corp", "robot", "t", "b", "https://link.test"));
        assertSame(response, robot.sendLinkMessage("corp", "robot", "t", "b", "https://link.test", "https://pic.test"));
        assertSame(response, robot.sendMarkdownMessage("corp", "robot", "t", "**b**"));
        assertSame(response, robot.sendMarkdownMessage("corp", "robot", "t", "**b**", new String[] {"123"}));
        assertSame(response, robot.sendMarkdownMessage("corp", "robot", "t", "**b**", true));
        assertSame(response, robot.sendActionCardMessage("corp", "robot", "t", "b"));
        assertSame(response, robot.sendActionCardMessage("corp", "robot", "t", "b", HideAvatarType.HIDE));
        ActionCardButton button = new ActionCardButton("go", "https://go.test");
        assertSame(response, robot.sendActionCardMessage("corp", "robot", "t", "b", button));
        assertSame(response, robot.sendActionCardMessage("corp", "robot", "t", "b", HideAvatarType.HIDE, button));
        List<FeedCardMessageItem> items = new ArrayList<>(List.of(new FeedCardMessageItem("t", "https://link.test", "https://pic.test")));
        assertSame(response, robot.sendFeedCardMessage("corp", "robot", items));
        ArgumentCaptor<OapiRobotSendRequest> requests = ArgumentCaptor.forClass(OapiRobotSendRequest.class);
        verify(robot, atLeast(14)).sendMessage(eq("corp"), eq("robot"), requests.capture());
        assertEquals("text", requests.getAllValues().get(0).getMsgtype());
    }
}
