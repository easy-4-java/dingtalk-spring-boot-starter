package com.dingtalk.spring.boot;

import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.spring.boot.bean.TextMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkRobotMessageTest {
    private final DingTalkRobotOperations robot = new DingTalkRobotOperations(null);

    @Test
    void serializesTargetedTextMessage() {
        OapiRobotSendRequest request = robot.buidRequest(new TextMessage("hello", new String[] {"123"}));
        assertEquals("text", request.getMsgtype());
        assertTrue(request.getText().contains("hello"));
        assertTrue(request.getAt().contains("123"));
    }

    @Test
    void serializesBroadcastTextMessage() {
        OapiRobotSendRequest request = robot.buidRequest(new TextMessage("all", true));
        assertEquals("text", request.getMsgtype());
        assertTrue(request.getText().contains("all"));
        assertTrue(request.getAt().contains("true"));
    }
}
