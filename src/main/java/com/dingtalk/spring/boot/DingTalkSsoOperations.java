package com.dingtalk.spring.boot;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiSnsGetPersistentCodeRequest;
import com.dingtalk.api.request.OapiSnsGetuserinfoBycodeRequest;
import com.dingtalk.api.response.OapiSnsGetPersistentCodeResponse;
import com.dingtalk.api.response.OapiSnsGetuserinfoBycodeResponse;
import com.taobao.api.ApiException;

import lombok.extern.slf4j.Slf4j;

/**
 * 应用管理后台免登
 * https://ding-doc.dingtalk.com/doc#/serverapi2/xswxhg
 */
@Slf4j
public class DingTalkSsoOperations extends DingTalkOperations {

	public DingTalkSsoOperations(DingTalkTemplate template) {
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
 	
}
