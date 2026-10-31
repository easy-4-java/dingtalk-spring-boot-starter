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

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.request.OapiV2UserGetRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.dingtalk.spring.boot.bean.ActionCardButton;
import com.dingtalk.spring.boot.bean.ActionCardMessage;
import com.dingtalk.spring.boot.bean.BaseMessage;
import com.dingtalk.spring.boot.bean.FeedCardMessage;
import com.dingtalk.spring.boot.bean.FeedCardMessageItem;
import com.dingtalk.spring.boot.bean.HideAvatarType;
import com.dingtalk.spring.boot.bean.LinkMessage;
import com.dingtalk.spring.boot.bean.MarkdownMessage;
import com.dingtalk.spring.boot.bean.TextMessage;
import com.dingtalk.spring.boot.property.DingTalkRobotProperties;
import com.taobao.api.ApiException;
import org.apache.commons.lang3.ArrayUtils;

/**
 */
public class DingTalkRobotOperations extends DingTalkOperations {

	public DingTalkRobotOperations(DingTalkTemplate template) {
		super(template);
	}

	protected String getWebhook(String corpId, String robotId, Long timestamp) {
		DingTalkRobotProperties poperties = template.getDingTalkConfigProvider().getDingTalkRobotProperties(corpId, robotId);
        StringBuilder serverUrl = new StringBuilder(PREFIX + "/robot/send?access_token=").append(poperties.getAccessToken());
        String sign =  template.getSign(poperties.getSecretToken(), timestamp);
        serverUrl.append("&timestamp=").append(timestamp).append("&sign=").append(sign);
        return serverUrl.toString();
    }

    public String getUserMobile(String access_token, String userid,  String lang) {
        try {
            DingTalkClient client = client(PREFIX + "/topapi/v2/user/get");
            OapiV2UserGetRequest req = new OapiV2UserGetRequest();
            req.setUserid(userid);
            req.setLanguage(lang);
            OapiV2UserGetResponse rsp = client.execute(req, access_token);
            System.out.println(rsp.getBody());
            return rsp.getResult().getMobile();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    OapiRobotSendRequest buidRequest(BaseMessage message){

		OapiRobotSendRequest request = new OapiRobotSendRequest();
		request.setMsgtype(message.getMsgtype().name());

		switch (message.getMsgtype()) {
			case actionCard:{
				ActionCardMessage msg = (ActionCardMessage) message;
				OapiRobotSendRequest.Actioncard card = new OapiRobotSendRequest.Actioncard();
				card.setTitle(msg.getTitle());
				card.setText(msg.getText());
				card.setHideAvatar(msg.getHideAvatar().getValue());
				card.setBtnOrientation(msg.getBtnOrientation().getValue());
				if (msg.isButtonView() && msg.getButtons().size() == 1) {
					card.setSingleTitle(msg.getButtons().get(0).getTitle());
					card.setSingleURL(msg.getButtons().get(0).getActionURL());
				} else {
					List<OapiRobotSendRequest.Btns> buttons = new ArrayList<>();
					for (ActionCardButton button : msg.getButtons()) {
						OapiRobotSendRequest.Btns item = new OapiRobotSendRequest.Btns();
						item.setTitle(button.getTitle());
						item.setActionURL(button.getActionURL());
						buttons.add(item);
					}
					card.setBtns(buttons);
				}
				request.setActionCard(card);
			};break;
			case feedCard:{
				FeedCardMessage msg = (FeedCardMessage) message;
				OapiRobotSendRequest.Feedcard card = new OapiRobotSendRequest.Feedcard();
				List<OapiRobotSendRequest.Links> links = new ArrayList<>();
				for (FeedCardMessageItem item : msg.getFeedCardItems()) {
					OapiRobotSendRequest.Links link = new OapiRobotSendRequest.Links();
					link.setTitle(item.getTitle());
					link.setMessageURL(item.getMessageURL());
					link.setPicURL(item.getPicURL());
					links.add(link);
				}
				card.setLinks(links);
				request.setFeedCard(card);
			};break;
			case link:{
				LinkMessage msg = (LinkMessage) message;
				OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
				link.setTitle(msg.getTitle());
				link.setText(msg.getText());
				link.setMessageUrl(msg.getMessageUrl());
				link.setPicUrl(msg.getPicUrl());
				request.setLink(link);
			};break;
			case markdown:{
				MarkdownMessage msg = (MarkdownMessage) message;
				OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
				markdown.setTitle(msg.getTitle());
				markdown.setText(msg.getText());
				request.setMarkdown(markdown);
				OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
				if (ArrayUtils.isNotEmpty(msg.getAtMobiles())) at.setAtMobiles(Arrays.asList(msg.getAtMobiles()));
				at.setIsAtAll(msg.getIsAtAll());
				request.setAt(at);
			};break;
			case text:{

				TextMessage msg = (TextMessage) message;

				OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
				text.setContent(msg.getContent());
				request.setText(text);
                OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
				if(ArrayUtils.isNotEmpty(msg.getAtMobiles())){
                    at.setAtMobiles(Arrays.asList(msg.getAtMobiles()));
                }
				at.setIsAtAll(msg.isAtAll());
				request.setAt(at);

			};break;
		}


		return request;
    }

    public OapiRobotSendResponse sendMessage(String corpId, String robotId, BaseMessage message) throws ApiException {
		return this.sendMessage(corpId, robotId, this.buidRequest(message));
	}

    public OapiRobotSendResponse sendMessage(String corpId, String robotId, OapiRobotSendRequest request) throws ApiException {
  		Long timestamp = System.currentTimeMillis();
		DingTalkClient client = client(this.getWebhook(corpId, robotId, timestamp));
  		request.setTimestamp(timestamp);
  		return client.execute(request);
  	}

    /*
     * 发送文本消息到钉钉
     */
    public OapiRobotSendResponse sendTextMessage(String corpId, String robotId, TextMessage message) throws ApiException {
		return this.sendMessage(corpId, robotId, this.buidRequest(message));
    }

    /*
     * 发送文本消息到钉钉
     */
    public OapiRobotSendResponse sendTextMessage(String corpId, String robotId, String content) throws ApiException {
        return this.sendMessage(corpId, robotId, new TextMessage(content));
    }

    /*
     * 发送文本消息到钉钉
     */
    public OapiRobotSendResponse sendTextMessage(String corpId, String robotId, String content, String[] atMobiles) throws ApiException {
        return this.sendMessage(corpId, robotId, new TextMessage(content, atMobiles));
    }

    /*
     * 发送文本消息到钉钉
     */
    public OapiRobotSendResponse sendTextMessage(String corpId, String robotId, String content, boolean isAtAll) throws ApiException {
        return this.sendMessage(corpId, robotId, new TextMessage(content, isAtAll));
    }

    /*
     * 发送Link消息到钉钉
     */
    public OapiRobotSendResponse sendLinkMessage(String corpId, String robotId, LinkMessage message) throws ApiException {
        return this.sendMessage(corpId, robotId, message);
    }

    /*
     * 发送Link消息到钉钉
     */
    public OapiRobotSendResponse sendLinkMessage(String corpId, String robotId, String title, String text, String messageUrl) throws ApiException {
        return this.sendMessage(corpId, robotId, new LinkMessage(title, text, messageUrl));
    }

    /*
     * 发送Link消息到钉钉
     */
    public OapiRobotSendResponse sendLinkMessage(String corpId, String robotId, String title, String text, String messageUrl, String picUrl) throws ApiException {
        return this.sendMessage(corpId, robotId, new LinkMessage(title, text, messageUrl, picUrl));
    }

    /*
     * 发送MarkDown消息到钉钉
     */
    public OapiRobotSendResponse sendMarkdownMessage(String corpId, String robotId, MarkdownMessage message) throws ApiException {
        return this.sendMessage(corpId, robotId, message);
    }

    /*
     * 发送MarkDown消息到钉钉
     */
    public OapiRobotSendResponse sendMarkdownMessage(String corpId, String robotId, String title, String text) throws ApiException {
        return this.sendMessage(corpId, robotId, new MarkdownMessage(title, text));
    }

    /*
     * 发送MarkDown消息到钉钉
     */
    public OapiRobotSendResponse sendMarkdownMessage(String corpId, String robotId, String title, String text, String[] atMobiles) throws ApiException {
        return this.sendMessage(corpId, robotId, new MarkdownMessage(title, text, atMobiles));
    }

    /*
     * 发送MarkDown消息到钉钉
     */
    public OapiRobotSendResponse sendMarkdownMessage(String corpId, String robotId, String title, String text, boolean isAtAll) throws ApiException {
        return this.sendMessage(corpId, robotId, new MarkdownMessage(title, text, isAtAll));
    }

    /*
     * 发送ActionCard消息到钉钉
     */
    public OapiRobotSendResponse sendActionCardMessage(String corpId, String robotId, ActionCardMessage message) throws ApiException {
        return this.sendMessage(corpId, robotId, message);
    }

    /*
     * 发送ActionCard消息到钉钉
     */
    public OapiRobotSendResponse sendActionCardMessage(String corpId, String robotId, String title, String text) throws ApiException {
        return this.sendMessage(corpId, robotId, new ActionCardMessage(title, text));
    }

    /*
     * 发送ActionCard消息到钉钉
     */
    public OapiRobotSendResponse sendActionCardMessage(String corpId, String robotId, String title, String text, HideAvatarType hideAvatar) throws ApiException {
        return this.sendMessage(corpId, robotId, new ActionCardMessage(title, text, hideAvatar));
    }

    /*
     * 发送ActionCard消息到钉钉
     */
    public OapiRobotSendResponse sendActionCardMessage(String corpId, String robotId, String title, String text, ActionCardButton button) throws ApiException {
        return this.sendMessage(corpId, robotId, new ActionCardMessage(title, text, button));
    }

    /*
     * 发送ActionCard消息到钉钉
     */
    public OapiRobotSendResponse sendActionCardMessage(String corpId, String robotId, String title, String text, HideAvatarType hideAvatar, ActionCardButton button) throws ApiException {
        return this.sendMessage(corpId, robotId, new ActionCardMessage(title, text, hideAvatar, button));
    }

    /*
     * 发送FeedCard消息到钉钉
     */
    public OapiRobotSendResponse sendFeedCardMessage(String corpId, String robotId, FeedCardMessage feedCardMessage) throws ApiException {
        return this.sendMessage(corpId, robotId, feedCardMessage);
    }

    /*
     * 发送FeedCard消息到钉钉
     */
    public OapiRobotSendResponse sendFeedCardMessage(String corpId, String robotId, List<FeedCardMessageItem> feedCardItems) throws ApiException {
        return this.sendMessage(corpId, robotId, new FeedCardMessage(feedCardItems));
    }

    public OapiRobotSendResponse sendMessageByUrl(String webhook, String secret, BaseMessage message) throws ApiException {
		return this.sendMessageByUrl(webhook, secret, this.buidRequest(message));
	}

    public OapiRobotSendResponse sendMessageByUrl(String webhook, String secret, OapiRobotSendRequest request) throws ApiException {
		Long timestamp = System.currentTimeMillis();

        String sign =  template.getSign(secret, timestamp);
        StringBuilder serverUrl = new StringBuilder(webhook).append("&timestamp=").append(timestamp).append("&sign=").append(sign);

		DingTalkClient client = client(serverUrl.toString());
		request.setTimestamp(timestamp);
		return client.execute(request);
	}

}
