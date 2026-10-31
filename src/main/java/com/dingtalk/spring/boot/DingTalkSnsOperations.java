package com.dingtalk.spring.boot;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiSnsGetPersistentCodeRequest;
import com.dingtalk.api.request.OapiSnsGetSnsTokenRequest;
import com.dingtalk.api.request.OapiSnsGetuserinfoBycodeRequest;
import com.dingtalk.api.request.OapiSnsGetuserinfoRequest;
import com.dingtalk.api.response.OapiSnsGetPersistentCodeResponse;
import com.dingtalk.api.response.OapiSnsGetSnsTokenResponse;
import com.dingtalk.api.response.OapiSnsGetuserinfoBycodeResponse;
import com.dingtalk.api.response.OapiSnsGetuserinfoResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

/**
 * <ul>
 * <li>
 * 1、扫码登录第三方网站：
 * https://ding-doc.dingtalk.com/doc#/serverapi2/kymkv6
 * </li>
 * <li>
 * 2、钉钉内免登第三方网站：
 * https://ding-doc.dingtalk.com/doc#/serverapi2/etaarr
 * </li>
 * <li>
 * 3、密码登录第三方网站：
 * https://ding-doc.dingtalk.com/doc#/serverapi2/hmxp3f
 * </ul>
 */
@Slf4j
public class DingTalkSnsOperations extends DingTalkOperations {

	public DingTalkSnsOperations(DingTalkTemplate template) {
		super(template);
	}

	/**
	 * 第三方应用钉钉扫码登录：通过临时授权码Code获取用户信息，临时授权码只能使用一次。
	 * https://open-doc.dingtalk.com/microapp/serverapi2/kymkv6
	 * @param tmp_auth_code 用户授权的临时授权码code，只能使用一次；在前面步骤中跳转到redirect_uri时会追加code参数
	 * @param accessKey 	应用的appId
	 * @param accessSecret 	应用的secret
	 * @return the OapiUserGetuserinfoResponse
	 * @throws ApiException if Api request Exception
	 */
	public OapiSnsGetuserinfoBycodeResponse getUserinfoByTmpCode( String tmp_auth_code, String accessKey, String accessSecret) throws ApiException {
		DingTalkClient client = client(PREFIX + "/sns/getuserinfo_bycode");
		OapiSnsGetuserinfoBycodeRequest request = new OapiSnsGetuserinfoBycodeRequest();
		request.setTmpAuthCode(tmp_auth_code);
		return client.execute(request, accessKey, accessSecret);
	}

	/**
	 * 获取用户授权的持久授权码
	 * @param tmp_auth_code 用户授权的临时授权码code，只能使用一次；在前面步骤中跳转到redirect_uri时会追加code参数
	 * @param accessToken  开放应用的token
	 * @return 响应信息
	 * @throws ApiException if Api request Exception
	 */
	public String getPersistentCode(String tmp_auth_code, String accessToken) throws ApiException  {
		DingTalkClient client = client(PREFIX + "/sns/get_persistent_code");
		OapiSnsGetPersistentCodeRequest request = new OapiSnsGetPersistentCodeRequest();
		request.setTmpAuthCode(tmp_auth_code);
		OapiSnsGetPersistentCodeResponse response = client.execute(request, accessToken);
		return response.getBody();
	}

	/*
	 * 获取用户授权的SNS_TOKEN
	 *
	 * @param openId
	 * @param persistentCode
	 * @param accessToken    开放应用的token
	 * @return
	 */
	public String getSnsToken(String openId, String persistentCode, String accessToken) throws ApiException {
		DingTalkClient client = client(PREFIX + "/sns/get_sns_token");
		OapiSnsGetSnsTokenRequest request = new OapiSnsGetSnsTokenRequest();
		request.setOpenid(openId);
		request.setPersistentCode(persistentCode);
		OapiSnsGetSnsTokenResponse response = client.execute(request, accessToken);
		return response.getSnsToken();
	}

	/*
	 * 获取用户授权的个人信息
	 *
	 * @param snsToken
	 * @return
	 */
	public String getUserinfo(String snsToken) throws ApiException{
		DingTalkClient client = client(PREFIX + "/sns/getuserinfo");
		OapiSnsGetuserinfoRequest request = new OapiSnsGetuserinfoRequest();
		request.setSnsToken(snsToken);
		request.setHttpMethod(METHOD_GET);
		OapiSnsGetuserinfoResponse response = client.execute(request);
		return response.getBody();
	}

}
