/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.dingtalk.spring.boot;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGetJsapiTicketRequest;
import com.dingtalk.api.response.OapiGetJsapiTicketResponse;
import com.dingtalk.spring.boot.bean.JsapiTicketSignature;
import com.dingtalk.spring.boot.utils.DingTalkUtils;
import com.dingtalk.spring.boot.utils.RandomUtils;
import com.taobao.api.ApiException;

/**
 */
public class DingTalkJsapiOperations extends DingTalkOperations {

	DingTalkClient client = new DefaultDingTalkClient(PREFIX + "/get_jsapi_ticket");
	
	public DingTalkJsapiOperations(DingTalkTemplate template) {
		super(template);
	}

	/*
	 * 获得ticket,不强制刷新ticket.
	 *
	 * @see #getTicket(TicketType, boolean)
	 */
	public OapiGetJsapiTicketResponse getTicket(TicketType type, String accessToken) throws ApiException {
		OapiGetJsapiTicketRequest req = new OapiGetJsapiTicketRequest();
		req.setTopHttpMethod(METHOD_GET);
		return client.execute(req, accessToken);
	}

	/*
	 * 创建调用jsapi时所需要的签名. 详情请见：https://ding-doc.dingtalk.com/doc#/dev/uwa7vs
	 */
	public JsapiTicketSignature createSignature(String url, String agentId, String accessToken)
			throws ApiException {

		long timestamp = System.currentTimeMillis() / 1000;
		String randomStr = RandomUtils.getRandomStr();
		OapiGetJsapiTicketResponse jsapiTicket = getTicket(TicketType.JSAPI, accessToken);
		String signature = DingTalkUtils.sign(jsapiTicket.getTicket(), randomStr, timestamp, url);
		JsapiTicketSignature jsapiSignature = new JsapiTicketSignature();
		jsapiSignature.setAgentId(agentId);
		jsapiSignature.setTimestamp(timestamp);
		jsapiSignature.setNonceStr(randomStr);
		jsapiSignature.setUrl(url);
		jsapiSignature.setSignature(signature);
		return jsapiSignature;

	}

}
