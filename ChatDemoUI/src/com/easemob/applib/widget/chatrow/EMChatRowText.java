package com.easemob.applib.widget.chatrow;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.easemob.applib.widget.MessageAdapter;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.activity.ContextMenu;
import com.easemob.chatuidemo.utils.SmileUtils;

public class EMChatRowText extends EMChatRowBase {
	
	private MessageAdapter adapter;

    public EMChatRowText(Context context, EMMessage message, final int position, ViewGroup parent, MessageAdapter adapter) {
		super(context, adapter);
		this.adapter = adapter;
		setupView(message, position, parent);
	}

	@Override
	public void setupView(EMMessage message, final int position, ViewGroup parent) {
		convertView = inflater.inflate(message.direct == EMMessage.Direct.RECEIVE ?
				R.layout.em_row_received_message : R.layout.em_row_sent_message, this);
		holder = new ViewHolder();
		convertView.setTag(holder);

		holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
		holder.staus_iv = (ImageView) convertView
				.findViewById(R.id.msg_status);
		holder.iv_avatar = (ImageView) convertView
				.findViewById(R.id.iv_userhead);
		// 这里是文字内容
		holder.tv = (TextView) convertView
				.findViewById(R.id.tv_chatcontent);
		holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
	}
	
	public void updateView(final EMMessage message, final int position, ViewGroup parent) {
		setAvatar(message, position, convertView, holder);
		updateAckDelivered(message, position, convertView, holder);
		setResendListener(message, position, convertView, holder);
		setOnBlackList(message, position, convertView, holder);
		
		handleTextMessage(message, holder, position);
	}

	/**
	 * 文本消息
	 * 
	 * @param message
	 * @param holder
	 * @param position
	 */
	private void handleTextMessage(EMMessage message, ViewHolder holder, final int position) {
		TextMessageBody txtBody = (TextMessageBody) message.getBody();
		Spannable span = SmileUtils.getSmiledText(context, txtBody.getMessage());
		// 设置内容
		holder.tv.setText(span, BufferType.SPANNABLE);
		// 设置长按事件监听
		holder.tv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				activity.startActivityForResult(
						(new Intent(context, ContextMenu.class)).putExtra("position", position).putExtra("type",
								EMMessage.Type.TXT.ordinal()), REQUEST_CODE_CONTEXT_MENU);
				return true;
			}
		});

		if (message.direct == EMMessage.Direct.SEND) {
			switch (message.status) {
			case SUCCESS: // 发送成功
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			case FAIL: // 发送失败
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.VISIBLE);
				break;
			case INPROGRESS: // 发送中
				holder.pb.setVisibility(View.VISIBLE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			default:
				// 发送消息
				sendMsgInBackground(message, holder);
			}
		}
	}

	@Override
	public void updateSendedView(EMMessage message, ViewHolder holder) {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onProgress(EMMessage message, ViewHolder holder, int progress,
			String status) {
		// TODO Auto-generated method stub
		
	}
}
