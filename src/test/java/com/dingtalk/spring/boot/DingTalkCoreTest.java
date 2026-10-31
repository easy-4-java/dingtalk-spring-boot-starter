package com.dingtalk.spring.boot;

import com.dingtalk.spring.boot.property.*;
import com.dingtalk.spring.boot.utils.DingTalkUtils;
import com.dingtalk.spring.boot.utils.RandomUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DingTalkCoreTest {
    @Test
    void resolvesEachConfiguredApplicationAndRobot() throws Exception {
        DingTalkProperties properties = new DingTalkProperties();
        properties.setCorpId("corp");
        properties.setCorpSecret("corp-secret");
        DingTalkCorpAppProperties corp = new DingTalkCorpAppProperties();
        corp.setAgentId("agent"); corp.setAppKey("corp-key"); corp.setAppSecret("corp-app-secret");
        DingTalkPersonalMiniAppProperties personal = new DingTalkPersonalMiniAppProperties();
        personal.setAppId("personal"); personal.setAppSecret("personal-secret");
        DingTalkSuiteProperties suite = new DingTalkSuiteProperties();
        suite.setSuiteId("suite"); suite.setAppId("suite-app"); suite.setSuiteSecret("suite-secret");
        DingTalkLoginProperties login = new DingTalkLoginProperties();
        login.setAppId("login"); login.setAppSecret("login-secret");
        DingTalkRobotProperties robot = new DingTalkRobotProperties();
        robot.setRobotId("robot"); robot.setAccessToken("robot-token"); robot.setSecretToken("robot-secret");
        properties.setCorpApps(List.of(corp)); properties.setApps(List.of(personal));
        properties.setSuites(List.of(suite)); properties.setLogins(List.of(login)); properties.setRobots(List.of(robot));
        DefaultDingTalkConfigProvider provider = new DefaultDingTalkConfigProvider(properties);
        provider.afterPropertiesSet();
        assertSame(properties, provider.getDingTalkProperties("corp"));
        assertSame(corp, provider.getDingTalkCorpAppProperties("corp", "agent"));
        assertSame(personal, provider.getDingTalkPersonalMiniAppProperties("corp", "personal"));
        assertSame(suite, provider.getDingTalkSuiteProperties("corp", "suite"));
        assertSame(login, provider.getDingTalkLoginProperties("corp", "login"));
        assertSame(robot, provider.getDingTalkRobotProperties("corp", "robot"));
        assertNull(provider.getDingTalkRobotProperties("corp", "missing"));
        assertNull(provider.getDingTalkCorpAppProperties("corp", "missing"));
        assertNull(provider.getDingTalkPersonalMiniAppProperties("corp", "missing"));
        assertNull(provider.getDingTalkSuiteProperties("corp", "missing"));
        assertNull(provider.getDingTalkLoginProperties("corp", "missing"));
        assertTrue(provider.hasAppKey("corp-key"));
        assertTrue(provider.hasAppKey("personal"));
        assertTrue(provider.hasAppKey("suite-app"));
        assertTrue(provider.hasAppKey("login"));
        assertFalse(provider.hasAppKey("unknown"));
        assertEquals("corp", provider.getCorpId("corp-key"));
        assertEquals("corp-secret", provider.getCorpSecret("corp"));
        assertEquals("suite-secret", provider.getAppSecret("corp", "suite-app"));
        assertNull(provider.getAppSecret("corp", "missing"));
    }

    @Test
    void templateDelegatesConfigurationAndSignsRobotRequests() throws Exception {
        DingTalkConfigProvider config = mock(DingTalkConfigProvider.class);
        DingTalkAccessTokenProvider tokens = mock(DingTalkAccessTokenProvider.class);
        when(config.hasAppKey("key")).thenReturn(true);
        when(config.getCorpId("key")).thenReturn("corp");
        when(config.getCorpSecret("corp")).thenReturn("secret");
        when(config.getAppSecret("corp", "key")).thenReturn("app-secret");
        when(tokens.getAccessToken("corp", "key")).thenReturn("token");
        when(tokens.getSnsAccessToken("corp", "key")).thenReturn("sns-token");
        DingTalkTemplate template = new DingTalkTemplate(config, tokens);
        template.afterPropertiesSet();
        assertTrue(template.hasAppKey("key"));
        assertEquals("corp", template.getCorpId("key"));
        assertEquals("secret", template.getCorpSecret("corp"));
        assertEquals("app-secret", template.getAppSecret("corp", "key"));
        assertEquals("token", template.getAccessToken("corp", "key"));
        assertEquals("sns-token", template.getSnsAccessToken("corp", "key"));
        assertSame(config, template.getDingTalkConfigProvider());
        assertSame(tokens, template.getDingTalkAccessTokenProvider());
        assertNotNull(template.opsForAccount()); assertNotNull(template.opsForSns());
        assertNotNull(template.opsForSso()); assertNotNull(template.opsForJsapi());
        assertNotNull(template.opsForRobot()); assertNotNull(template.opsForUser());
        assertEquals("1dxVzUsFiodFW5wtMQ0VW0lR58ivoWDJB%2B08r%2FH4Clw%3D", template.getSign("secret", 123L));
        assertNull(template.getSign(null, 123L));
    }

    @Test
    void computesJsapiSha1AndGeneratesProtocolNonce() throws Exception {
        String plain = "jsapi_ticket=ticket&noncestr=nonce&timestamp=123&url=https://example.test";
        String expected = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-1").digest(plain.getBytes(StandardCharsets.UTF_8)));
        assertEquals(expected, DingTalkUtils.sign("ticket", "nonce", 123L, "https://example.test"));
        assertTrue(RandomUtils.getRandomStr().matches("[a-zA-Z0-9]{16}"));
    }
}
