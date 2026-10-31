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

/**
 */
public abstract class DingTalkOperations {

	public static final String PREFIX = "https://oapi.dingtalk.com";
	public static final String METHOD_GET = "GET";
	public static final String APPLICATION_JSON_VALUE = "application/json";
	public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

	public static final String DELIMITER = "&";
	public static final String SEPARATOR = "=";

	protected DingTalkTemplate template;

	public DingTalkOperations(DingTalkTemplate template) {
		this.template = template;
	}

	protected DingTalkClient client(String url) {
		return new DefaultDingTalkClient(url);
	}

}
