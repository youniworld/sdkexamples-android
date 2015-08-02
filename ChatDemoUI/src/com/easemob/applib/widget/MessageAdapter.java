/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.applib.widget;

import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.applib.Constant;
import com.easemob.applib.widget.chatrow.EMChatRow;
import com.easemob.applib.widget.chatrow.EMChatRowBase;
import com.easemob.applib.widget.chatrow.EMChatRowCall;
import com.easemob.applib.widget.chatrow.EMChatRowFile;
import com.easemob.applib.widget.chatrow.EMChatRowImage;
import com.easemob.applib.widget.chatrow.EMChatRowLocation;
import com.easemob.applib.widget.chatrow.EMChatRowText;
import com.easemob.applib.widget.chatrow.EMChatRowVideo;
import com.easemob.applib.widget.chatrow.EMChatRowVoice;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chatuidemo.R;
import com.easemob.util.DateUtils;

public class MessageAdapter extends BaseAdapter{

	private final static String TAG = "msg";

	public static final String IMAGE_DIR = "chat/image/";
	public static final String VOICE_DIR = "chat/audio/";
	public static final String VIDEO_DIR = "chat/video";

	private Context context;
	
	private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
	private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
	private static final int HANDLER_MESSAGE_SEEK_TO = 2;

	private static final int MESSAGE_TYPE_RECV_TXT = 0;
	private static final int MESSAGE_TYPE_SENT_TXT = 1;
	private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
	private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
	private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
	private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
	private static final int MESSAGE_TYPE_SENT_VOICE = 6;
	private static final int MESSAGE_TYPE_RECV_VOICE = 7;
	private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
	private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
	private static final int MESSAGE_TYPE_SENT_FILE = 10;
	private static final int MESSAGE_TYPE_RECV_FILE = 11;
	private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 12;
	private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 13;
	private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 14;
	private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 15;
	
	public int itemTypeCount; 
	
	// reference to conversation object in chatsdk
	private EMConversation conversation;
	EMMessage[] messages = null;
	
//	Map<Integer, View> views = new HashMap<Integer, View>();
	ListView listView;

    private String toChatUsername;

	public MessageAdapter(Context context, String username, int chatType, ListView listView) {
		this.context = context;
		this.listView = listView;
		toChatUsername = username;
		this.conversation = EMChatManager.getInstance().getConversation(username);
	}
	
	Handler handler = new Handler() {
		private void refreshList() {
			// UI线程不能直接使用conversation.getAllMessages()
			// 否则在UI刷新过程中，如果收到新的消息，会导致并发问题
			messages = (EMMessage[]) conversation.getAllMessages().toArray(new EMMessage[conversation.getAllMessages().size()]);
			for (int i = 0; i < messages.length; i++) {
				// getMessage will set message as read status
				conversation.getMessage(i);
			}
			notifyDataSetChanged();
		}
		
		@Override
		public void handleMessage(android.os.Message message) {
			switch (message.what) {
			case HANDLER_MESSAGE_REFRESH_LIST:
				refreshList();
				break;
			case HANDLER_MESSAGE_SELECT_LAST:
				if (messages.length > 0) {
					listView.setSelection(messages.length - 1);
				}
				break;
			case HANDLER_MESSAGE_SEEK_TO:
				int position = message.arg1;
				listView.setSelection(position);
				break;
			default:
				break;
			}
		}
	};


	/**
	 * 获取item数
	 */
	public int getCount() {
		return messages == null ? 0 : messages.length;
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
			return;
		}
		android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
		handler.sendMessage(msg);
	}
	
	/**
	 * 刷新页面, 选择最后一个
	 */
	public void refreshSelectLast() {
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_SELECT_LAST));
	}
	
	/**
	 * 刷新页面, 选择Position
	 */
	public void refreshSeekTo(int position) {
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
		android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
		msg.arg1 = position;
		handler.sendMessage(msg);
	}

	public EMMessage getItem(int position) {
		if (messages != null && position < messages.length) {
			return messages[position];
		}
		return null;
	}

	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * 获取item类型数
	 */
	public int getViewTypeCount() {
        return 16;
    }
	

	/**
	 * 获取item类型
	 */
	public int getItemViewType(int position) {
		EMMessage message = getItem(position); 
		if (message == null) {
			return -1;
		}
		if (message.getType() == EMMessage.Type.TXT) {
			if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false))
			    return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
			else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false))
			    return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
		}
		if (message.getType() == EMMessage.Type.IMAGE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;

		}
		if (message.getType() == EMMessage.Type.LOCATION) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;
		}
		if (message.getType() == EMMessage.Type.VOICE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
		}
		if (message.getType() == EMMessage.Type.VIDEO) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;
		}
		if (message.getType() == EMMessage.Type.FILE) {
			return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SENT_FILE;
		}

		return -1;// invalid
	}

	private EMChatRowBase createViewByMessage(EMMessage message, int position, ViewGroup parent) {
		switch (message.getType()) {
		case LOCATION:
			return new EMChatRowLocation(context, message, position, parent, this);
		case IMAGE:
			return new EMChatRowImage(context, message, position, parent, this);
		case VOICE:
			return new EMChatRowVoice(context, message, position, parent, this);
		case VIDEO:
			return new EMChatRowVideo(context, message, position, parent, this);
		case FILE:
			return new EMChatRowFile(context, message, position, parent, this);
		default:
			// 语音通话,  视频通话
			if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) ||
				message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false))
				return new EMChatRowCall(context, message, position, parent, this);
			else
				return new EMChatRowText(context, message, position, parent, this);
		}
	}

	@SuppressLint("NewApi")
	public View getView(final int position, View convertView, ViewGroup parent) {
		final EMMessage message = getItem(position);
		if(convertView == null){
			convertView = EMChatRow.createChatRow(context, message, position, this);
		}
		((EMChatRow)convertView).setUpView();
		
		
		if (convertView == null) {
		    
			convertView = createViewByMessage(message, position, parent);
		} 
		
		((EMChatRowBase)convertView).updateView(message, position, parent);

		try {
			TextView timestamp = (TextView) convertView
					.findViewById(R.id.timestamp);

			if (position == 0) {
				timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
				timestamp.setVisibility(View.VISIBLE);
			} else {
				// 两条消息时间离得如果稍长，显示时间
				EMMessage prevMessage = getItem(position - 1);
				if (prevMessage != null
						&& DateUtils.isCloseEnough(message.getMsgTime(),
								prevMessage.getMsgTime())) {
					timestamp.setVisibility(View.GONE);
				} else {
					timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
					timestamp.setVisibility(View.VISIBLE);
				}
			}
		} catch (Exception e) {
		}
		return convertView;
	}

//	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (data == null) {
//			return false;
//		}
//		int position = data.getIntExtra("position", -1);
//		if (position == -1) {
//			return false;
//		}
//		
//		if (requestCode == EMChatMessageList.REQUEST_CODE_CONTEXT_MENU) {
//			
////			MessageAdapter adapter = messageList.getAdapter();
////			int position = data.getIntExtra("position", -1);
////			if (position == -1) {
////				return;
////			}
//			EMMessage message = (EMMessage)getItem(position);
//			
//			switch (resultCode) {
//			case EMChatMessageList.RESULT_CODE_COPY: // 复制消息
//				@SuppressWarnings("deprecation")
//				ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//				clipboard.setText(((TextMessageBody) message.getBody()).getMessage());
//				return true;
//			case EMChatMessageList.RESULT_CODE_DELETE: // 删除消息
//				conversation.removeMessage(message.getMsgId());
//				refreshSeekTo(position > 0 ? position - 1 : 0);
//				return true;
//
//			case EMChatMessageList.RESULT_CODE_FORWARD: // 转发消息
//				// TODO, EMWidget
//				EMMessage forwardMsg = (EMMessage) getItem(data.getIntExtra("position", 0));
//				Intent intent = new Intent(context, ForwardMessageActivity.class);
//				intent.putExtra("forward_msg_id", forwardMsg.getMsgId());
//				((Activity)context).startActivity(intent);
//				return true;
//			default:
//				break;
//			}
//		}		
//		return false;
//	}

	public String getToChatUsername(){
	    return toChatUsername;
	}
}
