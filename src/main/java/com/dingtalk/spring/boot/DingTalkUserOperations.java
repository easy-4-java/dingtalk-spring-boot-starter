package com.dingtalk.spring.boot;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

/**
 * <ul>
 * <li>
 * 1、企业内部应用免登：
 * https://open.dingtalk.com/document/orgapp-server/enterprise-internal-application-logon-free
 * </li>
 * <li>
 * 2、第三方企业应用免登：
 * https://open.dingtalk.com/document/orgapp-server/third-party-enterprise-application-logon-free
 * </li>
 * <li>
 * 3、应用管理后台免登：
 * https://open.dingtalk.com/document/orgapp-server/log-on-site-application-management-backend
 * </ul>
 */
@Slf4j
public class DingTalkUserOperations extends DingTalkOperations {

	public DingTalkUserOperations(DingTalkTemplate template) {
		super(template);
	}

	/**
	 * @param code 	code
	 * @param accessToken 	应用的accessToken
	 * @return the OapiUserGetuserinfoResponse
	 * @throws ApiException if Api request Exception
	 */
	public OapiUserGetuserinfoResponse getUserinfoByCode(String code, String accessToken) throws ApiException {
		DingTalkClient client = client(PREFIX + "user/getuserinfo");
		OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
		request.setCode(code);
		return client.execute(request, accessToken);
	}

}
