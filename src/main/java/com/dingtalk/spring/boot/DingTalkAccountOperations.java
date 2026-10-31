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
import com.dingtalk.api.request.OapiUserGetRequest;
import com.dingtalk.api.request.OapiUserGetUseridByUnionidRequest;
import com.dingtalk.api.request.OapiUserGetuserinfoRequest;
import com.dingtalk.api.response.OapiUserGetResponse;
import com.dingtalk.api.response.OapiUserGetUseridByUnionidResponse;
import com.dingtalk.api.response.OapiUserGetuserinfoResponse;
import com.taobao.api.ApiException;

import lombok.extern.slf4j.Slf4j;

/**
 * 企业内部应用免登
 * https://ding-doc.dingtalk.com/doc#/serverapi2/clotub
 * 用户管理
 * https://ding-doc.dingtalk.com/doc#/serverapi2/ege851
 */
@Slf4j
public class DingTalkAccountOperations extends DingTalkOperations {

	public DingTalkAccountOperations(DingTalkTemplate template) {
		super(template);
	}

	/**
	 * 1、企业内部应用免登录：通过免登授权码和access_token获取用户信息
	 * https://ding-doc.dingtalk.com/doc#/serverapi2/clotub
	 * 
	 * @param code    		免登授权码，参考上述“获取免登授权码”
	 * @param accessToken 	调用接口凭证
	 * @return the OapiUserGetuserinfoResponse
	 * @throws ApiException if Api request Exception
	 */
	public OapiUserGetuserinfoResponse getUserinfoBycode( String code, String accessToken) throws ApiException {
		DingTalkClient client = client(PREFIX + "/user/getuserinfo");
		OapiUserGetuserinfoRequest request = new OapiUserGetuserinfoRequest();
		request.setCode(code);
		request.setHttpMethod(METHOD_GET);
		return client.execute(request, accessToken);
	}
	
	
	/**
	 * 根据unionid获取userid
	 * https://open-doc.dingtalk.com/microapp/serverapi2/ege851#-5
	 * 
	 * @param unionid 员工在当前企业内的唯一标识，也称staffId。可由企业在创建时指定，并代表一定含义比如工号，创建后不可修改，企业内必须唯一。长度为1~64个字符，如果不传，服务器将自动生成一个userid。
	 * @param accessToken 	调用接口凭证
	 * @return the OapiUserGetUseridByUnionidResponse
	 * @throws ApiException if Api request Exception 
	 */
	public OapiUserGetUseridByUnionidResponse getUseridByUnionid( String unionid, String accessToken) throws ApiException {
		
		DingTalkClient client = client(PREFIX + "/user/getUseridByUnionid");
		OapiUserGetUseridByUnionidRequest request = new OapiUserGetUseridByUnionidRequest();
		request.setUnionid(unionid);
		request.setHttpMethod(METHOD_GET);
		
		return client.execute(request, accessToken);
	}
	
	/*
	 * 根据钉钉的userid拿取用户的详细信息(包括手机号，部门id，等)
	 * https://open-doc.dingtalk.com/microapp/serverapi2/ege851
	 * @param userid 用户ID
	 * @param accessToken 	调用接口凭证
	 * @return the OapiUserGetResponse
	 * @throws ApiException if Api request Exception 
	 */
	public OapiUserGetResponse getUserByUserid( String userid, String accessToken) throws ApiException {
		
		DingTalkClient client = client(PREFIX + "/user/get");
		OapiUserGetRequest request = new OapiUserGetRequest();
		request.setUserid(userid);
		request.setHttpMethod(METHOD_GET);
		
		return client.execute(request, accessToken);
	}

	
}
