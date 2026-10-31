package com.dingtalk.spring.boot;

import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.dingtalk.spring.boot.bean.JsapiTicketSignature;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DingTalkApiOperationsTest {
    @Test
    void accountUserSnsAndSsoUseExpectedRequests() throws Exception {
        OapiUserGetuserinfoResponse userInfo = new OapiUserGetuserinfoResponse();
        OapiUserGetUseridByUnionidResponse union = new OapiUserGetUseridByUnionidResponse();
        OapiUserGetResponse user = new OapiUserGetResponse();
        OapiSnsGetuserinfoBycodeResponse snsByCode = new OapiSnsGetuserinfoBycodeResponse();
        OapiSnsGetPersistentCodeResponse persistent = new OapiSnsGetPersistentCodeResponse(); persistent.setBody("persistent");
        OapiSnsGetSnsTokenResponse snsToken = new OapiSnsGetSnsTokenResponse(); snsToken.setSnsToken("sns-token");
        OapiSnsGetuserinfoResponse snsInfo = new OapiSnsGetuserinfoResponse(); snsInfo.setBody("sns-user");
        DingTalkClient client = mock(DingTalkClient.class, invocation -> {
            Object request = invocation.getArgument(0);
            if (request instanceof OapiUserGetuserinfoRequest) return userInfo;
            if (request instanceof OapiUserGetUseridByUnionidRequest) return union;
            if (request instanceof OapiUserGetRequest) return user;
            if (request instanceof OapiSnsGetuserinfoBycodeRequest) return snsByCode;
            if (request instanceof OapiSnsGetPersistentCodeRequest) return persistent;
            if (request instanceof OapiSnsGetSnsTokenRequest) return snsToken;
            if (request instanceof OapiSnsGetuserinfoRequest) return snsInfo;
            throw new AssertionError(request.getClass());
        });
        DingTalkAccountOperations account = spy(new DingTalkAccountOperations(null));
        DingTalkUserOperations users = spy(new DingTalkUserOperations(null));
        DingTalkSnsOperations sns = spy(new DingTalkSnsOperations(null));
        DingTalkSsoOperations sso = spy(new DingTalkSsoOperations(null));
        doReturn(client).when(account).client(anyString());
        doReturn(client).when(users).client(anyString());
        doReturn(client).when(sns).client(anyString());
        doReturn(client).when(sso).client(anyString());
        assertSame(userInfo, account.getUserinfoBycode("code", "access"));
        assertSame(union, account.getUseridByUnionid("union", "access"));
        assertSame(user, account.getUserByUserid("user", "access"));
        assertSame(userInfo, users.getUserinfoByCode("code", "access"));
        assertSame(snsByCode, sns.getUserinfoByTmpCode("temp", "key", "secret"));
        assertEquals("persistent", sns.getPersistentCode("temp", "access"));
        assertEquals("sns-token", sns.getSnsToken("open", "persistent", "access"));
        assertEquals("sns-user", sns.getUserinfo("sns-token"));
        assertSame(snsByCode, sso.getUserinfoByTmpCode("temp", "key", "secret"));
        assertEquals("persistent", sso.getPersistentCode("temp", "access"));
        verify(account).client(endsWith("/user/getuserinfo"));
        verify(sns).client(endsWith("/sns/get_sns_token"));
    }

    @Test
    void jsapiSignatureUsesTicketNonceAndTimestamp() throws Exception {
        DingTalkJsapiOperations jsapi = new DingTalkJsapiOperations(null);
        DingTalkClient client = mock(DingTalkClient.class);
        OapiGetJsapiTicketResponse response = new OapiGetJsapiTicketResponse();
        response.setTicket("ticket");
        when(client.execute(any(OapiGetJsapiTicketRequest.class), eq("access"))).thenReturn(response);
        ReflectionTestUtils.setField(jsapi, "client", client);
        assertSame(response, jsapi.getTicket(TicketType.JSAPI, "access"));
        JsapiTicketSignature signature = jsapi.createSignature("https://example.test", "agent", "access");
        assertEquals("agent", signature.getAgentId());
        assertEquals("https://example.test", signature.getUrl());
        assertEquals(16, signature.getNonceStr().length());
        assertEquals(40, signature.getSignature().length());
        assertTrue(signature.getTimestamp() > 0);
    }

    @Test
    void tokenProviderReturnsAccessTokensAndHandlesUnsuccessfulResponses() throws Exception {
        DingTalkConfigProvider config = mock(DingTalkConfigProvider.class);
        when(config.getAppSecret("corp", "app")).thenReturn("secret");
        DefaultDingTalkAccessTokenProvider provider = spy(new DefaultDingTalkAccessTokenProvider(config));
        OapiGettokenResponse good = mock(OapiGettokenResponse.class);
        when(good.isSuccess()).thenReturn(true);
        when(good.getAccessToken()).thenReturn("access");
        OapiSnsGettokenResponse snsGood = mock(OapiSnsGettokenResponse.class);
        when(snsGood.isSuccess()).thenReturn(true);
        when(snsGood.getAccessToken()).thenReturn("sns-access");
        DingTalkClient client = mock(DingTalkClient.class, invocation -> {
            Object request = invocation.getArgument(0);
            return request instanceof OapiGettokenRequest ? good : snsGood;
        });
        doReturn(client).when(provider).client(anyString());
        assertEquals("access", provider.getAccessToken("corp", "app"));
        assertEquals("sns-access", provider.getSnsAccessToken("corp", "app"));
        when(good.isSuccess()).thenReturn(false);
        when(snsGood.isSuccess()).thenReturn(false);
        assertNull(provider.getAccessToken("corp", "app"));
        assertEquals("", provider.getSnsAccessToken("corp", "app"));
        verify(provider, atLeastOnce()).client(endsWith("/gettoken"));
        verify(provider, atLeastOnce()).client(endsWith("/sns/gettoken"));
    }
}
